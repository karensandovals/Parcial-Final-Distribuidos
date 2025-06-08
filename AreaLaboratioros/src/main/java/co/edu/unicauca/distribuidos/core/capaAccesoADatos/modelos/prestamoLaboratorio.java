package co.edu.unicauca.distribuidos.core.capaAccesoADatos.modelos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class prestamoLaboratorio {
    private String codigoEstudiante;
    private String equipoPrestado;
    private String estadoPrestamo;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEstimada;
    private LocalDate fechaDevolucionReal;
}