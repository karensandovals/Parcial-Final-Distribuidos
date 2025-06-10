package co.edu.unicauca.distribuidos.core.repositorios;

import java.time.LocalDate;
import java.util.*;

import org.springframework.stereotype.Repository;

import co.edu.unicauca.distribuidos.core.modelos.Deuda;

@Repository
// Repositorio para manejar las deudas
public class DeudaRepositorio {
    private Map<String, List<Deuda>> deudasPorEstudiante;
    // private Map<String, String> nombresPorCodigo;

    public DeudaRepositorio() {
        this.deudasPorEstudiante = new HashMap<>();
        // this.nombresPorCodigo = new HashMap<>();

        // Deudas precargadas (simuladas en RAM)

        List<Deuda> deudasJuan = new ArrayList<>();
        deudasJuan.add(new Deuda("202012345", "Andrés Sánchez", 150000, "Mora en pago de matrícula",
                LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 31), "pendiente"));
        deudasJuan.add(new Deuda("202012345", "Andrés Sánchez", 25000, "Pérdida de material",
                LocalDate.of(2024, 9, 5), LocalDate.of(2024, 9, 30), "pendiente"));

        List<Deuda> deudasAna = new ArrayList<>();
        deudasAna.add(new Deuda("202087654", "Ana Gómez", 50000, "Mora en pago de matrícula",
                LocalDate.of(2025, 1, 10), LocalDate.of(2025, 2, 10), "en mora"));

        List<Deuda> deudasCarla = new ArrayList<>();
        deudasCarla.add(new Deuda("202078901", "Carla Rodríguez", 120000, "Mora en matrícula",
                LocalDate.of(2024, 6, 5), LocalDate.of(2024, 7, 5), "pendiente"));
                
        this.deudasPorEstudiante.put("202012345", deudasJuan);
        this.deudasPorEstudiante.put("202087654", deudasAna);
        this.deudasPorEstudiante.put("202078901", deudasCarla);

        // 🔽 Nombres precargados
        // this.nombresPorCodigo.put("202012345", "Juan Pérez");
        // this.nombresPorCodigo.put("202087654", "Ana Gómez");
    }

    public List<Deuda> obtenerDeudasPorCodigo(String codigoEstudiante) {
        return this.deudasPorEstudiante.getOrDefault(codigoEstudiante, new ArrayList<>());
    }

    public void eliminarDeudasPorCodigo(String codigoEstudiante) {
        this.deudasPorEstudiante.remove(codigoEstudiante);
    }

    public void agregarDeuda(String codigoEstudiante, Deuda deuda) {
        this.deudasPorEstudiante.computeIfAbsent(codigoEstudiante, k -> new ArrayList<>()).add(deuda);
    }

    public Map<String, List<Deuda>> getTodasLasDeudas() {
        return this.deudasPorEstudiante;
    }
}