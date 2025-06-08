package co.edu.unicauca.distribuidos.core.fachadaServices.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.distribuidos.core.capaAccesoADatos.modelos.prestamoLaboratorio;
import co.edu.unicauca.distribuidos.core.capaAccesoADatos.repositorio.PrestamoLaboratorioRepositorio;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTOPeticion.PeticionPrestamoDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta.RespuestaPrestamoDTOLaboratorio;

@Service
public class ReservaLaboratorioImpl implements ReservaLaboratorioInt {

    @Autowired
    private PrestamoLaboratorioRepositorio repositorio;

    @Override
    public List<RespuestaPrestamoDTOLaboratorio> consultarPrestamosPendientes(PeticionPrestamoDTO peticion) {
        List<prestamoLaboratorio> prestamos = repositorio.buscarPrestamosPendientes(peticion.getCodigoEstudiante());
        return prestamos.stream()
                .map(p -> new RespuestaPrestamoDTOLaboratorio(
                        p.getNombreEstudiante(),
                        p.getEquipo(),
                        p.getEstado(),
                        p.getFechaPrestamo(),
                        p.getFechaDevolucionEstimada(),
                        p.getFechaDevolucionReal()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean eliminarPrestamosPendientes(PeticionPrestamoDTO peticion) {
        return repositorio.eliminarPrestamosPendientes(peticion.getCodigoEstudiante());
    }
}
