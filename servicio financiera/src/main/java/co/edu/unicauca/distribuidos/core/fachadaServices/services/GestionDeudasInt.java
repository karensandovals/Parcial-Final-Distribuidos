package co.edu.unicauca.distribuidos.core.fachadaServices.services;

import co.edu.unicauca.distribuidos.core.fachadaServices.DTOPeticion.PeticionDeudaDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta.RespuestaDeudaDTO;

import java.util.List;

public interface GestionDeudasInt {
    List<RespuestaDeudaDTO> consultarDeudas(PeticionDeudaDTO objPeticion);
    Boolean eliminarDeudas(String codigoEstudiante);
}