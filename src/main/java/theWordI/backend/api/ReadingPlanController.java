package theWordI.backend.api;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import theWordI.backend.domain.readingPlan.dto.*;
import theWordI.backend.domain.readingPlan.entity.ReadingPlan;
import theWordI.backend.domain.readingPlan.service.ReadingPlanService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reading-plans")
public class ReadingPlanController {

    protected final ReadingPlanService plan_svc;

    @GetMapping("")
    public ResponseEntity<List<ReadingPlanResponse>> getPlan()
    {
        List<ReadingPlanResponse> plan = plan_svc.getPlans();
        return ResponseEntity.ok(plan);
    }

    //통독 계획 등록
    @PostMapping("")
    public ResponseEntity<Map<String, Long>> savePlan(@Valid @RequestBody ReadingPlanCreateRequest dto)
    {
        Long planId = plan_svc.savePlan(dto);
        Map<String, Long> responseBody = Collections.singletonMap("planId", planId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseBody);
    }

    //통독 계획 수정
    @PutMapping("/{planId}")
    public ResponseEntity<ReadingPlan> updatePlan(@PathVariable Long planId, @RequestBody @Valid ReadingPlanUpdateRequest dto)
    {
        ReadingPlan readingPlan = plan_svc.updatePlan(planId, dto);
        return ResponseEntity.ok(readingPlan);
    }

    //통독 계획 삭제
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{planId}")
    public void deletePlan(@PathVariable Long planId)
    {
        plan_svc.deletePlan(planId);
    }

    //읽은 책 등록
    @PostMapping("/{planId}/book")
    public ResponseEntity<Map<String, Long>> savePlanBook(@PathVariable Long planId, @RequestBody ReadingPlanBookCreateRequest dto)
    {
        Long planBookId = plan_svc.savePlanBook(planId, dto);
        Map<String, Long> responseBody = Collections.singletonMap("planBookId", planBookId);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }


    @GetMapping("/{planId}/books")
    public List<ReadingPlanBookResponse> getPlanBooks(@PathVariable Long planId, @RequestParam String versionId)
    {
        return  plan_svc.getPlansBooks(planId, versionId);
    }

    //읽은 책 목록
    @GetMapping("/{planId}/{planBookId}/logs")
    public List<ReadingPlanBookLogResponse> getPlanBookLogs(@PathVariable Long planId, @PathVariable Long planBookId)
    {
        return  plan_svc.getPlansBookLogs(planId, planBookId);
    }

    //읽은 책 로그 삭제 (Id별 삭제)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{planId}/{planBookId}/logs")
    public void deletePlanBookLogById(@PathVariable Long planId, @PathVariable Long planBookId, @RequestParam("planLogIds") List<Long> planLogIds)
    {
        plan_svc.deletePlanBookLogById(planId, planBookId, planLogIds);
    }


    //읽은 책 로그 삭제 (bookId별로: 북 삭제)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{planId}/books/{planBookId}")
    public void deletePlanBook(@PathVariable Long planId, @PathVariable Long planBookId)
    {
        plan_svc.deletePlanBook(planId, planBookId);
    }

}
