package co.edu.unicauca.distribuidos.core.capaAccesoADatos.repositorios;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import co.edu.unicauca.distribuidos.core.capaAccesoADatos.modelos.ImplementoDeportivo;

@Repository
public class ImplementoRepository {

    private Map<String, List<ImplementoDeportivo>> implementosPorEstudiante;
    private Map<String, String> nombresPorCodigo;

    public ImplementoRepository() {
        this.implementosPorEstudiante = new HashMap<>();
        this.nombresPorCodigo = new HashMap<>();

        // Juan Pérez
        List<ImplementoDeportivo> implementosJuan = new ArrayList<>();
        implementosJuan.add(new ImplementoDeportivo("202012345", "Andrés Sánchez", LocalDate.of(2024, 8, 1),
                LocalDate.of(2025, 8, 10), null, "Balón"));
        implementosJuan.add(new ImplementoDeportivo("202012345", "Andrés Sánchez", LocalDate.of(2024, 8, 5),
                LocalDate.of(2025, 8, 15), null, "Raqueta"));

        // Ana Gómez
        List<ImplementoDeportivo> implementosAna = new ArrayList<>();
        implementosAna.add(new ImplementoDeportivo("202087654", "Ana Gómez", LocalDate.of(2024, 1, 12),
                LocalDate.of(2025, 1, 20), null, "Uniforme"));

        List<ImplementoDeportivo> implementosPedro = new ArrayList<>();
        implementosPedro.add(new ImplementoDeportivo("202056789", "Pedro Ruiz", LocalDate.of(2024, 5, 2),
                LocalDate.of(2024, 5, 12), null, "Pesas"));
        implementosPedro.add(new ImplementoDeportivo("202056789", "Pedro Ruiz", LocalDate.of(2024, 5, 3),
                LocalDate.of(2024, 5, 10), null, "Colchoneta"));

        // No se agregan préstamos de laboratorio ni deudas financieras

        this.implementosPorEstudiante.put("202012345", implementosJuan);
        this.implementosPorEstudiante.put("202087654", implementosAna);
        this.implementosPorEstudiante.put("202056789", implementosPedro);
        this.nombresPorCodigo.put("202012345", "Juan Pérez");
        this.nombresPorCodigo.put("202087654", "Ana Gómez");
        
    }

    public List<ImplementoDeportivo> buscarPorCodigo(String codigoEstudiante) {
        return this.implementosPorEstudiante.getOrDefault(codigoEstudiante, new ArrayList<>());
    }

    public boolean eliminarPorCodigo(String codigoEstudiante) {
        return this.implementosPorEstudiante.remove(codigoEstudiante) != null;
    }

    public Map<String, String> obtenerNombresPorCodigo() {
        return this.nombresPorCodigo;
    }
}
