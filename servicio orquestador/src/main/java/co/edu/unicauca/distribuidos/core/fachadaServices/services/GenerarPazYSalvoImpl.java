package co.edu.unicauca.distribuidos.core.fachadaServices.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import co.edu.unicauca.distribuidos.core.fachadaServices.DTOPeticion.PeticionPazYSalvoDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta.RespuestaPazYSalvoDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta.RespuestaPazYSalvoDTODeportes;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta.RespuestaPazYSalvoDTOFinanciera;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta.RespuestaPazYSalvoDTOLaboratorio;
import reactor.core.publisher.Mono;

@Service
public class GenerarPazYSalvoImpl implements GenerarPazYSalvoInt {

    @Autowired
    private WebClient webClient;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final String LABORATORIO_URL = "http://localhost:2020/api/laboratorio/consultar";
    private final String FINANCIERA_URL = "http://localhost:5003/api/deudas";
    private final String DEPORTES_URL = "http://localhost:5008/api/deportes";

    private final String ELIMINAR_LABORATORIO_URL = "http://localhost:2020/api/laboratorio/eliminar";
    private final String ELIMINAR_FINANCIERA_URL = "http://localhost:5003/api/deudas/";
    private final String ELIMINAR_DEPORTES_URL = "http://localhost:5008/api/deportes/";

    private final int MAX_INTENTOS = 3;
    private final long DELAY_REINTENTO = 1000; // 1 segundo entre reintentos

    @Override
    public RespuestaPazYSalvoDTO consultarPazYSalvo(PeticionPazYSalvoDTO peticion) {
        RespuestaPazYSalvoDTO respuesta = new RespuestaPazYSalvoDTO();
        respuesta.setCodigoEstudiante(peticion.getCodigoEstudiante());

        // Notificar a administradores
        notificarAdministradores(peticion.getCodigoEstudiante(), peticion.getNombresEstudiante());

        boolean hasError = false;
        String errorMessage = "";

        try {
            // 1. Consultar Laboratorio con reintentos
            List<RespuestaPazYSalvoDTOLaboratorio> laboratorio = consultarConReintentos(
                () -> webClient.post()
                    .uri(LABORATORIO_URL)
                    .bodyValue(peticion)
                    .retrieve()
                    .bodyToFlux(RespuestaPazYSalvoDTOLaboratorio.class)
                    .collectList()
                    .block(),
                "Laboratorio"
            );

            if (laboratorio != null) {
                respuesta.setObjLaboratorio(laboratorio);
                notificar("laboratorio", laboratorio);
            } else {
                hasError = true;
                errorMessage += "Fallo en servicio de Laboratorio. ";
            }

            // 2. Consultar Financiera con reintentos
            List<RespuestaPazYSalvoDTOFinanciera> financiera = consultarConReintentos(
                () -> webClient.post()
                    .uri(FINANCIERA_URL)
                    .bodyValue(peticion)
                    .retrieve()
                    .bodyToFlux(RespuestaPazYSalvoDTOFinanciera.class)
                    .collectList()
                    .block(),
                "Financiera"
            );

            if (financiera != null) {
                respuesta.setObjFinanciera(financiera);
                notificar("financiera", financiera);
            } else {
                hasError = true;
                errorMessage += "Fallo en servicio de Financiera. ";
            }

            // 3. Consultar Deportes con reintentos
            List<RespuestaPazYSalvoDTODeportes> deportes = consultarConReintentos(
                () -> webClient.post()
                    .uri(DEPORTES_URL)
                    .bodyValue(peticion)
                    .retrieve()
                    .bodyToFlux(RespuestaPazYSalvoDTODeportes.class)
                    .collectList()
                    .block(),
                "Deportes"
            );

            if (deportes != null) {
                respuesta.setObjDeportes(deportes);
                notificar("deportes", deportes);
            } else {
                hasError = true;
                errorMessage += "Fallo en servicio de Deportes. ";
            }

            if (!hasError) {
                respuesta.setMensaje("Consulta completada con éxito.");
                // Notificar también al estudiante
                notificarEstudiante(peticion.getCodigoEstudiante(), respuesta);
            } else {
                respuesta.setMensaje("Consulta completada parcialmente. " + errorMessage);
                // Revertir operaciones parciales exitosas
                revertirOperacionesParciales(peticion, respuesta);
            }

        } catch (Exception e) {
            respuesta.setMensaje("Error en la solicitud. Se revertirán operaciones parciales.");
            // Revertir operaciones parciales
            revertirOperacionesParciales(peticion, respuesta);
            notificar("error", "Error consultando paz y salvo: " + e.getMessage());
        }

        return respuesta;
    }

