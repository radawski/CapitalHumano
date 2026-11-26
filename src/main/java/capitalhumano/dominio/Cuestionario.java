package capitalhumano.dominio;

import capitalhumano.enumeraciones.NombreEstadoCuestionario;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.time.Duration;

public class Cuestionario {

    private static final String ALFABETO_CLAVE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LONGITUD_CLAVE = 8;
    private static final SecureRandom RANDOM = new SecureRandom();
    /** Observación 1 de CU026: se sortean 2 preguntas por factor. */
    private static final int PREGUNTAS_POR_FACTOR = 2;
    /**
     * Los puntajes son float (NUMERIC(5,2) en la base) y las ponderaciones double: sin tolerancia,
     * un 5.10 leído como float (5.0999999) no alcanzaría un mínimo de 5.10.
     */
    private static final double TOLERANCIA_PUNTAJE = 1e-4;

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
        this.id = UUID.randomUUID();
        this.candidato = Objects.requireNonNull(candidato, "El candidato no puede ser nulo");
        this.configuracion = Objects.requireNonNull(configuracion, "La configuración no puede ser nula");
        this.fechaCreacion = LocalDateTime.now();
        this.fechaHoraCambioEstado = fechaCreacion;
        this.cantidadAccesos = 0;
        this.estado = new EstadoActivo();
        this.clave = generarClave();
    }

    /**
     * Constructor de reconstrucción (no figura en el diagrama): lo usan los DAO.
     */
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

    /** Getter sin lógica (no figura en el diagrama): lo usan los DAO. */
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaHoraCambioEstado() {
        return fechaHoraCambioEstado;
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
        StringBuilder sb = new StringBuilder(LONGITUD_CLAVE);
        for (int i = 0; i < LONGITUD_CLAVE; i++) {
            sb.append(ALFABETO_CLAVE.charAt(RANDOM.nextInt(ALFABETO_CLAVE.length())));
        }
        this.clave = sb.toString();
        return this.clave;
    }

    public void registrarAcceso() {
        cantidadAccesos++;
        ultimoIngreso = LocalDateTime.now();
    }

    public boolean superoMaximoAccesos() {
        return cantidadAccesos > configuracion.getMaximoAccesos();
    }

    public boolean superoTiempoActivo(LocalDateTime ahora) {
        return Duration.between(fechaCreacion, ahora).compareTo(configuracion.getTiempoMaximoActivo()) > 0;
    }

    public boolean superoTiempoMaximo(LocalDateTime ahora) {
        return Duration.between(fechaHoraInicio, ahora).compareTo(configuracion.getTiempoMaximo()) > 0;
    }

    /**
     * Paso 3 de CU026 (Observación 1). Elige 2 preguntas al azar por factor, las mezcla
     * y arma los bloques según la configuración. Patrón Creador: el cuestionario crea sus
     * PreguntaSeleccionada y sus BloqueCuestionario.
     */
    public void asignarPreguntas(Map<Factor, List<Pregunta>> preguntasPorFactor) {
        if (preguntasPorFactor == null || preguntasPorFactor.isEmpty()) {
            throw new IllegalArgumentException("No hay factores con preguntas suficientes para armar el cuestionario");
        }
        if (!bloques.isEmpty()) {
            throw new IllegalStateException("El cuestionario ya tiene sus preguntas asignadas");
        }

        List<Pregunta> preguntasElegidas = new ArrayList<>();
        for (List<Pregunta> preguntasDelFactor : preguntasPorFactor.values()) {
            preguntasElegidas.addAll(elegirAlAzar(preguntasDelFactor, PREGUNTAS_POR_FACTOR));
        }
        mezclar(preguntasElegidas);

        List<PreguntaSeleccionada> seleccionadas = new ArrayList<>();
        for (int i = 0; i < preguntasElegidas.size(); i++) {
            seleccionadas.add(new PreguntaSeleccionada(preguntasElegidas.get(i), i + 1));
        }

        int cantidadPorBloque = configuracion.getCantidadPreguntasPorBloque();
        BloqueCuestionario bloque = null;
        for (int i = 0; i < seleccionadas.size(); i++) {
            if (i % cantidadPorBloque == 0) {
                bloque = new BloqueCuestionario(i / cantidadPorBloque + 1);
                bloques.add(bloque);
            }
            bloque.agregarPregunta(seleccionadas.get(i));
        }
    }

    private List<Pregunta> elegirAlAzar(List<Pregunta> preguntas, int cantidad) {
        if (preguntas.size() < cantidad) {
            throw new IllegalArgumentException(
                    "El factor necesita al menos " + cantidad + " preguntas activas para ser evaluado");
        }
        List<Pregunta> copia = new ArrayList<>(preguntas);
        Collections.shuffle(copia, RANDOM);
        return new ArrayList<>(copia.subList(0, cantidad));
    }

    private void mezclar(List<Pregunta> preguntas) {
        Collections.shuffle(preguntas, RANDOM);
    }

    /** Flujo 1.C y paso 8: el primer bloque, en orden, que todavía no está contestado (null si no queda). */
    public BloqueCuestionario getBloquePendiente() {
        for (BloqueCuestionario bloque : bloques) {
            if (!bloque.estaContestado()) {
                return bloque;
            }
        }
        return null;
    }

    /** Paso 7: guarda las respuestas del bloque pendiente. respuestas = id de pregunta → id de ValorOpcion. */
    public BloqueCuestionario responderBloque(int nroBloque, Map<UUID, UUID> respuestas) {
        if (getNombreEstado() != NombreEstadoCuestionario.EN_PROCESO) {
            throw new IllegalStateException("Sólo se pueden responder bloques de un cuestionario en proceso");
        }
        BloqueCuestionario pendiente = getBloquePendiente();
        if (pendiente == null || pendiente.getNroBloque() != nroBloque) {
            throw new IllegalStateException("El bloque " + nroBloque + " no es el bloque pendiente del cuestionario");
        }
        pendiente.responder(respuestas);
        return pendiente;
    }

    /**
     * Paso 8 (Observación 3). Por cada competencia evaluada crea su ResultadoCompetencia; el puntaje
     * de cada factor lo calcula el propio Factor con sus preguntas seleccionadas.
     */
    public void calcularResultados() {
        Map<Competencia, Map<Factor, List<PreguntaSeleccionada>>> porCompetencia = new LinkedHashMap<>();
        for (BloqueCuestionario bloque : bloques) {
            for (PreguntaSeleccionada seleccionada : bloque.getPreguntas()) {
                Factor factor = seleccionada.getPregunta().getFactor();
                porCompetencia
                        .computeIfAbsent(factor.getCompetencia(), c -> new LinkedHashMap<>())
                        .computeIfAbsent(factor, f -> new ArrayList<>())
                        .add(seleccionada);
            }
        }

        List<ResultadoCompetencia> nuevosResultados = new ArrayList<>();
        for (Map.Entry<Competencia, Map<Factor, List<PreguntaSeleccionada>>> competencia : porCompetencia.entrySet()) {
            ResultadoCompetencia resultado = new ResultadoCompetencia(competencia.getKey());
            for (Map.Entry<Factor, List<PreguntaSeleccionada>> factor : competencia.getValue().entrySet()) {
                float puntajeFactor = factor.getKey().calcularPuntaje(factor.getValue());
                resultado.agregarResultadoFactor(factor.getKey(), puntajeFactor);
            }
            resultado.calcularPuntaje();
            nuevosResultados.add(resultado);
        }
        this.resultados = nuevosResultados;
    }

    /** Getter sin lógica (no figura en el diagrama): lo usan los DAO y BloqueDTO. */
    public List<BloqueCuestionario> getBloques() {
        return Collections.unmodifiableList(bloques);
    }

    public List<ResultadoCompetencia> getResultados() {
        return resultados;
    }

    /** CU028: promedio de los puntajes de los {@code ResultadoCompetencia} (escala 0 a 10); 0 si no hay. */
    public float getPuntajeTotal() {
        if (resultados == null || resultados.isEmpty()) {
            return 0f;
        }
        double suma = 0;
        for (ResultadoCompetencia resultado : resultados) {
            suma += resultado.getPuntaje();
        }
        return (float) (suma / resultados.size());
    }

    /**
     * CU028: el cuestionario está COMPLETO y en <b>todas</b> las competencias del puesto el puntaje
     * alcanza la ponderación mínima. Una competencia del puesto sin resultado cuenta como no alcanzada.
     */
    public boolean alcanzaMinimos(Puesto puesto) {
        if (getNombreEstado() != NombreEstadoCuestionario.COMPLETO) {
            return false;
        }
        for (Competencia competencia : puesto.getCompetencias()) {
            ResultadoCompetencia resultado = buscarResultado(competencia);
            if (resultado == null
                    || resultado.getPuntaje() + TOLERANCIA_PUNTAJE < puesto.getPonderacionRequerida(competencia)) {
                return false;
            }
        }
        return true;
    }

    private ResultadoCompetencia buscarResultado(Competencia competencia) {
        if (resultados == null) {
            return null;
        }
        for (ResultadoCompetencia resultado : resultados) {
            if (competencia.equals(resultado.getCompetencia())) {
                return resultado;
            }
        }
        return null;
    }

    public void iniciar() {
        estado.iniciar(this);
        fechaHoraInicio = LocalDateTime.now();
    }

    public void completar() {
        estado.completar(this);
        fechaHoraFin = LocalDateTime.now();
    }

    public void marcarIncompleto() {
        estado.marcarIncompleto(this);
    }

    public void marcarSinContestar() {
        estado.marcarSinContestar(this);
    }

    void setEstado(EstadoCuestionario nuevoEstado) {
        this.estado = nuevoEstado;
        this.fechaHoraCambioEstado = LocalDateTime.now();
    }

    /**
     * Navegabilidad inversa de la composición con Evaluacion (no figura en el
     * diagrama).
     */
    void setEvaluacion(Evaluacion evaluacion) {
        this.evaluacion = evaluacion;
    }
}
