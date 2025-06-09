package co.edu.unicauca.distribuidos.core.fachadaServices.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.distribuidos.core.capaAccesoADatos.modelos.ImplementoDeportivo;
import co.edu.unicauca.distribuidos.core.capaAccesoADatos.repositorios.ImplementoRepository;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTOPeticion.PeticionImplementoDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespuesta.RespuestaReporteDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespuesta.RespuestaPazYSalvoDTODeportes;

@Service
public class DeportesServiceImpl implements DeportesServiceInt {

    @Autowired
    private ImplementoRepository implementoRepository;

    private Date parseFecha(String fechaTexto) {
    if (fechaTexto == null || fechaTexto.trim().isEmpty()) {
        return null;
    }
    try {
        return new SimpleDateFormat("yyyy-MM-dd").parse(fechaTexto);
    } catch (ParseException e) {
        return null;
    }
}


    @Override
    public RespuestaReporteDTO crearImplemento(PeticionImplementoDTO objPeticion) {
        String codigo = objPeticion.getCodigoEstudiante();
        List<ImplementoDeportivo> pendientes = implementoRepository.buscarPorCodigo(codigo);
        List<RespuestaPazYSalvoDTODeportes> detalle = new ArrayList<>();

        for (ImplementoDeportivo imp : pendientes) {
            detalle.add(new RespuestaPazYSalvoDTODeportes(
                imp.getCodigoEstudiante(),
                parseFecha(imp.getFechaPrestamo()),
                parseFecha(imp.getFechaDevolucionEstimada()),
                parseFecha(imp.getFechaDevolucionReal()),
                imp.getNombreImplemento()
            ));
        }

        boolean estaPazYSalvo = detalle.isEmpty();
        String nombreEstudiante = implementoRepository.obtenerNombresPorCodigo()
                .getOrDefault(codigo, "Desconocido");

        return new RespuestaReporteDTO(codigo, nombreEstudiante, estaPazYSalvo, detalle);
    }

    @Override
    public boolean eliminarImplemento(String codigoEstudiante) {
        return implementoRepository.eliminarPorCodigo(codigoEstudiante);
    }
}