    @Override
    public Mono<RespuestaPazYSalvoDTO> consultarPazYSalvoAsincrono(PeticionPazYSalvoDTO peticion) {
        Mono<List<RespuestaPazYSalvoDTOLaboratorio>> laboratorioMono = consultarAsincronoConReintentos(
            webClient.post()
                .uri(LABORATORIO_URL)
                .bodyValue(peticion)
                .retrieve()
                .bodyToFlux(RespuestaPazYSalvoDTOLaboratorio.class)
                .collectList(),
            "Laboratorio"
        ).doOnNext(lab -> notificar("laboratorio", lab));

        Mono<List<RespuestaPazYSalvoDTOFinanciera>> financieraMono = consultarAsincronoConReintentos(
            webClient.post()
                .uri(FINANCIERA_URL)
                .bodyValue(peticion)
                .retrieve()
                .bodyToFlux(RespuestaPazYSalvoDTOFinanciera.class)
                .collectList(),
            "Financiera"
        ).doOnNext(fin -> notificar("financiera", fin));

        Mono<List<RespuestaPazYSalvoDTODeportes>> deportesMono = consultarAsincronoConReintentos(
            webClient.post()
                .uri(DEPORTES_URL)
                .bodyValue(peticion)
                .retrieve()
                .bodyToFlux(RespuestaPazYSalvoDTODeportes.class)
                .collectList(),
            "Deportes"
        ).doOnNext(dep -> notificar("deportes", dep));

        return Mono.zip(laboratorioMono, financieraMono, deportesMono)
                .map(tuple -> {
                    RespuestaPazYSalvoDTO respuesta = new RespuestaPazYSalvoDTO();
                    respuesta.setCodigoEstudiante(peticion.getCodigoEstudiante());
                    respuesta.setObjLaboratorio(tuple.getT1());
                    respuesta.setObjFinanciera(tuple.getT2());
                    respuesta.setObjDeportes(tuple.getT3());
                    respuesta.setMensaje("Consulta asíncrona exitosa.");

                    // Notificar a administradores
                    notificarAdministradores(peticion.getCodigoEstudiante(), peticion.getNombresEstudiante());

                    // Notificar al estudiante
                    notificarEstudiante(peticion.getCodigoEstudiante(), respuesta);
                    return respuesta;
                })
                .onErrorResume(e -> {
                    RespuestaPazYSalvoDTO respuesta = new RespuestaPazYSalvoDTO();
                    respuesta.setCodigoEstudiante(peticion.getCodigoEstudiante());
                    respuesta.setMensaje("Error en la consulta asíncrona: " + e.getMessage());
                    return Mono.just(respuesta);
                });
    }

