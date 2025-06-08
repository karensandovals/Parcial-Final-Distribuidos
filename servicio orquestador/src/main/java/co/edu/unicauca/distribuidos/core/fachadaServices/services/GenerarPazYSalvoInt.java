package co.edu.unicauca.distribuidos.core.fachadaServices.services;


import co.edu.unicauca.distribuidos.core.fachadaServices.DTOPeticion.PeticionPazYSalvoDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta.RespuestaPazYSalvoDTO;
import reactor.core.publisher.Mono;

public interface GenerarPazYSalvoInt {
    public RespuestaPazYSalvoDTO consultarPazYSalvo(PeticionPazYSalvoDTO peticion);
    public Mono<RespuestaPazYSalvoDTO> consultarPazYSalvoAsincrono(PeticionPazYSalvoDTO peticion);
    public void eliminarDeudasLaboratorio(PeticionPazYSalvoDTO peticion);
    public void eliminarDeudasFinanciera(PeticionPazYSalvoDTO peticion);
    public void eliminarDeudasDeportes(PeticionPazYSalvoDTO peticion);
}
