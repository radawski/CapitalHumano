package capitalhumano.dominio;

import capitalhumano.enumeraciones.TipoPregunta;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Pregunta extends SujetoAuditable {

    private UUID id;
    private String nombre;
    private String enunciado;
    private String descripcion;
    private TipoPregunta tipoPregunta;
    private boolean activa;
    /** Extremo de la composición Factor "1" *-- "0..*" Pregunta. */
    private Factor factor;
    /** Asociación Pregunta "0..*" -- "1" OpcionDeRespuesta. */
    private OpcionDeRespuesta opcion;
    /** Asociación Pregunta -- ValorOpcion con su clase de asociación PreguntaValorOpcion. */
    private Map<ValorOpcion, PreguntaValorOpcion> ponderaciones = new LinkedHashMap<>();

    public Pregunta(String nombre, String enunciado, String descripcion, Factor factor, OpcionDeRespuesta opcion) {
        throw new UnsupportedOperationException("TODO #8");
    }

    /** Constructor de reconstrucción (no figura en el diagrama): lo usan los DAO. */
    public Pregunta(UUID id, String nombre, String enunciado, String descripcion, TipoPregunta tipoPregunta,
                    boolean activa, Factor factor, OpcionDeRespuesta opcion, Map<ValorOpcion, Double> ponderaciones) {
        this.id = id;
        this.nombre = nombre;
        this.enunciado = enunciado;
        this.descripcion = descripcion;
        this.tipoPregunta = tipoPregunta;
        this.activa = activa;
        this.factor = factor;
        this.opcion = opcion;
        ponderaciones.forEach((valor, ponderacion) ->
                this.ponderaciones.put(valor, new PreguntaValorOpcion(this, valor, ponderacion)));
    }

    public void agregarPonderacion(ValorOpcion valor, double ponderacion) {
        throw new UnsupportedOperationException("TODO #8");
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public Factor getFactor() {
        return factor;
    }

    public OpcionDeRespuesta getOpcion() {
        return opcion;
    }

    public ValorOpcion getValor(UUID valorOpcionId) {
        throw new UnsupportedOperationException("TODO #16");
    }

    public double getPonderacion(ValorOpcion valor) {
        throw new UnsupportedOperationException("TODO #17");
    }

    @Override
    public void darDeBaja(String usuario) {
        this.activa = false;
        notificarObservadores(construirRegistro(usuario, "BAJA_LOGICA"));
    }

    @Override
    protected RegistroAuditoria construirRegistro(String usuario, String operacion) {
        return new RegistroAuditoria(usuario, LocalDateTime.now(), operacion, "Pregunta",
                id != null ? id.toString() : "");
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Pregunta otra && Objects.equals(id, otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
