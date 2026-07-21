package theWordI.backend.domain.meditation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import theWordI.backend.domain.meditation.entity.Meditation;
import theWordI.backend.util.HtmlSanitizerUtil;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MeditationUpdateRequest {

    @NotNull(message="수정할 묵상의 번호는 필수입니다.")
    private Long meditationId;

    @NotNull(message="묵상일은 필수입니다.")
    @PastOrPresent(message="미래의 날짜는 묵상일로 선택할 수 없습니다.")
    private LocalDate meditationDt;

    @NotBlank(message="제목은 필수입니다.")
    private String title;

    @NotBlank(message="내용은 필수입니다.")
    private String text;

    private String bibleText;
    private String etcText;
    private String etcSource;
    private List<Long> verseIds;

    //DTO를 entity로 변환
    public Meditation toEntity(Long userId)
    {
        //엔티티 변환 로직이 dto에 있으므로 sanitize 로직 여기서 적용
        String cleanHtml = HtmlSanitizerUtil.sanitize(this.text);
        return Meditation.builder()
                .meditationId(this.meditationId)
                .meditationDt(this.meditationDt)
                .title(this.title)
                .bibleText(this.bibleText)
                .etcText(this.etcText)
                .etcSource(this.etcSource)
                .text(cleanHtml)
                .userId(userId)
                .build();
    }
}
