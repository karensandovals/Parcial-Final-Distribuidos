package co.edu.unicauca.distribuidos.core.configuracion;

import java.util.Map;
import java.util.Set;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
public class AdminWebSocketTracker implements ApplicationListener<ApplicationEvent> {

    private final Map<String, Set<String>> topicSubscriptions = new ConcurrentHashMap<>();
    private final Set<String> sesionesConectadas = ConcurrentHashMap.newKeySet();

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof SessionConnectEvent) {
            String sessionId = (String) ((SessionConnectEvent) event).getMessage().getHeaders().get("simpSessionId");
            sesionesConectadas.add(sessionId);
            System.out.println("✅ Conectado: " + sessionId);

        } else if (event instanceof SessionDisconnectEvent) {
            String sessionId = ((SessionDisconnectEvent) event).getSessionId();
            sesionesConectadas.remove(sessionId);
            topicSubscriptions.values().forEach(set -> set.remove(sessionId));
            System.out.println("🔌 Desconectado: " + sessionId);

        } else if (event instanceof SessionSubscribeEvent) {
            SessionSubscribeEvent subscribeEvent = (SessionSubscribeEvent) event;
            String sessionId = (String) subscribeEvent.getMessage().getHeaders().get("simpSessionId");
            String destination = (String) subscribeEvent.getMessage().getHeaders().get("simpDestination");

            topicSubscriptions.computeIfAbsent(destination, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
            System.out.println("📌 Subscrito " + sessionId + " a " + destination);

        } else if (event instanceof SessionUnsubscribeEvent) {
            SessionUnsubscribeEvent unsubscribeEvent = (SessionUnsubscribeEvent) event;
            String sessionId = (String) unsubscribeEvent.getMessage().getHeaders().get("simpSessionId");

            topicSubscriptions.values().forEach(set -> set.remove(sessionId));
            System.out.println("❌ Unsubscrito: " + sessionId);
        }
    }

    public boolean estanTodosLosAdministradoresConectados() {
        return tieneSuscriptor("/notificacion/laboratorio") &&
               tieneSuscriptor("/notificacion/financiera") &&
               tieneSuscriptor("/notificacion/deportes");
    }

    private boolean tieneSuscriptor(String topic) {
        Set<String> suscriptores = topicSubscriptions.getOrDefault(topic, Set.of());
        return !suscriptores.isEmpty();
    }

    // Métodos adicionales útiles para pruebas y logs
    public Map<String, Set<String>> getTopicSubscriptions() {
        return topicSubscriptions;
    }

    public Set<String> getSesionesConectadas() {
        return sesionesConectadas;
    }
}


