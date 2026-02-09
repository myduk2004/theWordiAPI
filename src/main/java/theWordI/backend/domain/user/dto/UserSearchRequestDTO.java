package theWordI.backend.domain.user.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class UserSearchRequestDTO {


    private String username;
    private String email;
    private String name;

    private Integer page;
    private Integer size;

}
