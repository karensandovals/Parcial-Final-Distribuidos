package co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaPrestamoDTOLaboratorio {
    private String codigoEstudiante;
    private String nombreEstudiante;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEstimada;
    private LocalDate fechaDevolucionReal;
    private String estadoPrestamo; // activo, devuelto, vencido
    private String equipoPrestado;
}