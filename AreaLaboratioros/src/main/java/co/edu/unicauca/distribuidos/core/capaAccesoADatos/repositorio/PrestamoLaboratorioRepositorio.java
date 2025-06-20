package co.edu.unicauca.distribuidos.core.capaAccesoADatos.repositorio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import co.edu.unicauca.distribuidos.core.capaAccesoADatos.modelos.prestamoLaboratorio;

@Repository
public class PrestamoLaboratorioRepositorio {

    private List<prestamoLaboratorio> prestamos = new ArrayList<>();

    public PrestamoLaboratorioRepositorio() {
        prestamos.add(new prestamoLaboratorio(
                "202012345", "Andrés Sánchez", "Microscopio", "vencido",
                LocalDate.of(2024, 8, 30),
                LocalDate.of(2024, 5, 10),
                null));

        prestamos.add(new prestamoLaboratorio(
                "202012345", "Andrés Sánchez", "Computador", "activo",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 6, 7),
                null));

        prestamos.add(new prestamoLaboratorio(
                "202078901", "Carla Rodríguez", "Osciloscopio", "vencido",
                LocalDate.of(2024, 6, 10),
                LocalDate.of(2024, 6, 20),
                null));
    }

    public List<prestamoLaboratorio> buscarPrestamosPendientes(String codigoEstudiante) {
        return prestamos.stream()
                .filter(p -> p.getCodigoEstudiante().equals(codigoEstudiante)
                        && !p.getEstadoPrestamo().equalsIgnoreCase("devuelto"))
                .collect(Collectors.toList());
    }

    public boolean eliminarPrestamosPendientes(String codigoEstudiante) {
        return prestamos.removeIf(p -> p.getCodigoEstudiante().equals(codigoEstudiante)
                && !p.getEstadoPrestamo().equalsIgnoreCase("devuelto"));
    }

}
