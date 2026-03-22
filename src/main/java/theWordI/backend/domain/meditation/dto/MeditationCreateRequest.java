package theWordI.backend.domain.meditation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import theWordI.backend.domain.meditation.entity.Meditation;

import java.time.LocalDate;
import java.util.List;


@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MeditationCreateRequest {

    @NotNull(message="묵상일은 필수입니다.")
    @PastOrPresent(message="미래의 날짜는 묵상일로 선택할 수 없습니다.")
    private LocalDate meditationDt;

    @NotBlank(message="제목은 필수입니다.")
    private String title;

    @NotBlank(message="내용은 필수입니다.")
    private String text;

    private List<Long> verseIds;


    //DTO를 entity로 변환
    public Meditation toEntity(Long userId)
    {
        return Meditation.builder()
                .meditationDt(this.meditationDt)
                .title(this.title)
                .text(this.text)
                .userId(userId)
                .build();
    }
}