    @Override
    public void eliminarDeudasLaboratorio(PeticionPazYSalvoDTO peticion) {
        webClient.method(HttpMethod.DELETE)
                .uri(ELIMINAR_LABORATORIO_URL)
                .body(Mono.just(peticion), PeticionPazYSalvoDTO.class)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public void eliminarDeudasFinanciera(PeticionPazYSalvoDTO peticion) {
        webClient.delete()
                .uri(ELIMINAR_FINANCIERA_URL + peticion.getCodigoEstudiante())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public void eliminarDeudasDeportes(PeticionPazYSalvoDTO peticion) {
        webClient.delete()
                .uri(ELIMINAR_DEPORTES_URL + peticion.getCodigoEstudiante())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    // Método para realizar consultas síncronas con reintentos
    private <T> T consultarConReintentos(java.util.function.Supplier<T> consulta, String nombreServicio) {
        for (int intento = 1; intento <= MAX_INTENTOS; intento++) {
            try {
                System.out.println("Intentando conexión con el servicio " + nombreServicio + " (intento " + intento + "/" + MAX_INTENTOS + ")");
                
                T resultado = consulta.get();
                System.out.println("Conexión exitosa con el servicio " + nombreServicio);
                return resultado;
                
            } catch (WebClientException | RuntimeException e) {
                System.out.println("Error en intento " + intento + " para servicio " + nombreServicio + ": " + e.getMessage());
                
                if (intento == MAX_INTENTOS) {
                    System.out.println("Conexión fallida, no hubo contacto con el servicio " + nombreServicio);
                    return null;
                }
                
                // Esperar antes del siguiente intento
                try {
                    Thread.sleep(DELAY_REINTENTO);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }
        return null;
    }

    // Método para realizar consultas asíncronas con reintentos
    private <T> Mono<T> consultarAsincronoConReintentos(Mono<T> consulta, String nombreServicio) {
        return consulta
            .doOnSubscribe(sub -> System.out.println("Intentando conexión con el servicio " + nombreServicio + " (intento 1/" + MAX_INTENTOS + ")"))
            .doOnSuccess(result -> {
                if (result != null) {
                    System.out.println("Conexión exitosa con el servicio " + nombreServicio);
                }
            })
            .retryWhen(reactor.util.retry.Retry.fixedDelay(MAX_INTENTOS - 1, 
                java.time.Duration.ofMillis(DELAY_REINTENTO))
                .doBeforeRetry(signal -> {
                    int intentoActual = (int) signal.totalRetries() + 2;
                    System.out.println("Intentando conexión con el servicio " + nombreServicio + 
                        " (intento " + intentoActual + "/" + MAX_INTENTOS + ")");
                }))
            .doOnError(error -> System.out.println("Conexión fallida, no hubo contacto con el servicio " + nombreServicio))
            .onErrorReturn(null);
    }

    // Método para revertir operaciones parciales
    private void revertirOperacionesParciales(PeticionPazYSalvoDTO peticion, RespuestaPazYSalvoDTO respuesta) {
        if (respuesta.getObjLaboratorio() != null && !respuesta.getObjLaboratorio().isEmpty()) {
            try {
                webClient.method(HttpMethod.DELETE)
                    .uri(ELIMINAR_LABORATORIO_URL + "/" + peticion.getCodigoEstudiante())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .onErrorResume(err -> Mono.empty())
                    .block();
                System.out.println("Operación de laboratorio revertida para estudiante: " + peticion.getCodigoEstudiante());
            } catch (Exception e) {
                System.out.println("Error al revertir operación de laboratorio: " + e.getMessage());
            }
        }

        if (respuesta.getObjFinanciera() != null && !respuesta.getObjFinanciera().isEmpty()) {
            try {
                webClient.method(HttpMethod.DELETE)
                    .uri(ELIMINAR_FINANCIERA_URL + peticion.getCodigoEstudiante())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .onErrorResume(err -> Mono.empty())
                    .block();
                System.out.println("Operación financiera revertida para estudiante: " + peticion.getCodigoEstudiante());
            } catch (Exception e) {
                System.out.println("Error al revertir operación financiera: " + e.getMessage());
            }
        }

        if (respuesta.getObjDeportes() != null && !respuesta.getObjDeportes().isEmpty()) {
            try {
                webClient.method(HttpMethod.DELETE)
                    .uri(ELIMINAR_DEPORTES_URL + peticion.getCodigoEstudiante())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .onErrorResume(err -> Mono.empty())
                    .block();
                System.out.println("Operación de deportes revertida para estudiante: " + peticion.getCodigoEstudiante());
            } catch (Exception e) {
                System.out.println("Error al revertir operación de deportes: " + e.getMessage());
            }
        }
    }

    private void notificar(String canal, Object mensaje) {
        String destino = "/notificacion/" + canal;
        messagingTemplate.convertAndSend(destino, mensaje);
    }

    private void notificarEstudiante(String codigoEstudiante, Object mensaje) {
        String destino = "/notificacion/estudiante/" + codigoEstudiante;
        messagingTemplate.convertAndSend(destino, mensaje);
    }

    private void notificarAdministradores(String codigoEstudiante, String nombres) {
        String mensaje = String.format(
                "El estudiante con código %s y nombres %s ha realizado una nueva solicitud de paz y salvo",
                codigoEstudiante, nombres);
        messagingTemplate.convertAndSend("/notificacion/general/laboratorio", mensaje);
        messagingTemplate.convertAndSend("/notificacion/general/financiera", mensaje);
        messagingTemplate.convertAndSend("/notificacion/general/deportes", mensaje);
    }
}