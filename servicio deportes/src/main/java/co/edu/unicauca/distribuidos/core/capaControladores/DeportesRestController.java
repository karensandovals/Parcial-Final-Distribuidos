package co.edu.unicauca.distribuidos.core.capaControladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import co.edu.unicauca.distribuidos.core.fachadaServices.DTOPeticion.PeticionImplementoDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespuesta.RespuestaPazYSalvoDTODeportes;
import co.edu.unicauca.distribuidos.core.fachadaServices.services.DeportesServiceInt;


@RestController
@RequestMapping("/api")
public class DeportesRestController {

    @Autowired
    private DeportesServiceInt objFachada;

    @PostMapping("/deportes")
    public List<RespuestaPazYSalvoDTODeportes> consultarImplementosPendientes(@RequestBody PeticionImplementoDTO objPeticion) {
        return objFachada.crearImplemento(objPeticion);
    }


    @DeleteMapping("/deportes/{codigo}")
    public boolean eliminarImplementosPendientes(@PathVariable String codigo) {
        System.out.println("Eliminando implementos deportivos pendientes");
        boolean bandera = this.objFachada.eliminarImplemento(codigo);
        return bandera;
    }
}
