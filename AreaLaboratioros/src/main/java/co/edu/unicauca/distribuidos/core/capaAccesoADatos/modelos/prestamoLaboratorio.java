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
<<<<<<< HEAD
    private String equipoPrestado;
    private String estadoPrestamo;
=======
    private String nombreEstudiante;
    private String equipo;
    private String estado;
>>>>>>> e25775d1a30473dfd8c8a9c36990373fb254f017
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEstimada;
    private LocalDate fechaDevolucionReal;
}