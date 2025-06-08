
package co.edu.unicauca.distribuidos.core.capaControladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.distribuidos.core.fachadaServices.DTOPeticion.PeticionPazYSalvoDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta.RespuestaPazYSalvoDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.services.GenerarPazYSalvoInt;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class PazYSalvoRestController {

	@Autowired
	private GenerarPazYSalvoInt objFachada;

	@PostMapping("/orquestadorSincrono")
	public RespuestaPazYSalvoDTO orquestarServiciosSincronicamente(@RequestBody PeticionPazYSalvoDTO objPeticion) {
		RespuestaPazYSalvoDTO objResultado = this.objFachada.consultarPazYSalvo(objPeticion);
		return objResultado;
	}

	@PostMapping("/orquestadorAsincrono")
	public Mono<RespuestaPazYSalvoDTO> orquestarServiciosAsincronicamente(
			@RequestBody PeticionPazYSalvoDTO objPeticion) {
		Mono<RespuestaPazYSalvoDTO> objResultado = this.objFachada.consultarPazYSalvoAsincrono(objPeticion);
		return objResultado;
	}

	@PostMapping("/eliminarDeudasLaboratorio")
	public ResponseEntity<String> eliminarLaboratorio(@RequestBody PeticionPazYSalvoDTO peticion) {
		objFachada.eliminarDeudasLaboratorio(peticion);
		return ResponseEntity.ok("Deudas de laboratorio eliminadas exitosamente.");
	}

	@PostMapping("/eliminarDeudasDeportes")
	public ResponseEntity<String> eliminarDeportes(@RequestBody PeticionPazYSalvoDTO peticion) {
		objFachada.eliminarDeudasDeportes(peticion);
		return ResponseEntity.ok("Deudas de deportes eliminadas exitosamente.");
	}

	@PostMapping("/eliminarDeudasFinanciera")
	public ResponseEntity<String> eliminarFinanciera(@RequestBody PeticionPazYSalvoDTO peticion) {
		objFachada.eliminarDeudasFinanciera(peticion);
		return ResponseEntity.ok("Deudas de financiera eliminadas exitosamente.");
	}

}
