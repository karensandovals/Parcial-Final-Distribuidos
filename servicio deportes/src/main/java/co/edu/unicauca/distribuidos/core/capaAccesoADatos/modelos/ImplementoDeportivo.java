package co.edu.unicauca.distribuidos.core.capaAccesoADatos.modelos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImplementoDeportivo {
    private String codigoEstudiante;
    private String nombresEstudiante;
    private String nombreImplemento;
    private String fechaPrestamo;
    private String fechaDevolucionEstimada;
    private String fechaDevolucionReal;
}
