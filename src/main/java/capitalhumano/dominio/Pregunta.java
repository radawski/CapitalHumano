package capitalhumano.dominio;

import capitalhumano.enumeraciones.TipoPregunta;
import java.time.LocalDateTime;
import java.util.Collections;
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
        this.id = UUID.randomUUID();
        this.nombre = nombre;
        this.enunciado = enunciado;
        this.descripcion = descripcion != null ? descripcion : "";
        this.tipoPregunta = TipoPregunta.SINGLE_CHOICE;
        this.activa = true;
        this.factor = factor;
        this.opcion = opcion;
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
        if (valor == null) {
            throw new IllegalArgumentException("El valor de opción no puede ser nulo");
        }
        this.ponderaciones.put(valor, new PreguntaValorOpcion(this, valor, ponderacion));
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

    public String getDescripcion() {
        return descripcion;
    }

    public TipoPregunta getTipoPregunta() {
        return tipoPregunta;
    }

    public Map<ValorOpcion, PreguntaValorOpcion> getPonderaciones() {
        return Collections.unmodifiableMap(ponderaciones);
    }

    public Factor getFactor() {
        return factor;
    }

    public OpcionDeRespuesta getOpcion() {
        return opcion;
    }

    /** Devuelve el ValorOpcion de la opción de respuesta de esta pregunta que tiene ese id. */
    public ValorOpcion getValor(UUID valorOpcionId) {
        for (ValorOpcion valor : ponderaciones.keySet()) {
            if (valor.getId().equals(valorOpcionId)) {
                return valor;
            }
        }
        throw new IllegalArgumentException("El valor elegido no corresponde a la pregunta " + nombre);
    }

    public double getPonderacion(ValorOpcion valor) {
        PreguntaValorOpcion ponderacion = ponderaciones.get(valor);
        if (ponderacion == null) {
            throw new IllegalArgumentException("La pregunta " + nombre + " no tiene ponderación para ese valor");
        }
        return ponderacion.getPonderacion();
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
