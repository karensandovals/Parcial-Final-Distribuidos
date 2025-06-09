package co.edu.unicauca.distribuidos.core.capaAccesoADatos.repositorios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
        implementosJuan.add(new ImplementoDeportivo("202012345", "Balón", "2024-08-01", "2024-08-10", null));
        implementosJuan.add(new ImplementoDeportivo("202012345", "Raqueta", "2024-08-05", "2024-08-15", null));

        // Ana Gómez
        List<ImplementoDeportivo> implementosAna = new ArrayList<>();
        implementosAna.add(new ImplementoDeportivo("202087654", "Uniforme", "2025-01-12", "2025-01-20", null));

        this.implementosPorEstudiante.put("202012345", implementosJuan);
        this.implementosPorEstudiante.put("202087654", implementosAna);

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
