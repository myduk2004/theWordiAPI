package theWordI.backend.domain.user.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomUserPrincipal implements UserDetails, OAuth2User {

    private final Long userId;
    private final String username;
    private final String name;
    private final String password;
    private final String role;
    private final Map<String, Object> attributes;


    public CustomUserPrincipal(Long userId, String username, String name, String password, String role, Map<String, Object> attributes)
    {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.password = password;
        this.role = role;
        this.attributes = attributes;
    }

     //명시적으로 선언함
    public Long getUserId()
    {
        return userId;
    }

    @Override
    public String getName(){
        return name;
    }

    //=====권한 ======
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority(role));
    }


    //==== UserDetails =====
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }


    //===== OAuth2User ====
    @Override
    public Map<String, Object> getAttributes()
    {
        return attributes;
    }

}
