package co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta;

import java.util.List;

import lombok.Data;

@Data
public class RespuestaPazYSalvoDTO {
    private String codigoEstudiante;
    private String nombresEstudiante;
    private String mensaje; 
    private List<RespuestaPazYSalvoDTOLaboratorio> objLaboratorio;
    private List<RespuestaPazYSalvoDTOFinanciera> objFinanciera;
    private List<RespuestaPazYSalvoDTODeportes> objDeportes;  
}
