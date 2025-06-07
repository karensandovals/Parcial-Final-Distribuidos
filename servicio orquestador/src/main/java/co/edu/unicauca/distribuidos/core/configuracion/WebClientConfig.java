package co.edu.unicauca.distribuidos.core.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    WebClient metodoAjecutar(WebClient.Builder builder) {
        return builder.build();
    }
}