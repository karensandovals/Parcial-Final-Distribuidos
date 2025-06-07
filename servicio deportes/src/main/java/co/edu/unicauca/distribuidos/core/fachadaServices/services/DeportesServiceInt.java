package co.edu.unicauca.distribuidos.core.fachadaServices.services;

import co.edu.unicauca.distribuidos.core.fachadaServices.DTOPeticion.PeticionImplementoDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespuesta.RespuestaReporteDTO;

public interface DeportesServiceInt {
    public RespuestaReporteDTO crearImplemento(PeticionImplementoDTO objPeticion);
    public boolean eliminarImplemento(String codigoEstudiante);
     
}
