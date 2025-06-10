package co.edu.unicauca.distribuidos.core.capaAccesoADatos.modelos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImplementoDeportivo {
    private String codigoEstudiante;
    private String nombresEstudiante;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEstimada;
    private LocalDate fechaDevolucionReal;
    private String implementoDeportivoPrestado;
}
