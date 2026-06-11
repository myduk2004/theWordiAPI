package theWordI.backend.domain.headerVerse.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import theWordI.backend.domain.user.auth.CustomUserPrincipal;

import java.sql.SQLOutput;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class HeaderVerseServiceTest {

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
    @DisplayName("헤더 성경구절 조회")
    public void getHeaderVerses() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/headerVerse")
                        .with(user(mockUser()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andReturn();

        System.out.println("status="+ mvcResult.getResponse().getStatus());
        System.out.println("body="+ mvcResult.getResponse().getContentAsString());
        //.andExpect(status().isOk())


    }
}