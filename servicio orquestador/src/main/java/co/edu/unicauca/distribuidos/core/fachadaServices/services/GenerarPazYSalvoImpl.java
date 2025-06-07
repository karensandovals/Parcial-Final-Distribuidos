package co.edu.unicauca.distribuidos.core.fachadaServices.services;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

        private static final String URL_LAB = "http://localhost:xxxx/api/xxxx";
        private static final String URL_FIN = "http://localhost:xxxx/api/xxxx";
        private static final String URL_DEP = "http://localhost:xxxx/api/xxxx";

        private static final String TOPIC_LAB = "/notificacion/admin/laboratorio";
        private static final String TOPIC_FIN = "/notificacion/admin/financiera";
        private static final String TOPIC_DEP = "/notificacion/admin/deportes";

        @Override
        public RespuestaPazYSalvoDTO verificarPazYSalvo(PeticionPazYSalvoDTO objPeticion) {
                // 1) Notificación inicial a los 3 admins
                String notiInicio = String.format(
                                "El estudiante con código %s y nombres %s ha realizado una nueva solicitud de paz y salvo",
                                objPeticion.getCodigoEstudiante(), objPeticion.getNombresEstudiante());
                messagingTemplate.convertAndSend(TOPIC_LAB, notiInicio);
                messagingTemplate.convertAndSend(TOPIC_FIN, notiInicio);
                messagingTemplate.convertAndSend(TOPIC_DEP, notiInicio);

                RespuestaPazYSalvoDTO objRespuestaPazYSalvo = new RespuestaPazYSalvoDTO();

                try {
                        // 2. Consultar al servicio del area de laboratorios sobre el estado del
                        // estudiante
                        List<RespuestaPazYSalvoDTOLaboratorio> objRespuestaLaboratorio = webClient.post()
                                        .uri(URL_LAB)
                                        .bodyValue(objPeticion)
                                        .retrieve()
                                        .bodyToFlux(RespuestaPazYSalvoDTOLaboratorio.class)
                                        .collectList()
                                        .block(); // sincrono
                        objRespuestaPazYSalvo.setObjLaboratorio(
                                        objRespuestaLaboratorio != null ? objRespuestaLaboratorio
                                                        : Collections.emptyList());

                        // 3. Consultar al servicio del area de financiera sobre el estado del
                        // estudiante
                        List<RespuestaPazYSalvoDTOFinanciera> objRespuestaFinanciera = webClient.post()
                                        .uri(URL_FIN)
                                        .bodyValue(objPeticion)
                                        .retrieve()
                                        .bodyToFlux(RespuestaPazYSalvoDTOFinanciera.class)
                                        .collectList()
                                        .block(); // sincrono
                        objRespuestaPazYSalvo.setObjFinanciera(
                                        objRespuestaFinanciera != null ? objRespuestaFinanciera
                                                        : Collections.emptyList());

                        // 4. Consultar al servicio del area de deportes sobre el estado del estudiante
                        List<RespuestaPazYSalvoDTODeportes> objRespuestaDeportes = webClient.post()
                                        .uri(URL_DEP)
                                        .bodyValue(objPeticion)
                                        .retrieve()
                                        .bodyToFlux(RespuestaPazYSalvoDTODeportes.class)
                                        .collectList()
                                        .block(); // sincrono
                        objRespuestaPazYSalvo
                                        .setObjDeportes(objRespuestaDeportes != null ? objRespuestaDeportes
                                                        : Collections.emptyList());

                        // 5. Determinar paz y salvo global
                        boolean ok = objRespuestaPazYSalvo.getObjLaboratorio().isEmpty()
                                        && objRespuestaPazYSalvo.getObjFinanciera().isEmpty()
                                        && objRespuestaPazYSalvo.getObjDeportes().isEmpty();

                        if (ok) {
                                objRespuestaPazYSalvo.setMensaje("El estudiante se encuentra a paz y salvo");
                                // 6. Notificar a los admins por área
                                messagingTemplate.convertAndSend(TOPIC_LAB, objRespuestaPazYSalvo.getMensaje());
                                messagingTemplate.convertAndSend(TOPIC_FIN, objRespuestaPazYSalvo.getMensaje());
                                messagingTemplate.convertAndSend(TOPIC_DEP, objRespuestaPazYSalvo.getMensaje());
                        }

                } catch (Exception e) {
                        // manejo de reintentos, timeouts, etc. según enunciado
                        objRespuestaPazYSalvo.setMensaje("No se pudo generar el paz y salvo");

                        String mensajeLab = objRespuestaPazYSalvo.getMensaje()
                                        + consultarPrestamosDeEquiposDeLaboratorio(objRespuestaPazYSalvo);
                        String mensajeFin = objRespuestaPazYSalvo.getMensaje()
                                        + consultarDeudasFinancieras(objRespuestaPazYSalvo);
                        String mensajeDep = objRespuestaPazYSalvo.getMensaje()
                                        + consultarImplementosDeportivosNoRetornados(objRespuestaPazYSalvo);

                        System.out.println(e.getMessage());

                        messagingTemplate.convertAndSend(TOPIC_LAB, mensajeLab);
                        messagingTemplate.convertAndSend(TOPIC_FIN, mensajeFin);
                        messagingTemplate.convertAndSend(TOPIC_DEP, mensajeDep);
                }

                return objRespuestaPazYSalvo;
        }

        @Override
        public Mono<RespuestaPazYSalvoDTO> verificarPazYSalvoAsincrono(PeticionPazYSalvoDTO peticion) {
                // Notificación inicial
                String notiInicio = String.format(
                                "El estudiante con código %s y nombres %s ha realizado una nueva solicitud de paz y salvo",
                                peticion.getCodigoEstudiante(), peticion.getNombresEstudiante());

                messagingTemplate.convertAndSend(TOPIC_LAB, notiInicio);
                messagingTemplate.convertAndSend(TOPIC_FIN, notiInicio);
                messagingTemplate.convertAndSend(TOPIC_DEP, notiInicio);

                RespuestaPazYSalvoDTO objRespuestaPazYSalvo = new RespuestaPazYSalvoDTO();

                // Llamadas asíncronas a los servicios de paz y salvo
                // Lllamadas al servicio del area de laboratorios
                Mono<List<RespuestaPazYSalvoDTOLaboratorio>> objRespuestaLaboratorio = webClient.post()
                                .uri(URL_LAB)
                                .bodyValue(peticion)
                                .retrieve()
                                .bodyToFlux(RespuestaPazYSalvoDTOLaboratorio.class)
                                .collectList()
                                .doOnError(e -> System.err
                                                .println("Error verificando el paz y salvo en el area de laboratorios: "
                                                                + e.getMessage()))
                                .onErrorReturn(Collections.emptyList());

                // Llamadas al servicio del area de financiera
                Mono<List<RespuestaPazYSalvoDTOFinanciera>> objRespuestaFinanciera = webClient.post()
                                .uri(URL_FIN)
                                .bodyValue(peticion)
                                .retrieve()
                                .bodyToFlux(RespuestaPazYSalvoDTOFinanciera.class)
                                .collectList()
                                .doOnError(e -> System.err
                                                .println("Error verificando el paz y salvo en el area de financiera: "
                                                                + e.getMessage()))
                                .onErrorReturn(Collections.emptyList());

                Mono<List<RespuestaPazYSalvoDTODeportes>> objRespuestaDeportes = webClient.post()
                                .uri(URL_DEP)
                                .bodyValue(peticion)
                                .retrieve()
                                .bodyToFlux(RespuestaPazYSalvoDTODeportes.class)
                                .collectList()
                                .doOnError(e -> System.err
                                                .println("Error verificando el paz y salvo en el area de deportes: "
                                                                + e.getMessage()))
                                .onErrorReturn(Collections.emptyList());

                // Composición de respuestas
                return Mono.zip(objRespuestaLaboratorio, objRespuestaFinanciera, objRespuestaDeportes)
                                .map(results -> {
                                        objRespuestaPazYSalvo.setObjLaboratorio(results.getT1());
                                        objRespuestaPazYSalvo.setObjFinanciera(results.getT2());
                                        objRespuestaPazYSalvo.setObjDeportes(results.getT3());

                                        boolean ok = results.getT1().isEmpty()
                                                        && results.getT2().isEmpty()
                                                        && results.getT3().isEmpty();

                                        if (ok) {
                                                objRespuestaPazYSalvo
                                                                .setMensaje("El estudiante se encuentra a paz y salvo");
                                                // 6. Notificar a los admins por área
                                                messagingTemplate.convertAndSend(TOPIC_LAB,
                                                                objRespuestaPazYSalvo.getMensaje());
                                                messagingTemplate.convertAndSend(TOPIC_FIN,
                                                                objRespuestaPazYSalvo.getMensaje());
                                                messagingTemplate.convertAndSend(TOPIC_DEP,
                                                                objRespuestaPazYSalvo.getMensaje());
                                        }
                                        return objRespuestaPazYSalvo;
                                })
                                .onErrorResume(error -> {
                                        RespuestaPazYSalvoDTO respuesta = new RespuestaPazYSalvoDTO();
                                        respuesta.setMensaje("No se pudo generar el paz y salvo");

                                        String mensajeLab = respuesta.getMensaje()
                                                        + consultarPrestamosDeEquiposDeLaboratorio(
                                                                        objRespuestaPazYSalvo);
                                        String mensajeFin = respuesta.getMensaje()
                                                        + consultarDeudasFinancieras(objRespuestaPazYSalvo);
                                        String mensajeDep = respuesta.getMensaje()
                                                        + consultarImplementosDeportivosNoRetornados(
                                                                        objRespuestaPazYSalvo);

                                        messagingTemplate.convertAndSend(TOPIC_LAB, mensajeLab);
                                        messagingTemplate.convertAndSend(TOPIC_FIN, mensajeFin);
                                        messagingTemplate.convertAndSend(TOPIC_DEP, mensajeDep);

                                        return Mono.just(respuesta);
                                });
        }

        private String consultarPrestamosDeEquiposDeLaboratorio(RespuestaPazYSalvoDTO objPazYSalvo) {
                List<RespuestaPazYSalvoDTOLaboratorio> objLaboratorio = webClient.post()
                                .uri(URL_LAB)
                                .bodyValue(objPazYSalvo.getCodigoEstudiante())
                                .retrieve()
                                .bodyToFlux(RespuestaPazYSalvoDTOLaboratorio.class)
                                .collectList()
                                .block();

                if (objLaboratorio != null && !objLaboratorio.isEmpty()) {
                        StringBuilder mensaje = new StringBuilder("Laboratorio: Préstamos activos:\n");
                        objLaboratorio.forEach(p -> mensaje.append(p.toString()).append("\n"));
                        return mensaje.toString();
                }
                return "";
        }

        private String consultarDeudasFinancieras(RespuestaPazYSalvoDTO objPazYSalvo) {
                List<RespuestaPazYSalvoDTOFinanciera> objFinanciera = webClient.post()
                                .uri(URL_FIN)
                                .bodyValue(objPazYSalvo.getCodigoEstudiante())
                                .retrieve()
                                .bodyToFlux(RespuestaPazYSalvoDTOFinanciera.class)
                                .collectList()
                                .block();

                if (objFinanciera != null && !objFinanciera.isEmpty()) {
                        StringBuilder mensaje = new StringBuilder("Financiera: Deudas encontradas:\n");
                        objFinanciera.forEach(d -> mensaje.append(d.toString()).append("\n"));
                        return mensaje.toString();
                }
                return "";
        }

        private String consultarImplementosDeportivosNoRetornados(RespuestaPazYSalvoDTO objPazYSalvo) {
                List<RespuestaPazYSalvoDTODeportes> objDeportes = webClient.post()
                                .uri(URL_DEP)
                                .bodyValue(objPazYSalvo.getCodigoEstudiante())
                                .retrieve()
                                .bodyToFlux(RespuestaPazYSalvoDTODeportes.class)
                                .collectList()
                                .block();

                if (objDeportes != null && !objDeportes.isEmpty()) {
                        StringBuilder mensaje = new StringBuilder("Deportes: Implementos no retornados:\n");
                        objDeportes.forEach(i -> mensaje.append(i.toString()).append("\n"));
                        return mensaje.toString();
                }
                return "";
        }

}
