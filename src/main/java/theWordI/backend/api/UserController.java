package theWordI.backend.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import theWordI.backend.domain.user.dto.UserCreateRequestDTO;
import theWordI.backend.domain.user.dto.UserSearchRequestDTO;
import theWordI.backend.domain.user.dto.UserResponseDTO;
import theWordI.backend.domain.user.dto.UserUpdateRequestDTO;
import theWordI.backend.domain.user.service.UserService;

import java.util.Collections;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    // 사용자 username 중복체크
    @PostMapping(value="/user/exist", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> existUser(@RequestBody UserSearchRequestDTO dto)
    {
        return ResponseEntity.ok(userService.existUser(dto));
    }

    // 회원가입
    @PostMapping(value="/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> JoinApi(@RequestBody UserCreateRequestDTO dto)
    {
        Long id = userService.addUser(dto);
        Map<String, Long> responseBody = Collections.singletonMap("userEntityId", id);
        return ResponseEntity.status(201).body(responseBody);
    }

    // 내 사용자 정보 수정
    @PutMapping(value="/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> updateUserApi(UserUpdateRequestDTO dto
    ) throws AccessDeniedException
    {
        return ResponseEntity.status(200).body(userService.updateUser(dto));
    }

    // 내 사용자 정보
    @GetMapping(value="/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponseDTO usreMeApi()
    {
        return userService.readUser();
    }


    //내 회원탈퇴 (자체/소셜)
    @DeleteMapping(value="/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteUserApi() throws AccessDeniedException
    {
        userService.deleteUser();
        return ResponseEntity.status(200).body(true);
    }
}
