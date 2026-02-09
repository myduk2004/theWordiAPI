package theWordI.backend.domain.bible.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class BibleVerseUpdateRequest {

    @NotNull(message="성경 구절값은 필수입니다.")
    private Long verseId;

    @NotBlank(message="성경 구절내용은 필수입니다.")
    private String text;
}
