package co.edu.unicauca.distribuidos.core.fachadaServices.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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

    private static final Logger logger = LoggerFactory.getLogger(GenerarPazYSalvoImpl.class);

    private int contadorFallosSimulados = 0;
    private final int MAX_FALLOS_SIMULADOS = 3; // Simular fallo las primeras 3 veces

    @Override
    public RespuestaPazYSalvoDTO consultarPazYSalvo(PeticionPazYSalvoDTO peticion) {
        int maxIntentos = 3;
        int intento = 0;
        while (intento < maxIntentos) {
            try {
                intento++;

                // Simular fallo solo en el primer intento de la primera vez que se llama al
                // método
                if (contadorFallosSimulados < MAX_FALLOS_SIMULADOS) {
                    contadorFallosSimulados++;
                    logger.warn("🔥 Simulando fallo en el orquestador (intento {})", intento);
                    throw new RuntimeException("Fallo simulado en el orquestador.");
                }

                // --- Lógica normal si no hay fallo ---
                RespuestaPazYSalvoDTO respuesta = new RespuestaPazYSalvoDTO();
                respuesta.setCodigoEstudiante(peticion.getCodigoEstudiante());

                // Notificar a administradores
                notificarAdministradores(peticion.getCodigoEstudiante(), peticion.getNombresEstudiante());

                // 1. Laboratorio
                List<RespuestaPazYSalvoDTOLaboratorio> laboratorio = null;
                try {
                    laboratorio = webClient.post()
                            .uri(LABORATORIO_URL)
                            .bodyValue(peticion)
                            .retrieve()
                            .bodyToFlux(RespuestaPazYSalvoDTOLaboratorio.class)
                            .collectList()
                            .block();
                    respuesta.setObjLaboratorio(laboratorio);
                    notificar("laboratorio", laboratorio);
                } catch (Exception ex) {
                    String msg = "Error al consultar el servicio de Laboratorio: " + ex.getMessage();
                    logger.error("❌ {}", msg);
                    respuesta.setMensaje(msg);
                    return respuesta;
                }

                // 2. Financiera
                List<RespuestaPazYSalvoDTOFinanciera> financiera = null;
                try {
                    financiera = webClient.post()
                            .uri(FINANCIERA_URL)
                            .bodyValue(peticion)
                            .retrieve()
                            .bodyToFlux(RespuestaPazYSalvoDTOFinanciera.class)
                            .collectList()
                            .block();
                    respuesta.setObjFinanciera(financiera);
                    notificar("financiera", financiera);
                } catch (Exception ex) {
                    String msg = "Error al consultar el servicio Financiero: " + ex.getMessage();
                    logger.error("❌ {}", msg);
                    respuesta.setMensaje(msg);
                    return respuesta;
                }

                // 3. Deportes
                List<RespuestaPazYSalvoDTODeportes> deportes = null;
                try {
                    deportes = webClient.post()
                            .uri(DEPORTES_URL)
                            .bodyValue(peticion)
                            .retrieve()
                            .bodyToFlux(RespuestaPazYSalvoDTODeportes.class)
                            .collectList()
                            .block();
                    respuesta.setObjDeportes(deportes);
                    notificar("deportes", deportes);
                } catch (Exception ex) {
                    String msg = "Error al consultar el servicio de Deportes: " + ex.getMessage();
                    logger.error("❌ {}", msg);
                    respuesta.setMensaje(msg);
                    return respuesta;
                }

                respuesta.setMensaje("Consulta completada con éxito.");

                // Notificar al estudiante
                notificarEstudiante(peticion.getCodigoEstudiante(), respuesta);

                return respuesta;

            } catch (Exception e) {
                logger.error("❌ Error en intento {}: {}", intento, e.getMessage());
                if (intento >= maxIntentos) {
                    RespuestaPazYSalvoDTO respuestaError = new RespuestaPazYSalvoDTO();
                    respuestaError.setCodigoEstudiante(peticion.getCodigoEstudiante());
                    respuestaError.setMensaje("Error durante la consulta: " + e.getMessage());
                    return respuestaError;
                }
                try {
                    Thread.sleep(1000); // Esperar 1 segundo antes de reintentar
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // Fallback de seguridad (no debería alcanzarse)
        RespuestaPazYSalvoDTO respuestaFinal = new RespuestaPazYSalvoDTO();
        respuestaFinal.setCodigoEstudiante(peticion.getCodigoEstudiante());
        respuestaFinal.setMensaje("Error inesperado.");
        return respuestaFinal;
    }

    @Override
    public Mono<RespuestaPazYSalvoDTO> consultarPazYSalvoAsincrono(PeticionPazYSalvoDTO peticion) {
        Mono<List<RespuestaPazYSalvoDTOLaboratorio>> laboratorioMono = webClient.post()
                .uri(LABORATORIO_URL)
                .bodyValue(peticion)
                .retrieve()
                .bodyToFlux(RespuestaPazYSalvoDTOLaboratorio.class)
                .collectList()
                .doOnNext(lab -> notificar("laboratorio", lab));

        Mono<List<RespuestaPazYSalvoDTOFinanciera>> financieraMono = webClient.post()
                .uri(FINANCIERA_URL)
                .bodyValue(peticion)
                .retrieve()
                .bodyToFlux(RespuestaPazYSalvoDTOFinanciera.class)
                .collectList()
                .doOnNext(fin -> notificar("financiera", fin));

        Mono<List<RespuestaPazYSalvoDTODeportes>> deportesMono = webClient.post()
                .uri(DEPORTES_URL)
                .bodyValue(peticion)
                .retrieve()
                .bodyToFlux(RespuestaPazYSalvoDTODeportes.class)
                .collectList()
                .doOnNext(dep -> notificar("deportes", dep));

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
