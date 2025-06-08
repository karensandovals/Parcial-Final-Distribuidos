package co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta;


import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaPazYSalvoDTOLaboratorio {
    private String codigoEstudiante;
    private Date fechaPrestamo;
    private Date fechaDevolucionEstimada;
    private Date fechaDevolucionReal;
    private String estadoPrestamo; // activo, devuelto, vencido
    private String equipoPrestado;
}