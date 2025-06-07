package co.edu.unicauca.distribuidos.core.configuracion;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefijos a los que el cliente puede suscribirse
        config.enableSimpleBroker("/notificacion");
        // Prefijo para mensajes entrantes desde el cliente
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Punto de entrada WebSocket para los clientes (por ejemplo, desde Angular)
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*") // Reemplazar con el dominio real en producción
                .withSockJS(); // Para compatibilidad con navegadores antiguos
    }
}

