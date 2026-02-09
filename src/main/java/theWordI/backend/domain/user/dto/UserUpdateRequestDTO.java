package theWordI.backend.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserUpdateRequestDTO {

    @NotEmpty
    private String name;

    private String email;

    private String phone;
}
