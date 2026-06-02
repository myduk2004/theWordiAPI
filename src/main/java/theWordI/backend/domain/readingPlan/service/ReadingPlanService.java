package theWordI.backend.domain.readingPlan.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import theWordI.backend.domain.bible.entity.BibleVersion;
import theWordI.backend.domain.bible.service.BibleService;
import theWordI.backend.domain.readingPlan.dto.*;
import theWordI.backend.domain.readingPlan.entity.*;
import theWordI.backend.domain.readingPlan.repository.ReadLogRepository;
import theWordI.backend.domain.readingPlan.repository.ReadingPlanBookLogRepository;
import theWordI.backend.domain.readingPlan.repository.ReadingPlanBookRepository;
import theWordI.backend.domain.readingPlan.repository.ReadingPlanRepository;
import theWordI.backend.util.SecurityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@Transactional
public class ReadingPlanService {

    private final ReadingPlanRepository repo;
    private final ReadingPlanBookRepository repo_book;
    private final ReadingPlanBookLogRepository repo_book_log;
    private final ReadLogRepository repo_read_log;

    private final BibleService bibleService;

    /*
    * 통독계획 조회
    * */
    public List<ReadingPlanResponse> getPlans()
    {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "readCount"));
        List<ReadingPlan> plans = repo.findByUserId(SecurityUtil.getUserId(), pageRequest);
        return plans.stream()
                .map(ReadingPlanResponse::from)
                .toList();
    }


    /*
    통독계획 등록
     */
    public Long savePlan(ReadingPlanCreateRequest dto)
    {
        int maxReadCount = repo.findMaxReadCount(SecurityUtil.getUserId())+1;
        ReadingPlan newPlan = dto.toEntity(maxReadCount);
        repo.save(newPlan);
        return newPlan.getPlanId();
    }

    /*
    통독계획 수정
    */
    public ReadingPlan updatePlan(Long planId , ReadingPlanUpdateRequest dto)
    {
        ReadingPlan plan = repo.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("데이터가 존재하지 않습니다."));

        if (!plan.isOwner()) throw new AccessDeniedException("권한이 없습니다.");

        plan.update(dto.getTitle());

        return plan;
    }

    /* 통독계획 삭제
    * plan을 삭제 시 read_log, book_log, book 먼저 삭제 후 plan 삭제한다.
    * */
    public void deletePlan(Long planId)
    {
        Long userId = SecurityUtil.getUserId();
        //1. 존재여부체크
        ReadingPlan readingPlan = repo.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("데이터가 존재하지 않습니다."));

        //2. 작성자 체크
        if (!readingPlan.isOwner())
        {
            throw new AccessDeniedException("접근권한이 없습니다.");
        }

        //3. repo_read_log 삭제
        repo_read_log.deleteByUserIdAndPlanId(userId, planId);

        //4. repo_book_log 삭제
        repo_book_log.deleteByUserIdAndPlanId(userId, planId);

        //5. reading_plan_book 삭제
        repo_book.deleteByUserIdAndPlanId(userId, planId);

        //6. reading_plan 삭제
        repo.delete(readingPlan);

        //7. readCount 차감
        repo.decrementReadCounts(userId, planId);
    }

    /*
     * 책계획 조회
     * */
    public List<ReadingPlanBookResponse> getPlansBooks(Long planId, String versionId)
    {

        if (versionId == "" || versionId.isBlank()) {
            List<BibleVersion> versions = bibleService.getVersionAll().stream()
                    .filter(v -> Boolean.TRUE.equals(v.getIsStandard()))
                    .toList();

            if (versions.isEmpty())
            {
                throw new IllegalArgumentException("기본 성경 버전을 찾을 수 없습니다.");
            }
            versionId  = versions.getFirst().getVersionId();
        }

        return repo_book.findPlanBooks(SecurityUtil.getUserId(), planId, versionId);
    }

    /*
    책계획 로그 조회
     */
    public List<ReadingPlanBookLogResponse> getPlansBookLogs(Long planId, Long planBookId) {
        List<ReadingPlanBookLog> logs = repo_book_log.findByUserIdAndPlanIdAndPlanBookIdOrderByStartChapter(SecurityUtil.getUserId(), planId, planBookId);
        return logs.stream().map(ReadingPlanBookLogResponse::from).toList();
    }

    /*
    * 책게획 등록
    * */
    public Long savePlanBook(Long planId, ReadingPlanBookCreateRequest dto) {

        Long userId = SecurityUtil.getUserId();
        ReadingPlan plan = repo.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("계획 데이터가 존재하지 않습니다."));

        int start = dto.getStartChapter();
        int end = dto.getEndChapter();
        int total = BookInfo.getTotalChapter(dto.getBookId());
        int addedCount = (end - start) + 1;
        ReadingPlanBook planBook;
        PlanStatus bookStatus = PlanStatus.PROCEEDING;

        //1. 기존 범위검사
        if (start > end) throw new IllegalArgumentException("시작 장이 종료 장보다 클 수 없습니다.");
        if (end > total) throw new IllegalArgumentException("책의 전체 범위를 벗어났습니다.");

        if (dto.getPlanBookId() == null)
        {
            //[Create] 신규 등록일 시  
            //2. plan_book정보 등록
            repo_book.findByUserIdAndPlanIdAndBookId(userId, planId, dto.getBookId())
                    .ifPresent(
                            book -> {
                                throw new IllegalArgumentException("이미 읽기 시작한 책입니다.");
                            }
                    );

            bookStatus =(addedCount >= total)?PlanStatus.COMPLETED:PlanStatus.PROCEEDING;
            planBook = ReadingPlanBook.create(planId, dto,addedCount, bookStatus);

            repo_book.save(planBook);


        } else {

            //[Add] 기존 기록에 추가 시 
            //2. plan_book정보 조회
            planBook = repo_book.findById(dto.getPlanBookId())
                    .orElseThrow(() -> new EntityNotFoundException("데이터가 존재하지 않습니다."));

            if (!planBook.isOwner())  throw new AccessDeniedException("접근권한이 없습니다.");

            //3. DB로그 중복 체크
            boolean alreadyRead = repo_read_log.existsByUserIdAndPlanIdAndBookIdAndChapterNumBetween(userId,
                    planBook.getPlanId(), planBook.getBookId(), start, end);

            if (alreadyRead)
            {
                throw new IllegalArgumentException("해당 범위 내에 이미 기록된 읽기 로그가 존재합니다.");
            }

            int updatedTotalRead = planBook.getReadChaptersCnt() + addedCount;
            bookStatus = (updatedTotalRead >= total )? PlanStatus.COMPLETED: planBook.getStatus();

            planBook.update(bookStatus, updatedTotalRead,null);
        }

        //4. book_log등록
        ReadingPlanBookLog planBookLog = repo_book_log.save(ReadingPlanBookLog.create(dto, planBook.getPlanId(), planBook.getPlanBookId()));

        //5. read_log등록
        List<ReadLog> readLogs = new ArrayList<>();
        for (int i= start; i <=end; i++) {
            readLogs.add(ReadLog.create(planBook.getPlanId(),
                    planBook.getPlanBookId(),
                    planBookLog.getPlanLogId(),
                    planBook.getBookId(),
                    i));
        }
        repo_read_log.saveAll(readLogs);


        
        /*
        case 1: 첫 book등록 ,   등록하면서 chapter 다 안 읽은 경우
        plan.status = proceeding, plan.updDt = now

        case 2: 첫 book등록, 등록하면서 chapter 다 읽은 경우
        plan.status = proceeding, plan.bookCount = bookCount + 1 , plan.updDt = now

        case 3: 이미 book 등록 , 추가하면서 chapter 다 안읽은 경우
        plan update 할 계획 없음

        case 4: 이미 book 등록, 추가하면서 chapter 다 읽은 경우
        plan.status = complete, plan.bookCount = bookCount + 1 , plan.endDt = now , plan.updDt = now
         */
        if (dto.getPlanBookId() == null)
        {
            plan.updateProceedingPlan(bookStatus);
        }
        else
        {
            if (bookStatus == PlanStatus.COMPLETED)
            {
                plan.updateCompletePlan();
            }
        }


        return planBook.getPlanBookId();
    }


    /*
     * 읽은 책 내역 삭제 (bookId별로: 북 삭제)
     * */
    public void deletePlanBook(Long planId, Long planBookId)
    {
        Long userId = SecurityUtil.getUserId();

        //1. 존재여부체크
        ReadingPlan plan = repo.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("데이터가 존재하지 않습니다."));

        ReadingPlanBook planBook = repo_book.findById(planBookId)
                .orElseThrow(() -> new EntityNotFoundException("데이터가 존재하지 않습니다."));

        if (!plan.isOwner() || !planBook.isOwner())  throw new AccessDeniedException("접근권한이 없습니다.");
        if (plan.getStatus().equals(PlanStatus.WAITING) ||
                plan.getStatus().equals(PlanStatus.ABANDONED))  throw new AccessDeniedException("진행/완료된 계획만 삭제가능합니다.");


        //3. repo_read_log 삭제(n개)
        repo_read_log.deleteByUserIdAndPlanIdAndPlanBookId(userId, planId, planBookId);

        //4. planBookLog 삭제(n개)
        repo_book_log.deleteByUserIdAndPlanIdAndPlanBookId(userId, planId, planBookId);

        //5. reading_plan_book 삭제
        repo_book.delete(planBook);

        //plan update(1개)
        if (plan.getStatus() == PlanStatus.COMPLETED)
        {
            plan.updatePreStatus();
        }
    }


    /*
     * 읽은 책 내역 삭제 (Id별 삭제)
     * */
    public void deletePlanBookLogById(Long planId, Long planBookId, List<Long> planLogIds)
    {

        if (planLogIds == null || planLogIds.isEmpty())
        {
            return ; //삭제할 ID가 없으면 즉시 종료
        }
        Long userId = SecurityUtil.getUserId();

        //1. 부모 엔티티 존재 여부 및 권한 체크 (반복문 밖에서 딱 1번만 수행)
        ReadingPlan plan = repo.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("데이터가 존재하지 않습니다."));

        ReadingPlanBook planBook = repo_book.findById(planBookId)
                .orElseThrow(() -> new EntityNotFoundException("데이터가 존재하지 않습니다."));

        if (!plan.isOwner() || !planBook.isOwner())  throw new AccessDeniedException("접근권한이 없습니다.");

        if (plan.getStatus().equals(PlanStatus.WAITING) ||
                plan.getStatus().equals(PlanStatus.ABANDONED))  throw new AccessDeniedException("진행/완료된 계획만 삭제가능합니다.");


        // 2. 삭제할 로그들을 In 절로 한 번에 조회
        List<ReadingPlanBookLog> planBookLogs = repo_book_log.findAllById(planLogIds);

        //요청된 Id 개수와 조회된 데이터 개수가 다르면 데이터 유식/오류가 있는것
        if (planLogIds.size() != planBookLogs.size())
        {
            throw new EntityNotFoundException("삭제할 데이터 중 존재하지 않는 내역이 포함되어 있습니다.");
        }

        int totalDeletedChapters = 0;

        // 3. 메모리 상에서 검증 및 총 삭제 장수(Chapters) 계산
        for (ReadingPlanBookLog planBookLog : planBookLogs) {
            // 각 로그의 소유권 검증
            if (!planBookLog.isOwner())
            {
                throw new AccessDeniedException("접근 권한이 없습니다.");
            }


            int start = planBookLog.getStartChapter();
            int end = planBookLog.getEndChapter();
            int readChapterCnt = (end - start)+1;
            if (readChapterCnt < 1) throw new IllegalArgumentException("삭제할 데이터 중 잘못된 값이 존재합니다.");

            totalDeletedChapters += readChapterCnt;

        }

        // 4. DB 일괄 삭제 (Bulk Delete) 실행
        // 4-1. read_log 테이블 일괄 삭제 (In 절 활용 추천)
        repo_read_log.deleteByUserIdAndPlanLogIdIn(userId, planLogIds);

        // 4-2. planBookLog 테이블 일괄 삭제
        repo_book_log.deleteAllByIdInBatch(planLogIds);


        // 5. 부모 엔티티(PlanBook, Plan) 상태 업데이트
        //planBook update(n개) : planBookLog에서 삭제한 장수만큼 PlanBook에 읽은 장 수 update
        int updatedCnt = planBook.getReadChaptersCnt() - totalDeletedChapters;
        if (updatedCnt < 0) throw new IllegalArgumentException("삭제할 데이터 중 잘못된 값이 존재합니다. 관리자에게 문의해주세요.");

        if (updatedCnt == 0)
        {
            //읽은 총 장수만큼 삭제할 경우 repoBook도 삭제한다.
            repo_book.deleteById(planBookId);
        }
        else
        {
            planBook.updatePreStatus(updatedCnt);
        }

        //plan update(1개) : Plan이 완료된 경우 '진행'으로 변경
        if (plan.getStatus() == PlanStatus.COMPLETED)
        {
            plan.updatePreStatus();
        }
    }
}
