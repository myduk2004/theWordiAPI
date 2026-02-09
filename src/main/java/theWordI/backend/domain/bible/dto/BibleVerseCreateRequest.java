package theWordI.backend.domain.bible.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.security.access.AccessDeniedException;
import theWordI.backend.domain.bible.entity.BibleVerse;
import theWordI.backend.exception.UnAuthorizedException;
import theWordI.backend.util.SecurityUtil;

import java.util.Optional;

@Data
public class BibleVerseCreateRequest {

    @NotBlank(message="성경 버전 선택은 필수입니다.")
    private String versionId;

    @NotNull(message="책 번호값은 필수입니다.")
    @Min(value= 1, message = "책 번호값은 양수이어야 합니다.")
    private Integer bookId;

    @NotNull(message="장 번호값은 필수입니다.")
    @Min(value= 1, message = "장 번호값은 양수이어야 합니다.")
    private Integer chapter;

    @NotNull(message="절 번호값은 필수입니다.")
    @Min(value= 1, message = "절 번호값은 양수이어야 합니다.")
    private Integer verse;

    @NotBlank(message="성경 구절 입력은 필수입니다.")
    private String text;

    private Long regId;


    //DTO를 Entity로 변환하는 메서드
    public BibleVerse toEntity(Long regId)
    {

        return BibleVerse.builder()
                .versionId(this.versionId)
                .bookId(this.bookId)
                .chapter(this.chapter)
                .verse(this.verse)
                .text(this.text)
                .regId(Optional.ofNullable(regId)
                        .orElseThrow(() -> new UnAuthorizedException("로그인이 필요합니다.")))
                .build();
    }

}
