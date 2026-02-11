package theWordI.backend.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI()
    {
        OpenAPI openAPI = new OpenAPI();
        openAPI.info(new Info()
                .title("theWordI API 목록")
                .description("theWordI API 목록입니다.")
                .version("v1.0.0"));
        openAPI.servers(List.of(
                new Server()
                        .url("https://api.thewordi.kr")
                        .description("api서버")
        ));
        return openAPI;

    }
}
