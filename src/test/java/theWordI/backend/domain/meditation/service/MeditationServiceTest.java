package theWordI.backend.domain.meditation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import theWordI.backend.domain.meditation.dto.MeditationCreateRequest;
import theWordI.backend.domain.meditation.dto.MeditationListResponse;
import theWordI.backend.domain.meditation.dto.MeditationListResponse2;
import theWordI.backend.domain.meditation.dto.MeditationUpdateRequest;
import theWordI.backend.domain.meditation.entity.Meditation;
import theWordI.backend.domain.meditation.repository.MeditationRepository;
import theWordI.backend.domain.user.auth.CustomUserPrincipal;

import java.time.LocalDate;

import java.util.Collections;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class MeditationServiceTest {

    @Autowired
    private MeditationService meditationService;

    @Autowired
    private MeditationRepository meditationRepository;

    @BeforeEach
    void setUp()
    {
        CustomUserPrincipal principal = new CustomUserPrincipal(
                5L,
                "sh0908",
                "sh0908",
                "sh0908",
                "ROLE_USER",
                Collections.emptyMap());


        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

    }


    @Test
    @Rollback(false)
    //@WithMockUser(username = "5")
    void save() {

        //given
        MeditationCreateRequest dto = MeditationCreateRequest.builder()
                .meditationDt(LocalDate.now())
                .title("테스트제목7")
                .text("테스트 내용7")
                .build();

        //when
        Long save = meditationService.save(dto);

        //then
        assertThat(save).isNotNull();

    }

    @Test
    @Rollback(false)
    void update() {

        //ginven
        MeditationUpdateRequest dto = MeditationUpdateRequest.builder()
                .meditationId(2L)
                .meditationDt(LocalDate.now())
                .title("테스트제목2233")
                .text("테스트 내용22333")
                .build();

        //when
        Long update = meditationService.update(dto);

        //then
        assertThat(update).isNotNull();
    }

    @Test
    @Rollback(false)
    void delete() {

        //given
        MeditationCreateRequest dto = MeditationCreateRequest.builder()
                .meditationDt(LocalDate.now())
                .title("테스트제목3")
                .text("테스트 내용3")
                .build();

        Long meditationId = meditationService.save(dto);

        //when
        meditationService.delete(meditationId);

        //then
       Meditation meditation = meditationService.getMyMeditation(meditationId);

        //assertThat(meditation).isEmpty();
    }

    @Test
    void getMeditation() {

        //given
        MeditationCreateRequest dto = MeditationCreateRequest.builder()
                .meditationDt(LocalDate.now())
                .title("테스트제목20")
                .text("테스트 내용20")
                .build();
        Long meditationId = meditationService.save(dto);

        //when
        Meditation meditation = meditationService.getMyMeditation(meditationId);

        //then
//        assertThat(meditation).isPresent();
//        assertThat(meditation.get().getMeditationId()).isEqualTo(meditationId);
    }

    @Test
    void getMeditations() {

        //given
        String title  = "테스트";
        String bibleText = "";
        String etcText = "";
        LocalDate startDt = LocalDate.of(2026, 3, 1);
        LocalDate endDt =  LocalDate.of(2026, 3, 31);
        Pageable pageable = PageRequest.of(0, 5);



        //when
        Slice<MeditationListResponse> meditations = meditationService.getMyMeditations(
                title,
                bibleText,
                etcText,
                startDt,
                endDt,
                pageable
        );

        //then
        assertThat(meditations.getContent()).hasSize(5);  // 데이터 개수 검증

    }
}