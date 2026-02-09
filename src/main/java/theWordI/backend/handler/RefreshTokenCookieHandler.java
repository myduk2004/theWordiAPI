package theWordI.backend.handler;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCookieHandler {

    private final long refreshTokenMaxAge;
    private final boolean secure;

    public RefreshTokenCookieHandler(@Value("${jwt.refreshTokenMaxAge}") long refreshTokenMaxAge,
                                     @Value("${cookie.secure}") boolean secure)
    {
        this.refreshTokenMaxAge = refreshTokenMaxAge;
        this.secure = secure;
    }

    //RefreshToken 쿠키 생성(유효기간 기본값: refreshTokenMaxAge : 7일)
    public void delete_add_RefreshToken(HttpServletResponse response, String newRefreshToken) {
        delete_add_RefreshToken(response, newRefreshToken, 0);
    }

    //RefreshToken 쿠키 생성(유효기간 기본값: 받은값)
    public void delete_add_RefreshToken(HttpServletResponse response, String newRefreshToken, long maxAge)
    {
        // 기존 쿠키 제거
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .path("/")
                .httpOnly(true)
                .secure(secure)     //HTTPS 환경에서만 전송
                .sameSite("Lax")   // Strict : cross-site 인증 불가, Lax : 폼 제출/링크 이동 허용, None : cross-site 가능 / 반드시 secure(true)
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        // 새로운 쿠키 등록
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAge > 0? maxAge : refreshTokenMaxAge) // 예: 7일
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }


    //RefreshToken 쿠키 생성(유효기간 기본값: 받은값)
    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        // 기존 쿠키 제거
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .path("/")
                .httpOnly(true)
                .secure(secure)    //HTTPS 환경에서만 전송
                .sameSite("Lax")  // Strict : cross-site 인증 불가, Lax : 폼 제출/링크 이동 허용, None : cross-site 가능 / 반드시 secure(true)
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
    }

    public String getRefreshToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        // 기존 쿠키 제거
        String refreshToken = null;
        for ( Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
                break;
            }
        }

        return refreshToken;
    }

}
