package co.edu.unicauca.distribuidos.core.modelos;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor 
@NoArgsConstructor  
public class Deuda {
    private String codigoEstudiante;
    private String nombresEstudiante;
    private double monto;
    private String motivo;
    private LocalDate fechaGeneracion;
    private LocalDate fechaLimite;
    private String estado; // pendiente, pagada, en mora
}