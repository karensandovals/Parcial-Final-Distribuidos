package co.edu.unicauca.distribuidos.core.fachadaServices.DTORespuesta;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaReporteDTO {
    private String codigoEstudiante;
    private String nombreEstudiante;
    private boolean estaPazYSalvo;
    private List<RespuestaPazYSalvoDTODeportes> implementosPendientes;
}
