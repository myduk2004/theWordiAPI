package theWordI.backend.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import theWordI.backend.domain.jwt.service.JwtService;
import theWordI.backend.domain.user.auth.CustomUserPrincipal;
import theWordI.backend.util.JWTUtil;

import java.io.IOException;

@Component
@Qualifier("SocialSuccessHandler")
@Slf4j
public class SocialSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final RefreshTokenCookieHandler refreshTokenCookieHandler;

    @Value("${app.redirect.after-social-login}")
    private String redirectUrl;

    public SocialSuccessHandler(JwtService jwtService, RefreshTokenCookieHandler refreshTokenCookieHandler) {
        this.jwtService = jwtService;
        this.refreshTokenCookieHandler = refreshTokenCookieHandler;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        try {

            CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
            Long userId = principal.getUserId();
            String role = principal.getAuthorities().iterator().next().getAuthority();


            //1.JWT(Refresh) 발급
            String refreshToken = JWTUtil.createRefreshToken(userId);

            //2. 기존 발급된 Refresh Token DB 삭제 후 새로운 Refresh Token DB 저장
            jwtService.addRefreshToken(userId, refreshToken);

            //3. Refresh Token 쿠키 저장(기존 쿠키 삭제 후 저장)
            refreshTokenCookieHandler.delete_add_RefreshToken(response, refreshToken, 10); // 10초 (프론트에서 발급 후 바로 헤더 전환 로직 진행 예정)

            response.sendRedirect(redirectUrl);
        }
        catch (Exception ex)
        {
            log.error("로그인 처리 중 오류 :  ", ex.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "로그인 처리 중 오류가 발생하였습니다.");
        }

    }

}
