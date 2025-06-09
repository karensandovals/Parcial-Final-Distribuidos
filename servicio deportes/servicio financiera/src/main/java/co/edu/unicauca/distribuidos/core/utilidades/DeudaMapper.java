package co.edu.unicauca.distribuidos.core.utilidades;

import co.edu.unicauca.distribuidos.core.fachadaServices.DTORespueta.RespuestaDeudaDTO;
import co.edu.unicauca.distribuidos.core.modelos.Deuda;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DeudaMapper {

    public static RespuestaDeudaDTO fromDeudaToDTO(Deuda deuda) {
        return new RespuestaDeudaDTO(
                deuda.getCodigoEstudiante(),
                deuda.getMonto(),
                deuda.getMotivo(),
                localDateToDate(deuda.getFechaGeneracion()),
                localDateToDate(deuda.getFechaLimite()),
                deuda.getEstado()
        );
    }

    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate dateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
