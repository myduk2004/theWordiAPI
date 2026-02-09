package theWordI.backend.domain.jwt.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequestDTO {
//RefreshRequestDTO 사용안함
    @NotBlank
    private String refreshToken;
}
