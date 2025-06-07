package co.edu.unicauca.distribuidos.core.capaControladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import co.edu.unicauca.distribuidos.core.fachadaServices.DTOPeticion.PeticionImplementoDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespuesta.RespuestaReporteDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.services.DeportesServiceInt;

@RestController
@RequestMapping("/api")
public class DeportesRestController {

    @Autowired
    private DeportesServiceInt objFachada;

    @PostMapping("/deportes")
    public RespuestaReporteDTO consultarImplementosPendientes(@RequestBody PeticionImplementoDTO objPeticion) {
        System.out.println("Consultando implementos deportivos pendientes");
        try {
            Thread.sleep(2000); 
        } catch (InterruptedException e) {
			e.printStackTrace();
        }
        RespuestaReporteDTO objResultado = this.objFachada.crearImplemento(objPeticion);
        return objResultado;
    }

    @DeleteMapping("/deportes/{codigo}")
    public boolean eliminarImplementosPendientes(@PathVariable String codigo) {
        System.out.println("Eliminando implementos deportivos pendientes");
        boolean bandera = this.objFachada.eliminarImplemento(codigo);
        return bandera;
    }
}
