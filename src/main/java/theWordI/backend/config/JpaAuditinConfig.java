package theWordI.backend.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditinConfig {
    //EnableJpaAuditing를 활성화시키기 위한 config 
    //BackendApplication 위에 선언해놓아도 되지만 
    //통합테스트 시 불편해서 여기에 정의
}
