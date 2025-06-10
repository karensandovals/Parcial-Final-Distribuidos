package co.edu.unicauca.distribuidos.core.fachadaServices.services;

import java.util.List;

import co.edu.unicauca.distribuidos.core.fachadaServices.DTOPeticion.PeticionImplementoDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespuesta.RespuestaPazYSalvoDTODeportes;

public interface DeportesServiceInt {
    List<RespuestaPazYSalvoDTODeportes> crearImplemento(PeticionImplementoDTO objPeticion);

    public boolean eliminarImplemento(String codigoEstudiante);
     
}
