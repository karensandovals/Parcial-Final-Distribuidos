package co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaPrestamoDTOLaboratorio {
<<<<<<< HEAD
    private String codigoEstudiante;
=======
     private String nombreEstudiante;
    private String equipo;
    private String estado;
>>>>>>> e25775d1a30473dfd8c8a9c36990373fb254f017
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEstimada;
    private LocalDate fechaDevolucionReal;
    private String estadoPrestamo; // activo, devuelto, vencido
    private String equipoPrestado;
}