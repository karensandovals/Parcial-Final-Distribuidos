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
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespuesta.RespuestaPazYSalvoDTODeportes;

@Service
public class DeportesServiceImpl implements DeportesServiceInt {

    @Autowired
    private ImplementoRepository implementoRepository;

    @Override
    public List<RespuestaPazYSalvoDTODeportes> crearImplemento(PeticionImplementoDTO objPeticion) {
        String codigo = objPeticion.getCodigoEstudiante();
        List<ImplementoDeportivo> pendientes = implementoRepository.buscarPorCodigo(codigo);
        List<RespuestaPazYSalvoDTODeportes> respuesta = new ArrayList<>();

        for (ImplementoDeportivo imp : pendientes) {
            respuesta.add(new RespuestaPazYSalvoDTODeportes(
                    imp.getCodigoEstudiante(),
                    imp.getNombresEstudiante(),
                    imp.getFechaPrestamo(),
                    imp.getFechaDevolucionEstimada(),
                    imp.getFechaDevolucionReal(),
                    imp.getImplementoDeportivoPrestado()));
        }

        return respuesta;
    }

    @Override
    public boolean eliminarImplemento(String codigoEstudiante) {
        return implementoRepository.eliminarPorCodigo(codigoEstudiante);
    }
}
