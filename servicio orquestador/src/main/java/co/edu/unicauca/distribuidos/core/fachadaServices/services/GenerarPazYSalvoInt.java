package co.edu.unicauca.distribuidos.core.fachadaServices.services;

import co.edu.unicauca.distribuidos.core.fachadaServices.DTOPeticion.PeticionPazYSalvoDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta.RespuestaPazYSalvoDTO;
import reactor.core.publisher.Mono;

public interface GenerarPazYSalvoInt {
    public RespuestaPazYSalvoDTO verificarPazYSalvo(PeticionPazYSalvoDTO peticion);
    public Mono<RespuestaPazYSalvoDTO> verificarPazYSalvoAsincrono(PeticionPazYSalvoDTO peticion);
}
