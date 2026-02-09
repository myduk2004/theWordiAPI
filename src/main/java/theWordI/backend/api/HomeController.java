package theWordI.backend.api;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import theWordI.backend.domain.user.dto.UserResponseDTO;

@RestController
public class HomeController {
    // 유저 정보
    @GetMapping(value="/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean>  HomeApi()
    {
        return ResponseEntity.status(200).body(true);
    }
}
