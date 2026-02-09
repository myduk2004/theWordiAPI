package theWordI.backend.util;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import theWordI.backend.domain.user.auth.CustomUserPrincipal;

import java.util.List;
import java.util.Optional;

public class SecurityUtil {

    private SecurityUtil() {}

    public static Optional<CustomUserPrincipal> getUser()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null ||
            !(auth.getPrincipal() instanceof CustomUserPrincipal))
        {
            return Optional.empty();
        }

        return Optional.of((CustomUserPrincipal) auth.getPrincipal());
    }

    public static Long getUserId()
    {
        return getUser().map(CustomUserPrincipal::getUserId)
                .orElseThrow(() -> new AccessDeniedException("로그인이 필요합니다."));
    }


    public static Optional<String> getUserName()
    {
        return getUser().map(CustomUserPrincipal::getUsername);
    }

    public static List<String> getUserRole()
    {
        return getUser()
                .map(u -> u.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
                )
                .orElse(List.of());
    }
}
