package co.edu.unicauca.distribuidos.core.capaControladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.distribuidos.core.fachadaServices.DTOPeticion.PeticionPazYSalvoDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta.RespuestaPazYSalvoDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.services.GenerarPazYSalvoInt;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class PazYSalvoRestController {

	@Autowired
	private GenerarPazYSalvoInt objFachada;

	@PostMapping("/orquestadorSincrono")
	public RespuestaPazYSalvoDTO orquestarServiciosSincronicamente(@RequestBody PeticionPazYSalvoDTO objPeticion) {
		RespuestaPazYSalvoDTO objResultado = this.objFachada.verificarPazYSalvo(objPeticion);
		return objResultado;
	}

	@PostMapping("/orquestadorAsincrono")
	public Mono<RespuestaPazYSalvoDTO> orquestarServiciosAsincronicamente(@RequestBody PeticionPazYSalvoDTO objPeticion) {
		Mono<RespuestaPazYSalvoDTO> objResultado = this.objFachada.verificarPazYSalvoAsincrono(objPeticion);
		return objResultado;
	}
	
}
