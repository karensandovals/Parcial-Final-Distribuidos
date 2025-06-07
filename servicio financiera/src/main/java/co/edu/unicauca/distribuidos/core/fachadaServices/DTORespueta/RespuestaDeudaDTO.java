package co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta;


import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaDeudaDTO {
    private String codigoEstudiante;
    private double monto;
    private String motivo;
    private Date fechaGeneracion;
    private Date fechaLimite;
    private String estado; // pendiente, pagada, en mora
}
