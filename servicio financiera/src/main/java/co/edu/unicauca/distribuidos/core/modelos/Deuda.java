package co.edu.unicauca.distribuidos.core.modelos;

import java.time.LocalDate;

public class Deuda {
    private String codigoEstudiante;
    private double monto;
    private String motivo;
    private LocalDate fechaGeneracion;
    private LocalDate fechaLimite;
    private String estado; // pendiente, pagada, en mora
    
    public Deuda(String codigoEstudiante, double monto, String motivo, LocalDate fechaGeneracion, LocalDate fechaLimite,
            String estado) {
        this.codigoEstudiante = codigoEstudiante;
        this.monto = monto;
        this.motivo = motivo;
        this.fechaGeneracion = fechaGeneracion;
        this.fechaLimite = fechaLimite;
        this.estado = estado;
    }
    public String getCodigoEstudiante() {
        return codigoEstudiante;
    }
    public void setCodigoEstudiante(String codigoEstudiante) {
        this.codigoEstudiante = codigoEstudiante;
    }
    public double getMonto() {
        return monto;
    }
    public void setMonto(double monto) {
        this.monto = monto;
    }
    public String getMotivo() {
        return motivo;
    }
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }
    public void setFechaGeneracion(LocalDate fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }
    public LocalDate getFechaLimite() {
        return fechaLimite;
    }
    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

}
