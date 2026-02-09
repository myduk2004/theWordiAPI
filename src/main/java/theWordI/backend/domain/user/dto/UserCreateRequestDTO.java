package theWordI.backend.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;



@Data
public class UserCreateRequestDTO {

    @NotBlank(message="사용자아이디는 필수입니다.")
    private String username;

    @NotBlank(message="비밀번호는 필수입니다.") @Size(min=4)
    private String password;

    @NotBlank(message="이메일은 필수입니다.")
    @Email(message="이메일 양식이 맞지않습니다.")
    private String email;

    @NotBlank(message="이름은 필수입니다.")
    private String name;
    private String phone;
}
