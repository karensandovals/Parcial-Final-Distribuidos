package co.edu.unicauca.distribuidos.core.fachadaServices.services;

import org.springframework.stereotype.Service;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTOPeticion.PeticionImplementoDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespuesta.RespuestaReporteDTO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class DeportesServiceImpl implements DeportesServiceInt {

    // Lista simulada en memoria de implementos deportivos prestados y no retornados
    private List<ImplementoDeportivo> implementosPendientes;

    public DeportesServiceImpl() {
        implementosPendientes = new ArrayList<>();
        implementosPendientes.add(new ImplementoDeportivo("1001", "Balón"));
        implementosPendientes.add(new ImplementoDeportivo("1002", "Raqueta"));
        implementosPendientes.add(new ImplementoDeportivo("1001", "Uniforme"));
    }

    @Override
    public RespuestaReporteDTO crearImplemento(PeticionImplementoDTO objPeticion) {
        String codigo = objPeticion.getCodigoEstudiante();
        List<String> pendientes = new ArrayList<>();

        for (ImplementoDeportivo imp : implementosPendientes) {
            if (imp.getCodigoEstudiante().equals(codigo)) {
                pendientes.add(imp.getNombreImplemento());
            }
        }

        boolean estaPazYSalvo = pendientes.isEmpty();
        return new RespuestaReporteDTO(codigo, estaPazYSalvo, pendientes);
    }

    @Override
    public boolean eliminarImplemento(String codigoEstudiante) {
        boolean eliminado = false;
        Iterator<ImplementoDeportivo> iterator = implementosPendientes.iterator();
        while (iterator.hasNext()) {
            ImplementoDeportivo imp = iterator.next();
            if (imp.getCodigoEstudiante().equals(codigoEstudiante)) {
                iterator.remove();
                eliminado = true;
            }
        }
        return eliminado;
    }

    private static class ImplementoDeportivo {
        private String codigoEstudiante;
        private String nombreImplemento;

        public ImplementoDeportivo(String codigoEstudiante, String nombreImplemento) {
            this.codigoEstudiante = codigoEstudiante;
            this.nombreImplemento = nombreImplemento;
        }

        public String getCodigoEstudiante() {
            return codigoEstudiante;
        }

        public String getNombreImplemento() {
            return nombreImplemento;
        }
    }
}
