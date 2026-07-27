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
            case KAKAO -> fromKakao(attributes);
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
                id,
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
                id,
                email,
                name,
                SocialProviderType.NAVER
        );
    }




    
    private static SocialOAuthAttributes fromKakao(
            Map<String, Object> attributes
    ){

        /* 카카오 응답 형식
        {
            "id":3485729348,

           "properties":{
                "nickname":"김은명"
            },
            "kakao_account":{
                "profile":{
                    "nickname":"김은명"
                }
            }
        } */

        String id = String.valueOf(attributes.get("id"));
        Map<String, Object> kakaoAccount =
                (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String name = (String) profile.get("nickname");

        return new SocialOAuthAttributes(
                id,
                "",
                name,
                SocialProviderType.KAKAO
        );
    }
}
