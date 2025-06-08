package co.edu.unicauca.distribuidos.core.capaAccesoADatos.modelos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class prestamoLaboratorio {
    private String codigoEstudiante;
    private String nombreEstudiante;
    private String equipo;
    private String estado;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEstimada;
    private LocalDate fechaDevolucionReal;
}