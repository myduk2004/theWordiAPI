package theWordI.backend.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import theWordI.backend.domain.jwt.service.JwtService;
import theWordI.backend.domain.user.auth.CustomUserPrincipal;
import theWordI.backend.util.JWTUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Qualifier("LoginSuccessHandler")
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final RefreshTokenCookieHandler refreshTokenCookieHandler;

    public LoginSuccessHandler(JwtService jwtService,
                               RefreshTokenCookieHandler refreshTokenCookieHandler)
    {
        this.jwtService = jwtService;
        this.refreshTokenCookieHandler = refreshTokenCookieHandler;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        //username, role
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();
        String role = principal.getAuthorities().iterator().next().getAuthority();
        String username = principal.getUsername();
        String name = principal.getName();

        // JWT(Access/Refresh) 발급
        String accessToken = JWTUtil.createAccessToken(userId, role);
        String refreshToken = JWTUtil.createRefreshToken(userId);

        //발급한 Refresh DB저장
        jwtService.addRefreshToken(userId, refreshToken);

        //refreshToken HttpOnly 쿠키로 저장
        refreshTokenCookieHandler.delete_add_RefreshToken(response, refreshToken);


        //응답
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", accessToken);
        responseData.put("userId", userId);
        responseData.put("username", username);
        responseData.put("name", name);
        responseData.put("role", role);

        String json = objectMapper.writeValueAsString(responseData);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
        response.getWriter().flush();

    }

}
