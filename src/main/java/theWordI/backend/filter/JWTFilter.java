package theWordI.backend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import theWordI.backend.domain.user.auth.CustomUserPrincipal;
import theWordI.backend.util.JWTUtil;

import java.io.IOException;
import java.util.Map;

// OncePerRequestFilter
// redirect가 아닌 forword :내부적으로 N번 요청하는것에 대해 첫번째에만 동작
public class JWTFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer "))
        {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authorization.substring(7);

        //1. JWT 검증 (서명, 만료여부)
        if (!JWTUtil.isValid(accessToken, true)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"토큰 만료 또는 유효하지 않은 토큰\"}");
            return;
        }

        //2. 사용자 검증
        Long userId = JWTUtil.getUserId(accessToken);
        String role = JWTUtil.getRole(accessToken);


        //3.Pricipal 생성
        CustomUserPrincipal principal = new CustomUserPrincipal(
                userId,
                null,
                null,
                null,
                role,
                Map.of()
        );

        //4. Authentication 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
        
        //5. SecurityContext 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);

    }
}
