package capitalhumano.dominio;

import java.time.LocalDateTime;

public class RegistroAuditoria {

    private String usuario;
    private LocalDateTime fechaHora;
    private String operacion;
    private String entidad;
    private String idEntidad;

    /** Constructor de reconstrucción (no figura en el diagrama): lo usan construirRegistro y los DAO. */
    public RegistroAuditoria(String usuario, LocalDateTime fechaHora, String operacion, String entidad, String idEntidad) {
        this.usuario = usuario;
        this.fechaHora = fechaHora;
        this.operacion = operacion;
        this.entidad = entidad;
        this.idEntidad = idEntidad;
    }

    public String getUsuario() {
        return usuario;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getOperacion() {
        return operacion;
    }

    public String getEntidad() {
        return entidad;
    }
}
