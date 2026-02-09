package theWordI.backend.domain.user.auth;

import lombok.Getter;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import theWordI.backend.domain.user.entity.SocialProviderType;

import java.util.Map;

@Getter
public class SocialOAuthAttributes {

    private final String username;
    private final String email;
    private final String name;
    private final SocialProviderType provider;

    private SocialOAuthAttributes(String username,
                                  String email,
                                  String name,
                                  SocialProviderType provider)
    {
        this.username = username;
        this.email = email;
        this.name = name;
        this.provider = provider;
    }

    public static SocialOAuthAttributes of(String registrationId,
                                            Map<String, Object> attributes)
    {
        SocialProviderType provider = SocialProviderType.valueOf(registrationId.toUpperCase());
        return switch (provider) {
            case NAVER -> fromNaver(attributes);
            case GOOGLE -> fromGoogle(attributes);
            default -> throw new OAuth2AuthenticationException(
                    new OAuth2Error("UNSUPPORTED_PROVIDER"), "지원하지 않는 소셜 로그인입니다."
            );
        };
    }

    private static SocialOAuthAttributes fromGoogle(
            Map<String, Object> attributes
    ) {
        String id = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        return new SocialOAuthAttributes(
                "GOOGLE_" + id,
                email,
                name,
                SocialProviderType.GOOGLE
        );
    }

    private static SocialOAuthAttributes fromNaver(
            Map<String, Object> attributes
    ){
        Map<String, Object> response =
                (Map<String, Object>) attributes.get("response");

        String id = (String) response.get("id");
        String email = (String) response.get("email");
        String name = (String) response.get("name");

        return new SocialOAuthAttributes(
                "NAVER_" + id,
                email,
                name,
                SocialProviderType.NAVER
        );
    }
}
