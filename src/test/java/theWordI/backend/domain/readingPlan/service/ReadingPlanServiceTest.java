package theWordI.backend.domain.readingPlan.service;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import theWordI.backend.domain.readingPlan.dto.ReadingPlanBookCreateRequest;
import theWordI.backend.domain.readingPlan.dto.ReadingPlanCreateRequest;

import theWordI.backend.domain.readingPlan.dto.ReadingPlanUpdateRequest;
import theWordI.backend.domain.readingPlan.entity.PlanStatus;
import theWordI.backend.domain.user.auth.CustomUserPrincipal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest         // 모든 빈을 로드 (통합 테스트)
@AutoConfigureMockMvc   //MockMvc를 사용할 수 있게 자동 설정
class ReadingPlanServiceTest {

    @Autowired
    MockMvc mockMvc;


    @Autowired
    ObjectMapper objectMapper;

    private CustomUserPrincipal mockUser()
    {
        return new CustomUserPrincipal(
                5L,
                "sh0908",
                "sh0908",
                "$2a$10$kvCllsN4invFdViFfX0xoeu74lQFXrHvOaQV77/Rs.NxLz3yEtpqi",
                "USER",
                Map.of()
        );
    }

    @Test
    @DisplayName("plan 등록")
    public void savePlan() throws Exception
    {
        ReadingPlanCreateRequest dto = new ReadingPlanCreateRequest();
        dto.setVersionId("KSKJB");
        dto.setTitle("테스트통독_4");
        dto.setStartDt(LocalDate.now());

        mockMvc.perform(post("/reading-plans")
                .with(user(mockUser()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

    }


//    @Test
//    @DisplayName("통독계획 삭제")
//    public void deletePlan() throws Exception
//    {
//        //case1 : 통독계획만 등록 됬을 시 삭제 (OK)
//        //case2 : 통독계획 + 책등록 됬을 시 삭제 (추후테스트)
//        mockMvc.perform(delete("/reading-plans/8")
//                        .with(user(mockUser()))
//                        .with(csrf()))
//                        .andExpect(status().isNoContent());  //204
//
//    }

//
//    @Test
//    @DisplayName("통독 책 등록")
//    public void savePlanBook() throws Exception
//    {
//
//        //case1: planBookId가 없는경우s
////        ReadingPlanBookCreateRequest dto = new ReadingPlanBookCreateRequest();
////        dto.setPlanBookId(null);
////        dto.setBookId(1);
////        dto.setStartChapter(1);
////        dto.setEndChapter(1);
////        dto.setStartDt(LocalDateTime.now());
////        dto.setEndDt(LocalDateTime.now());
//
////        ReadingPlanBookCreateRequest dto = new ReadingPlanBookCreateRequest();
////        dto.setBookId(1);
////        dto.setStartChapter(1);
////        dto.setEndChapter(3);
////        dto.setStartDt(LocalDateTime.now());
////        dto.setEndDt(LocalDateTime.now());
//
//
//        //case2: planBookId가 있는경우
//        ReadingPlanBookCreateRequest dto = new ReadingPlanBookCreateRequest();
//        dto.setPlanBookId(16L);
//        dto.setBookId(1);
//        dto.setStartChapter(50);
//        dto.setEndChapter(50);
//        dto.setStartDt(LocalDate.now());
//        dto.setEndDt(LocalDate.now());
//
//        mockMvc.perform(post("/reading-plans/6/books")
//                .with(user(mockUser()))
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(dto))
//        ).andExpect(status().isCreated());
//    }

//
//    @Test
//    @DisplayName("통독 계획 수정")
//    public void updatePlan()  throws Exception
//    {
//
//        ReadingPlanUpdateRequest dto = new ReadingPlanUpdateRequest();
//        dto.setVersionId("KKJV");
//        dto.setTitle("테스트");
//        dto.setStartDt(LocalDate.now());
//
//        mockMvc.perform(put("/reading-plans/6")
//                .with(user(mockUser()))
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(dto))
//        ).andExpect(status().isOk());
//    }

    @Test
    @DisplayName("통독 책 로그 건별 삭제")
    public void deletePlanBookLog()
    {

    }


    //통독 책 삭제?(사용안하는걸로)
//    @Test
//    @DisplayName("통독 책 로그 한권 삭제")
//    public void deletePlanBook() throws Exception
//    {
//        Long planId = 6L;
//        Long planBookId = 16L;
//
//        mockMvc.perform(delete("/reading-plans/{planId}/books/{planBookId}", planId, planBookId)
//                .with(user(mockUser()))
//                .with(csrf())
//        ).andExpect(status().isNoContent());
//    }

}