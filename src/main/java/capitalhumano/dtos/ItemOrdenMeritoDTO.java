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

    /**
     * Fila del listado de CU028. El puntaje llega null cuando el cuestionario no está completo
     * (Observación 1); la posición la asigna {@code OrdenMeritoDTO.ordenar()} solo a los aprobados.
     */
    public ItemOrdenMeritoDTO(Candidato candidato, Cuestionario cuestionario, Float puntaje) {
        this.tipoDocumento = candidato.getTipoDocumento();
        this.nroDocumento = candidato.getNroDocumento();
        this.apellido = candidato.getApellido();
        this.nombre = candidato.getNombre();
        this.puntajeObtenido = puntaje;
        this.estadoCuestionario = cuestionario.getNombreEstado();
        this.fechaInicio = cuestionario.getFechaHoraInicio();
        this.fechaFin = cuestionario.getFechaHoraFin();
        this.ultimoIngreso = cuestionario.getUltimoIngreso();
        this.cantidadAccesos = cuestionario.getCantidadAccesos();
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
