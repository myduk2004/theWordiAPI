package theWordI.backend.domain.jwt.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import theWordI.backend.domain.jwt.dto.JWTResponseDTO;
import theWordI.backend.domain.jwt.entity.RefreshEntity;
import theWordI.backend.domain.jwt.repository.RefreshRepository;
import theWordI.backend.domain.user.entity.UserEntity;
import theWordI.backend.domain.user.entity.UserRoleType;
import theWordI.backend.domain.user.repository.UserRepository;
import theWordI.backend.handler.RefreshTokenCookieHandler;
import theWordI.backend.util.JWTUtil;


@Service
@Transactional
public class JwtService {
    private final RefreshRepository refreshRepository;
    private final RefreshTokenCookieHandler refreshTokenCookieHandler;
    private final UserRepository userRepository;

    public JwtService(RefreshRepository refreshRepository,
                      RefreshTokenCookieHandler refreshTokenCookieHandler,
                      UserRepository userRepository)
    {
        this.refreshRepository = refreshRepository;
        this.refreshTokenCookieHandler = refreshTokenCookieHandler;
        this.userRepository = userRepository;
    }



    public JWTResponseDTO createAccessTokenByRefreshCookie(HttpServletRequest request,
                                                        HttpServletResponse response
    ){

        String refreshToken = extractRefreshToken(request);
        
        //1. 쿠키에 refreshToken 존재 검증
        if (refreshToken == null) {
            throw new RuntimeException("refreshToken 쿠키가 없습니다.");
        }

        //2. JWT 검증
        if (!JWTUtil.isValid(refreshToken, false)) {
            throw new RuntimeException("유효하지 않은 refreshToken 입니다.");
        }

        //3. DB whitelist 검증
        RefreshEntity refreshEntity = refreshRepository
                .findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("RefreshToken DB 없음"));



        //4. 사용자정보 일치여부 검증
        Long userId = JWTUtil.getUserId(refreshToken);
        if (!refreshEntity.getUserId().equals(userId))
        {
            throw new RuntimeException("RefreshToken 사용자 불일치");
        }

        //5. 사용자정보 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("refreshToken에 유효한 사용자 없음"));


        //6. 토큰 재발급
        String newAccessToken = JWTUtil.createAccessToken(userId, user.getRole().name());
        String newRefreshToken = JWTUtil.createRefreshToken(userId);

        //7. Refresh 토큰 DB 교체
        refreshRepository.delete(refreshEntity);
        refreshRepository.flush(); // 같은 트랜잭션 내부라 : 삭제 -> 생성 문제 해결

        refreshRepository.save(RefreshEntity.builder()
                                .userId(userId)
                                .refreshToken(newRefreshToken)
                                .build()
        );

        
        //8. 쿠키 교체
        refreshTokenCookieHandler.delete_add_RefreshToken(response, newRefreshToken);


        return new JWTResponseDTO(newAccessToken,
                "",
                user.getUserId().toString(),
                user.getUsername(),
                user.getName(),
                user.getRole().name());
    }

    public String extractRefreshToken(HttpServletRequest request)
    {
        // 쿠키 리스트
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new RuntimeException("쿠키가 존재하지 않습니다.");
        }

        // Refresh 토큰 획득
        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
                break;
            }
        }
        return refreshToken;
    }


    @Transactional
    //JWT Refresh 토큰 발급 후 저장 메소드
    public void addRefreshToken(Long userId, String refreshToken)
    {
        //기존 등록된 리프레시 토큰 삭제
        deleteRefreshToken(userId);

        //등록
        RefreshEntity entity = RefreshEntity.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .build();
        refreshRepository.save(entity);
    }

    //JWT Refresh 존재 확인 메소드
    @Transactional(readOnly = true)
    public boolean existsRefresh(String refreshToken)
    {
        return refreshRepository.existsByRefreshToken(refreshToken);
    }

    //특정 유저 Refresh 토큰 모두 삭제
    public void deleteRefreshToken(Long userId)
    {
        refreshRepository.deleteByUserId(userId);
    }

}
