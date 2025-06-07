package co.edu.unicauca.distribuidos.core.repositorios;

import java.time.LocalDate;
import java.util.*;

import org.springframework.stereotype.Repository;

import co.edu.unicauca.distribuidos.core.modelos.Deuda;

@Repository
// Repositorio para manejar las deudas
public class DeudaRepositorio {
   private Map<String, List<Deuda>> deudasPorEstudiante;

    public DeudaRepositorio() {
        this.deudasPorEstudiante = new HashMap<>();

        // 🔽 Deudas precargadas (simuladas en RAM)

        List<Deuda> deudasJuan = new ArrayList<>();
        deudasJuan.add(new Deuda("202012345", 150000, "Mora en pago de matrícula", 
                LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 31), "pendiente"));
        deudasJuan.add(new Deuda("202012345", 25000, "Pérdida de material", 
                LocalDate.of(2024, 9, 5), LocalDate.of(2024, 9, 30), "pendiente"));

        List<Deuda> deudasAna = new ArrayList<>();
        deudasAna.add(new Deuda("202087654", 50000, "Mora en pago de matrícula", 
                LocalDate.of(2025, 1, 10), LocalDate.of(2025, 2, 10), "en mora"));

        this.deudasPorEstudiante.put("202012345", deudasJuan);
        this.deudasPorEstudiante.put("202087654", deudasAna);
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
