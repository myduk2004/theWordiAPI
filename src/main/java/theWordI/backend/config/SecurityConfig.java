package theWordI.backend.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import theWordI.backend.domain.jwt.service.JwtService;
import theWordI.backend.domain.user.entity.UserRoleType;
import theWordI.backend.filter.JWTFilter;
import theWordI.backend.filter.LoginFilter;
import theWordI.backend.handler.RefreshTokenCookieHandler;
import theWordI.backend.handler.RefreshTokenLogoutHandler;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthenticationSuccessHandler loginSuccessHandler;
    private final AuthenticationSuccessHandler socialSuccessHandler;
    private final JwtService jwtService;
    private final RefreshTokenCookieHandler refreshTokenCookieHandler;


    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration,
                          @Qualifier("LoginSuccessHandler") AuthenticationSuccessHandler loginSuccessHandler,
                          @Qualifier("SocialSuccessHandler") AuthenticationSuccessHandler socialSuccessHandler,
                          JwtService jwtService,
                          RefreshTokenCookieHandler refreshTokenCookieHandler)

    {
        this.authenticationConfiguration = authenticationConfiguration;
        this.loginSuccessHandler = loginSuccessHandler;
        this.socialSuccessHandler = socialSuccessHandler;
        this.jwtService = jwtService;
        this.refreshTokenCookieHandler = refreshTokenCookieHandler;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception
    {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        //CSRF 보안 필터 disable
        http.csrf(AbstractHttpConfigurer::disable);

        //CORS 설정
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));


        //기본 Form 기반 인증 필터들 disable
        http.formLogin(AbstractHttpConfigurer::disable);

        //기본 Basic 인증 필터 disable
        http.httpBasic(AbstractHttpConfigurer::disable);

        //세션필터 설정 (STATELESS)
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));



        //인가(API 접근설정)
        /* 테스트 끝난 다음 열어라
        http.authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(HttpMethod.GET,  "/", "/index", "/home", "/css/**", "/js/**", "/images/**").permitAll()
                    .requestMatchers("/jwt/exchange", "/jwt/refresh").permitAll()
                    .requestMatchers(HttpMethod.POST, "/user/exist", "/user").permitAll()
                    .requestMatchers(HttpMethod.GET, "/user").hasRole(UserRoleType.USER.name())
                    .requestMatchers(HttpMethod.PUT, "/user").hasRole(UserRoleType.USER.name())
                    .requestMatchers(HttpMethod.DELETE, "/user").hasRole(UserRoleType.USER.name())
                    .anyRequest().authenticated()
                );
         */

        //테스트용
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() //CORS
                        .requestMatchers(HttpMethod.GET,  "/", "/index", "/home",
                                "/css/**", "/js/**", "/images/**").permitAll()  //정적 리소스
                        .requestMatchers("/jwt/exchange", "/jwt/refresh").permitAll()  //인증관련

                        //회원
                        .requestMatchers(HttpMethod.POST, "/user/exist", "/user").permitAll()
                        .requestMatchers("/user").authenticated()

                        //API 보호
                        .requestMatchers("/api/**").authenticated()

                        //나머지
                        .anyRequest().denyAll()

        );


        //JWT인증 필터
        http.addFilterBefore(new JWTFilter(), LogoutFilter.class);


        //커스텀 필터 추가
        http.addFilterBefore(new LoginFilter(authenticationManager(authenticationConfiguration), loginSuccessHandler), UsernamePasswordAuthenticationFilter.class);


        //oAuth2 인증용
        http.oauth2Login(oauth2 -> oauth2.successHandler(socialSuccessHandler));


        //기존 로그아웃 필터 + 커스텀 Refresh 토큰 삭제 핸들러 추가
        http.logout(logout -> logout
                .addLogoutHandler(new RefreshTokenLogoutHandler(jwtService, refreshTokenCookieHandler))
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\":\"로그아웃 성공\"}");
                }));

        //예외처리
        //인증되지 않은 사용자 접근시 401 Error
        //인증은 되었지만 권한이 없을 때 403 Error
        http.exceptionHandling(e ->
                e.authenticationEntryPoint((request, response, authException) -> {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        })
                        .accessDeniedHandler((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN);
                        }));


        return http.build();
    }

    //Cors Configuration 정보 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration configuration = new CorsConfiguration();
       // configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedOriginPatterns(List.of(
                "https://www.thewordi.kr",
                "https://thewordi.kr",
                "http://localhost:5173"));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
        configuration.setMaxAge(3600L); //1시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }


    //Role 계층적 설정
    @Bean
    public RoleHierarchy roleHierarchy()
    {
        return RoleHierarchyImpl.withRolePrefix("ROLE_")
                .role(UserRoleType.ADMIN.name()).implies(UserRoleType.USER.name())
                .build();
    }

    // 비밀번호 단방향(BCrypt) 암호화용 Bean
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
