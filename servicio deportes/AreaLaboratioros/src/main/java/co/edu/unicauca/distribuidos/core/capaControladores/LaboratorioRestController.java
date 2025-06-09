
package co.edu.unicauca.distribuidos.core.capaControladores;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.distribuidos.core.fachadaServices.DTOPeticion.PeticionPrestamoDTO;
import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta.RespuestaPrestamoDTOLaboratorio;
import co.edu.unicauca.distribuidos.core.fachadaServices.services.ReservaLaboratorioInt;

@RestController
@RequestMapping("/api/laboratorio")
public class LaboratorioRestController {
//permite consultar los préstamos de un estudiante y eliminar un préstamo.
	@Autowired
	private ReservaLaboratorioInt servicioLaboratorio;

		@PostMapping("/consultar")
		public List<RespuestaPrestamoDTOLaboratorio> obtenerDeudas(@RequestBody PeticionPrestamoDTO peticion) {
			System.out.println("Petición recibida para código: " + peticion.getCodigoEstudiante());
    		return servicioLaboratorio.consultarPrestamosPendientes(peticion);
		}

		@DeleteMapping("/eliminar")
		public boolean eliminarDeudas(@RequestBody PeticionPrestamoDTO peticion) {
			return servicioLaboratorio.eliminarPrestamosPendientes(peticion);
		}
}