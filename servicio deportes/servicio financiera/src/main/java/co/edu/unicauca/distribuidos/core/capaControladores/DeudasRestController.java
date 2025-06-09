package co.edu.unicauca.distribuidos.core.capaControladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.distribuidos.core.fachadaServices.DTOPeticion.PeticionDeudaDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta.RespuestaDeudaDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.services.GestionDeudasInt;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DeudasRestController {

    @Autowired
    private GestionDeudasInt objFachada;

    @PostMapping("/deudas")
    public List<RespuestaDeudaDTO> consultarDeudas(@RequestBody PeticionDeudaDTO objPeticion) {
        System.out.println("Consultando deudas del estudiante: " + objPeticion.getCodigoEstudiante());
        return this.objFachada.consultarDeudas(objPeticion);
    }

    @DeleteMapping("/deudas/{codigo}")
    public boolean eliminarDeudas(@PathVariable String codigo) {
        System.out.println("Eliminando deudas del estudiante: " + codigo);
        return this.objFachada.eliminarDeudas(codigo);
    }
}