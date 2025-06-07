package co.edu.unicauca.distribuidos.core.fachadaServices.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.distribuidos.core.fachadaServices.DTOPeticion.PeticionDeudaDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta.RespuestaDeudaDTO;
import co.edu.unicauca.distribuidos.core.modelos.Deuda;
import co.edu.unicauca.distribuidos.core.repositorios.DeudaRepositorio;
import co.edu.unicauca.distribuidos.core.utilidades.DeudaMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class GestionDeudasImpl implements GestionDeudasInt {

    @Autowired
    private DeudaRepositorio repo;

    @Override
    public List<RespuestaDeudaDTO> consultarDeudas(PeticionDeudaDTO objPeticion) {
        List<Deuda> lista = repo.obtenerDeudasPorCodigo(objPeticion.getCodigoEstudiante());
        List<RespuestaDeudaDTO> respuesta = new ArrayList<>();

        for (Deuda deuda : lista) {
            respuesta.add(DeudaMapper.fromDeudaToDTO(deuda));
        }

        return respuesta;
    }

    @Override
    public Boolean eliminarDeudas(String codigoEstudiante) {
        repo.eliminarDeudasPorCodigo(codigoEstudiante);
        return true;
    }
}
