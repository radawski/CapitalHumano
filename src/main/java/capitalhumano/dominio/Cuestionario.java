package capitalhumano.dominio;

import capitalhumano.enumeraciones.NombreEstadoCuestionario;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Cuestionario {

    private UUID id;
    private String clave;
    private EstadoCuestionario estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private LocalDateTime ultimoIngreso;
    private int cantidadAccesos;
    private LocalDateTime fechaHoraCambioEstado;
    /** Asociación Candidato "1" -- "0..*" Cuestionario. */
    private Candidato candidato;
    /** Extremo de la composición Evaluacion "1" *-- "1..*" Cuestionario. */
    private Evaluacion evaluacion;
    /** Asociación Cuestionario "0..*" -- "1" ConfiguracionCuestionario. */
    private ConfiguracionCuestionario configuracion;
    /** Composición Cuestionario "1" *-- "0..*" BloqueCuestionario. */
    private List<BloqueCuestionario> bloques = new ArrayList<>();
    /** Composición Cuestionario "1" *-- "0..*" ResultadoCompetencia. */
    private List<ResultadoCompetencia> resultados = new ArrayList<>();

    public Cuestionario(Candidato candidato, ConfiguracionCuestionario configuracion) {
        throw new UnsupportedOperationException("TODO #12");
    }

    /** Constructor de reconstrucción (no figura en el diagrama): lo usan los DAO. */
    public Cuestionario(UUID id, String clave, EstadoCuestionario estado, LocalDateTime fechaCreacion,
                        LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, LocalDateTime ultimoIngreso,
                        int cantidadAccesos, LocalDateTime fechaHoraCambioEstado, Candidato candidato,
                        ConfiguracionCuestionario configuracion, List<BloqueCuestionario> bloques,
                        List<ResultadoCompetencia> resultados) {
        this.id = id;
        this.clave = clave;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.ultimoIngreso = ultimoIngreso;
        this.cantidadAccesos = cantidadAccesos;
        this.fechaHoraCambioEstado = fechaHoraCambioEstado;
        this.candidato = candidato;
        this.configuracion = configuracion;
        this.bloques = new ArrayList<>(bloques);
        this.resultados = new ArrayList<>(resultados);
    }

    public UUID getId() {
        return id;
    }

    public String getClave() {
        return clave;
    }

    public Candidato getCandidato() {
        return candidato;
    }

    public Evaluacion getEvaluacion() {
        return evaluacion;
    }

    public ConfiguracionCuestionario getConfiguracion() {
        return configuracion;
    }

    public NombreEstadoCuestionario getNombreEstado() {
        return estado.getNombre();
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public LocalDateTime getUltimoIngreso() {
        return ultimoIngreso;
    }

    public int getCantidadAccesos() {
        return cantidadAccesos;
    }

    public String generarClave() {
        throw new UnsupportedOperationException("TODO #12");
    }

    public void registrarAcceso() {
        throw new UnsupportedOperationException("TODO #14");
    }

    public boolean superoMaximoAccesos() {
        throw new UnsupportedOperationException("TODO #14");
    }

    public boolean superoTiempoActivo(LocalDateTime ahora) {
        throw new UnsupportedOperationException("TODO #14");
    }

    public boolean superoTiempoMaximo(LocalDateTime ahora) {
        throw new UnsupportedOperationException("TODO #14");
    }

    public void asignarPreguntas(Map<Factor, List<Pregunta>> preguntasPorFactor) {
        throw new UnsupportedOperationException("TODO #15");
    }

    private List<Pregunta> elegirAlAzar(List<Pregunta> preguntas, int cantidad) {
        throw new UnsupportedOperationException("TODO #15");
    }

    private void mezclar(List<Pregunta> preguntas) {
        throw new UnsupportedOperationException("TODO #15");
    }

    public BloqueCuestionario getBloquePendiente() {
        throw new UnsupportedOperationException("TODO #16");
    }

    public BloqueCuestionario responderBloque(int nroBloque, Map<UUID, UUID> respuestas) {
        throw new UnsupportedOperationException("TODO #16");
    }

    public void calcularResultados() {
        throw new UnsupportedOperationException("TODO #17");
    }

    public List<ResultadoCompetencia> getResultados() {
        return resultados;
    }

    public float getPuntajeTotal() {
        throw new UnsupportedOperationException("TODO #6");
    }

    public boolean alcanzaMinimos(Puesto puesto) {
        throw new UnsupportedOperationException("TODO #6");
    }

    public void iniciar() {
        throw new UnsupportedOperationException("TODO #14");
    }

    public void completar() {
        throw new UnsupportedOperationException("TODO #14");
    }

    public void marcarIncompleto() {
        throw new UnsupportedOperationException("TODO #14");
    }

    public void marcarSinContestar() {
        throw new UnsupportedOperationException("TODO #14");
    }

    void setEstado(EstadoCuestionario nuevoEstado) {
        this.estado = nuevoEstado;
    }

    /** Navegabilidad inversa de la composición con Evaluacion (no figura en el diagrama). */
    void setEvaluacion(Evaluacion evaluacion) {
        this.evaluacion = evaluacion;
    }
}
