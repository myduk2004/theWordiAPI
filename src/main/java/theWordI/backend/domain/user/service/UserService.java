package theWordI.backend.domain.user.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import theWordI.backend.domain.jwt.service.JwtService;
import theWordI.backend.domain.user.auth.CustomUserPrincipal;
import theWordI.backend.domain.user.auth.SocialOAuthAttributes;
import theWordI.backend.domain.user.dto.UserCreateRequestDTO;
import theWordI.backend.domain.user.dto.UserSearchRequestDTO;
import theWordI.backend.domain.user.dto.UserResponseDTO;
import theWordI.backend.domain.user.dto.UserUpdateRequestDTO;
import theWordI.backend.domain.user.entity.UserEntity;
import theWordI.backend.domain.user.repository.UserRepository;
import theWordI.backend.util.SecurityUtil;

import java.util.Map;

@Service
public class UserService extends DefaultOAuth2UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public UserService(PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       JwtService jwtService)
    {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    // 사용자아이디 존재여부 체크(회원가입 시)
    public Boolean existUser(UserSearchRequestDTO dto)
    {
        return userRepository.existsByUsername(dto.getUsername());
    }

    // 회원 가입
    @Transactional
    public Long addUser(UserCreateRequestDTO dto)
    {
        UserEntity entity = UserEntity.createBasic(dto, passwordEncoder);
        try
        {
            userRepository.save(entity);
            return entity.getUserId();
        }
        catch (DataIntegrityViolationException e)
        {
            throw new IllegalArgumentException("이미 사용자가 존재합니다.");
        }
    }


    // 내 정보 수정
    @Transactional
    public Long updateUser(UserUpdateRequestDTO dto) throws AccessDeniedException
    {
        //조회
        UserEntity user = userRepository.findByUserId(SecurityUtil.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        //회원정보 수정
        user.updateUser(dto);

        return user.getUserId();
    }


    //일반 로그인 중 사용자정보 처리
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userRepository
                .findByUsernameAndIsLockAndIsSocial(username, false, false)
                .orElseThrow(() -> new UsernameNotFoundException(username));


        return new CustomUserPrincipal(
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getPassword(),
                "ROLE_" + user.getRole().name(),
                Map.of()
        );
    }

    // 소셜 로그인 중 사용자정보 처리 (매 로그인 시 : 신규 = 가입, 기존 = 업데이트)
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException
    {
        //부모 메서드 호출
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        //소셜별 사용자정보 추출
        SocialOAuthAttributes attr = SocialOAuthAttributes.of(registrationId, oAuth2User.getAttributes());
        String username = attr.getUsername();
        String email = attr.getEmail();
        String name = attr.getName();

        //=== DB 처리 ===
        UserEntity user = userRepository.findByUsernameAndIsSocial(username, true)
            .map(e -> {
                e.validateNotLocked();
                return updateSocialUser(e, name, email);
            })
            .orElseGet(() ->
                createSocialUser(username, email, name, registrationId)
            );


        return new CustomUserPrincipal(
            user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getPassword(),
                "ROLE_" + user.getRole().name(),
                oAuth2User.getAttributes()
        );
    }

    public UserEntity createSocialUser(String username, String email,String name, String registrationId)
    {
        //신규 유저 추가
        UserEntity user = UserEntity.createSocial(username, email, name, registrationId);
        return userRepository.save(user);
    }


    public UserEntity updateSocialUser(UserEntity user, String name,String  email)
    {
        UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
        dto.setName(name);
        dto.setEmail(email);

        user.updateUser(dto);
        return user;
    }


    // 내 정보 조회
    @Transactional
    public UserResponseDTO readUser()
    {
        UserEntity entity = userRepository.findByUserId(SecurityUtil.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다 : " + SecurityUtil.getUserName()));

        return new UserResponseDTO(entity.getUserId(),
                entity.getUsername(),
                entity.isSocial(),
                entity.getName(),
                entity.getEmail(),
                entity.getRole().name());
    }


    // 회원 탈퇴(자체/소셜) - 추후 로그 로직 보강하도록
    @Transactional
    public void deleteUser() throws AccessDeniedException
    {
        Long userId = SecurityUtil.getUserId();

        //유저제거
        userRepository.deleteByUserId(userId);

        //refresh 토큰 제거
        jwtService.deleteRefreshToken(userId);
    }

}
