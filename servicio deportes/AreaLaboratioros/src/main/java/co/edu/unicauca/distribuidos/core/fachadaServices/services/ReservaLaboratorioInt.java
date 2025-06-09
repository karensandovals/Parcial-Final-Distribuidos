package co.edu.unicauca.distribuidos.core.fachadaServices.services;

import co.edu.unicauca.distribuidos.core.fachadaServices.DTOPeticion.PeticionPrestamoDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta.RespuestaPrestamoDTOLaboratorio;

import java.util.List;

public interface ReservaLaboratorioInt {
    List<RespuestaPrestamoDTOLaboratorio> consultarPrestamosPendientes(PeticionPrestamoDTO peticion);
    boolean eliminarPrestamosPendientes(PeticionPrestamoDTO peticion);
}