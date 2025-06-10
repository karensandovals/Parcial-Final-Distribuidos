package co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaPazYSalvoDTODeportes {    
    private String codigoEstudiante;
    private String nombresEstudiante;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEstimada;
    private LocalDate fechaDevolucionReal;  
    private String nombreImplemento; 
}
