package theWordI.backend.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import theWordI.backend.domain.bible.dto.BibleVerseCreateRequest;
import theWordI.backend.domain.bible.service.BibleService;
import theWordI.backend.domain.user.auth.CustomUserPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//컨트롤러만 테스트
@WebMvcTest(BibleController.class)
class BibleControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BibleService bibleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("성경 구절 등록 성공")
    void addVerse_success() throws Exception {

        //given
        given(bibleService.createVerse(any())).willReturn(1L);

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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.verseId").value(1L));
    }


    private CustomUserPrincipal mockUser()
    {
        return new CustomUserPrincipal(
                1L,
                null,
                null,
                null,
                "USER",
                Map.of()
        );
    }

}
