package co.edu.unicauca.distribuidos.core.fachadaServices.services;

import co.edu.unicauca.distribuidos.core.capaAccesoADatos.modelos.prestamoLaboratorio;
import co.edu.unicauca.distribuidos.core.capaAccesoADatos.repositorio.PrestamoLaboratorioRepositorio;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTOPeticion.PeticionPrestamoDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta.RespuestaPrestamoDTOLaboratorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaLaboratorioImpl implements ReservaLaboratorioInt {

    @Autowired
    private PrestamoLaboratorioRepositorio repositorio;

    @Override
    public List<RespuestaPrestamoDTOLaboratorio> consultarPrestamosPendientes(PeticionPrestamoDTO peticion) {
        List<prestamoLaboratorio> prestamos = repositorio.buscarPrestamosPendientes(peticion.getCodigoEstudiante());
        return prestamos.stream()
                .map(p -> new RespuestaPrestamoDTOLaboratorio(
                        p.getCodigoEstudiante(),
                        p.getFechaPrestamo(),
                        p.getFechaDevolucionEstimada(),
                        p.getFechaDevolucionReal(),
                        p.getEstadoPrestamo(),
                        p.getEquipoPrestado()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean eliminarPrestamosPendientes(PeticionPrestamoDTO peticion) {
        return repositorio.eliminarPrestamosPendientes(peticion.getCodigoEstudiante());
    }
}
