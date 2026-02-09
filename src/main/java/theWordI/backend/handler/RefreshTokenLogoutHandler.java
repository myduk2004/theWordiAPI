package theWordI.backend.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.StringUtils;
import theWordI.backend.domain.jwt.service.JwtService;
import theWordI.backend.util.JWTUtil;
import theWordI.backend.util.SecurityUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class RefreshTokenLogoutHandler implements LogoutHandler {

    private final JwtService jwtService;
    private final RefreshTokenCookieHandler refreshTokenCookieHandler;


    public RefreshTokenLogoutHandler(JwtService jwtService, RefreshTokenCookieHandler refreshTokenCookieHandler)
    {
        this.jwtService = jwtService;
        this.refreshTokenCookieHandler = refreshTokenCookieHandler;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        try
        {

            String refreshToken = refreshTokenCookieHandler.getRefreshToken(request);
            if (refreshToken != null)
            {
               if (JWTUtil.isValid(refreshToken, false))
               {
                   Long userId = JWTUtil.getUserId(refreshToken);
                   if (userId != null)
                   {
                       jwtService.deleteRefreshToken(userId);
                   }
               }
            }

        }
        catch (Exception e)
        {
            log.warn("Logout processing failed", e);
        }
        finally {
            //Refresh 쿠키 삭제
            refreshTokenCookieHandler.deleteRefreshTokenCookie(response);
        }
    }
}
