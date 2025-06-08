package co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaPrestamoDTOLaboratorio {
    private String codigoEstudiante;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEstimada;
    private LocalDate fechaDevolucionReal;
    private String estadoPrestamo; // activo, devuelto, vencido
    private String equipoPrestado;
}