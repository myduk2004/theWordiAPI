package theWordI.backend.api;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import theWordI.backend.domain.bible.dto.BibleVerseCreateRequest;

import theWordI.backend.domain.bible.dto.BibleVerseUpdateRequest;
import theWordI.backend.domain.bible.entity.BibleVerse;
import theWordI.backend.domain.bible.service.BibleService;
import theWordI.backend.domain.user.auth.CustomUserPrincipal;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//통합테스트
@SpringBootTest // 모든 빈을 로드 (통합 테스트)
@AutoConfigureMockMvc // MockMvc를 사용할 수 있게 자동 설정
//@Transactional // 테스트 후 DB 데이터를 롤백하여 다음 테스트에 영향을 주지 않음
class BibleTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Autowired
    BibleService bibleService;

    @Test
    @DisplayName("성경 구절 등록")
    void 성경구절등록() throws Exception {


        BibleVerseCreateRequest request = new BibleVerseCreateRequest();
        request.setVersionId("KJVKO");
        request.setBookId(1);
        request.setChapter(1);
        request.setVerse(1);
        request.setText("태초에 하나님께서 하늘과 땅을 창조하셨느니라.");


        mockMvc.perform(post("/api/bible/verse")
                        .with(user(mockUser()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("성경 구절 삭제")
    void 성경구절삭제() throws Exception {
        Long verseId  = 20L;
        mockMvc.perform(delete("/api/bible/verse/{verseId}", verseId)
                .with(user(mockUser()))
                .with(csrf())
        ).andExpect(status().isNoContent()); //204
    }


    @Test
    @DisplayName("성경 구절 조회:장 전체")
    void 성경구절조회_챕터() throws Exception
    {
        mockMvc.perform(get("/api/bible/verses")
                        .with(user(mockUser()))
                        .param("versionId", "KSKJB")
                        .param("bookId", "1")
                        .param("chapter", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").isNotEmpty());


    }

    @Test
    @DisplayName("성경 구절 조회:범위")
    void 성경구절조회_범위() throws Exception
    {
        mockMvc.perform(get("/api/bible/verses/range")
                        .with(user(mockUser()))
                        .param("versionId", "KSKJB")
                        .param("bookId", "1")
                        .param("chapter", "1")
                        .param("startVerse", "1")
                        .param("endVerse", "")

                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }


    @Test
    @Transactional
    @Rollback(false)
    void 성경구절_갱신() throws Exception
    {
        BibleVerseUpdateRequest req = new BibleVerseUpdateRequest();
        req.setVerseId(10L);
        req.setText("그리고 하나님께서 말씀하시기를, \"물들의 한가운데 하나의 궁창이 있으라. 그리하여 그것은 물들에서 물들을 나누라.\"하셨느니라.");

        mockMvc.perform(put("/api/bible/verse")
                        .with(user(mockUser()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                 )
                .andDo(print())
                .andExpect(status().isOk());
    }


    private CustomUserPrincipal mockUser()
    {
        return new CustomUserPrincipal(
                1L,
                "sh0908",
                "sh0908",
                "$2a$10$kvCllsN4invFdViFfX0xoeu74lQFXrHvOaQV77/Rs.NxLz3yEtpqi",
                "USER",
                Map.of()
        );
    }

}
