package co.edu.unicauca.distribuidos.core.fachadaServices.services;

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

    private final String LABORATORIO_URL = "http://localhost:2020/api/laboratorio/consultar";
    private final String FINANCIERA_URL = "http://localhost:5003/api/deudas";
    private final String DEPORTES_URL = "http://localhost:5008/api/deportes";

    private final String ELIMINAR_LABORATORIO_URL = "http://localhost:2020/api/laboratorio/eliminar";
    private final String ELIMINAR_FINANCIERA_URL = "http://localhost:5003/api/deudas/";
    private final String ELIMINAR_DEPORTES_URL = "http://localhost:5008/api/deportes/";


    @Override
    public RespuestaPazYSalvoDTO consultarPazYSalvo(PeticionPazYSalvoDTO peticion) {
        RespuestaPazYSalvoDTO respuesta = new RespuestaPazYSalvoDTO();
        respuesta.setCodigoEstudiante(peticion.getCodigoEstudiante());

        // Notificar a administradores
        notificarAdministradores(peticion.getCodigoEstudiante(), peticion.getNombresEstudiante());

        try {
            // 1. Consultar Laboratorio
            List<RespuestaPazYSalvoDTOLaboratorio> laboratorio = webClient.post()
                    .uri(LABORATORIO_URL)
                    .bodyValue(peticion)
                    .retrieve()
                    .bodyToFlux(RespuestaPazYSalvoDTOLaboratorio.class)
                    .collectList()
                    .block();

            respuesta.setObjLaboratorio(laboratorio);
            notificar("laboratorio", laboratorio);

            // 2. Consultar Financiera
            List<RespuestaPazYSalvoDTOFinanciera> financiera = webClient.post()
                    .uri(FINANCIERA_URL)
                    .bodyValue(peticion)
                    .retrieve()
                    .bodyToFlux(RespuestaPazYSalvoDTOFinanciera.class)
                    .collectList()
                    .block();

            respuesta.setObjFinanciera(financiera);
            notificar("financiera", financiera);

            // 3. Consultar Deportes
            List<RespuestaPazYSalvoDTODeportes> deportes = webClient.post()
                    .uri(DEPORTES_URL)
                    .bodyValue(peticion)
                    .retrieve()
                    .bodyToFlux(RespuestaPazYSalvoDTODeportes.class)
                    .collectList()
                    .block();

            respuesta.setObjDeportes(deportes);
            notificar("deportes", deportes);

            respuesta.setMensaje("Consulta completada con éxito.");

            // Notificar tambien al estudiante
            notificarEstudiante(peticion.getCodigoEstudiante(), respuesta);

        } catch (Exception e) {
            respuesta.setMensaje("Error durante la consulta: " + e.getMessage());
        }

        return respuesta;
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
                    respuesta.setMensaje("Consulta asincrónica exitosa.");

                    // Notificar a administradores
                    notificarAdministradores(peticion.getCodigoEstudiante(), peticion.getNombresEstudiante());

                    // Notificar al estudiante
                    notificarEstudiante(peticion.getCodigoEstudiante(), respuesta);
                    return respuesta;
                })
                .onErrorResume(e -> {
                    RespuestaPazYSalvoDTO respuesta = new RespuestaPazYSalvoDTO();
                    respuesta.setCodigoEstudiante(peticion.getCodigoEstudiante());
                    respuesta.setMensaje("Error en la consulta asincrónica: " + e.getMessage());
                    return Mono.just(respuesta);
                });
    }

    @Override
    public void eliminarDeudasLaboratorio(PeticionPazYSalvoDTO peticion) {
        webClient.post()
                .uri(ELIMINAR_LABORATORIO_URL)
                .bodyValue(peticion)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public void eliminarDeudasFinanciera(PeticionPazYSalvoDTO peticion) {
        webClient.post()
                .uri(ELIMINAR_FINANCIERA_URL)
                .bodyValue(peticion)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public void eliminarDeudasDeportes(PeticionPazYSalvoDTO peticion) {
        webClient.post()
                .uri(ELIMINAR_DEPORTES_URL)
                .bodyValue(peticion)
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
