package co.edu.unicauca.distribuidos.core.capaAccesoADatos.repositorio;

import co.edu.unicauca.distribuidos.core.capaAccesoADatos.modelos.prestamoLaboratorio;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PrestamoLaboratorioRepositorio {

    private List<prestamoLaboratorio> prestamos = new ArrayList<>();

    public PrestamoLaboratorioRepositorio() {
        prestamos.add(new prestamoLaboratorio("1234", "Microscopio", "vencido",
                LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 10), null));

        prestamos.add(new prestamoLaboratorio("1234", "Computador", "activo",
                LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 7), null));
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
