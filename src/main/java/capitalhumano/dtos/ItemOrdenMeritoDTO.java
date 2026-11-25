package capitalhumano.dtos;

import capitalhumano.dominio.Candidato;
import capitalhumano.dominio.Cuestionario;
import capitalhumano.enumeraciones.NombreEstadoCuestionario;
import capitalhumano.enumeraciones.TipoDocumento;
import java.time.LocalDateTime;

public class ItemOrdenMeritoDTO {

    private Integer posicion;
    private TipoDocumento tipoDocumento;
    private String nroDocumento;
    private String apellido;
    private String nombre;
    private Float puntajeObtenido;
    private NombreEstadoCuestionario estadoCuestionario;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime ultimoIngreso;
    private int cantidadAccesos;

    public ItemOrdenMeritoDTO(Candidato candidato, Cuestionario cuestionario, Float puntaje) {
        throw new UnsupportedOperationException("TODO #6");
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public Integer getPosicion() {
        return posicion;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public String getApellido() {
        return apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public Float getPuntajeObtenido() {
        return puntajeObtenido;
    }

    public NombreEstadoCuestionario getEstadoCuestionario() {
        return estadoCuestionario;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public LocalDateTime getUltimoIngreso() {
        return ultimoIngreso;
    }

    public int getCantidadAccesos() {
        return cantidadAccesos;
    }
}
