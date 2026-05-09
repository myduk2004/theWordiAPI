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

        plan.update(dto.getVersionId(), dto.getTitle(), dto.getStartDt());

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


        //7. 종 book 66권을 다 읽었을 경우 계획을 완료상태로 update
        if (bookStatus == PlanStatus.COMPLETED)
        {
            plan.updateStatus(plan.getBookCount() + 1);
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
    public void deletePlanBookLogById(Long planId, Long planBookId, Long planLogId)
    {
        Long userId = SecurityUtil.getUserId();

        //1. 존재여부체크
        ReadingPlan plan = repo.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("데이터가 존재하지 않습니다."));

        ReadingPlanBook planBook = repo_book.findById(planBookId)
                .orElseThrow(() -> new EntityNotFoundException("데이터가 존재하지 않습니다."));

        ReadingPlanBookLog planBookLog = repo_book_log.findById(planLogId)
                .orElseThrow(() -> new EntityNotFoundException("데이터가 존재하지 않습니다."));


        if (!plan.isOwner() || !planBook.isOwner() || !planBookLog.isOwner())  throw new AccessDeniedException("접근권한이 없습니다.");
        if (plan.getStatus().equals(PlanStatus.WAITING) ||
                plan.getStatus().equals(PlanStatus.ABANDONED))  throw new AccessDeniedException("진행/완료된 계획만 삭제가능합니다.");


        int start = planBookLog.getStartChapter();
        int end = planBookLog.getEndChapter();
        int readChapterCnt = (end - start)+1;

        //read_log 삭제(n개)
        repo_read_log.deleteByUserIdAndPlanLogId(userId, planLogId);

        //planBookLog 삭제(1개)
        repo_book_log.deleteById(planLogId);

        //planBook update(1개)
        planBook.updatePreStatus(readChapterCnt);

        //plan update(1개)
        if (plan.getStatus() == PlanStatus.COMPLETED)
        {
            plan.updatePreStatus();
        }

    }
}
