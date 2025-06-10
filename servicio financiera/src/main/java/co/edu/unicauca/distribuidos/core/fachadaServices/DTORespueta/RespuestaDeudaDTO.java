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
    private String nombresEstudiante;
    private double montoAdeudado;
    private String motivoDeuda;
    private Date fechaGeneracionDeuda;
    private Date fechaLimitePago;
    private String estadoDeuda; // pendiente, pagada, en mora
}
