package co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaPrestamoDTOLaboratorio {
    private String equipo;
    private String estado; // activo, vencido
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEstimada;
    private LocalDate fechaDevolucionReal;
}