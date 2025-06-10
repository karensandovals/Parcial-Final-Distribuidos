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
        } else if (event instanceof SessionDisconnectEvent) {
            String sessionId = ((SessionDisconnectEvent) event).getSessionId();
            sesionesConectadas.remove(sessionId);
            // Eliminar suscripciones asociadas
            topicSubscriptions.values().forEach(set -> set.remove(sessionId));
        } else if (event instanceof SessionSubscribeEvent) {
            SessionSubscribeEvent subscribeEvent = (SessionSubscribeEvent) event;
            String sessionId = (String) subscribeEvent.getMessage().getHeaders().get("simpSessionId");
            String destination = (String) subscribeEvent.getMessage().getHeaders().get("simpDestination");

            topicSubscriptions.computeIfAbsent(destination, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        } else if (event instanceof SessionUnsubscribeEvent) {
            SessionUnsubscribeEvent unsubscribeEvent = (SessionUnsubscribeEvent) event;
            String sessionId = (String) unsubscribeEvent.getMessage().getHeaders().get("simpSessionId");
            // Podrías remover el sessionId de todos los topics si lo deseas
            topicSubscriptions.values().forEach(set -> set.remove(sessionId));
        }
    }

    public boolean estanTodosLosAdministradoresConectados() {
        return tieneSuscriptor("/notificacion/laboratorio")
                && tieneSuscriptor("/notificacion/financiera")
                && tieneSuscriptor("/notificacion/deportes");
    }

    private boolean tieneSuscriptor(String topic) {
        return topicSubscriptions.getOrDefault(topic, Set.of()).size() > 0;
    }
}


