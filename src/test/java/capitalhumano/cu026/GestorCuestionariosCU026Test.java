package capitalhumano.cu026;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import capitalhumano.controladores.GestorCuestionarios;
import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Cuestionario;
import capitalhumano.dominio.Evaluacion;
import capitalhumano.dominio.Factor;
import capitalhumano.dominio.PonderacionPuesto;
import capitalhumano.dominio.Pregunta;
import capitalhumano.dominio.Puesto;
import capitalhumano.dtos.BloqueDTO;
import capitalhumano.dtos.CredencialesLoginDTO;
import capitalhumano.dtos.PreguntaBloqueDTO;
import capitalhumano.dtos.RespuestasBloqueDTO;
import capitalhumano.enumeraciones.NombreEstadoCuestionario;
import capitalhumano.enumeraciones.TipoAcceso;
import capitalhumano.enumeraciones.TipoDocumento;
import capitalhumano.persistencia.CompetenciaDAO;
import capitalhumano.persistencia.EvaluacionDAO;
import capitalhumano.persistencia.PreguntaDAO;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Recorrido de CU026 en el gestor (#14 validarClaveCandidato, #15, #16, #17) con DAO en memoria. */
class GestorCuestionariosCU026Test {

    private final Competencia evaluable = DatosCU026.competencia("C1");
    private final Competencia noEvaluable = DatosCU026.competencia("C3");
    private final Factor f1 = DatosCU026.factor("F1", evaluable);
    private final Factor f2 = DatosCU026.factor("F2", evaluable);
    private final Factor fPocas = DatosCU026.factor("F9", noEvaluable);

    private Cuestionario cuestionario;
    private final List<String> llamadas = new ArrayList<>();
    private GestorCuestionarios gestor;

    @BeforeEach
    void armar() {
        cuestionario = DatosCU026.activo(3);
        Map<Competencia, PonderacionPuesto> ponderaciones = new LinkedHashMap<>();
        ponderaciones.put(evaluable, new PonderacionPuesto(5));
        ponderaciones.put(noEvaluable, new PonderacionPuesto(5));
        Puesto puesto = new Puesto("P001", "PUESTO", "", "EMPRESA", true, ponderaciones);
        new Evaluacion(UUID.randomUUID(), LocalDate.now(), true, puesto, List.of(cuestionario));

        Map<Factor, List<Pregunta>> preguntas = Map.of(
                f1, DatosCU026.preguntas(f1, 4),
                f2, DatosCU026.preguntas(f2, 2),
                fPocas, DatosCU026.preguntas(fPocas, 1));

        EvaluacionDAO evaluacionDAO = fake(EvaluacionDAO.class, Map.of(
                "obtenerCuestionario", args -> cuestionario.getId().equals(args[0]) ? cuestionario : null,
                "obtenerCuestionarioPorClave", args -> cuestionario.getClave().equals(args[0]) ? cuestionario : null,
                "guardarCuestionario", args -> registrar("guardarCuestionario"),
                "guardarBloque", args -> registrar("guardarBloque"),
                "guardarResultados", args -> registrar("guardarResultados"),
                "actualizarCuestionario", args -> registrar("actualizarCuestionario")));
        CompetenciaDAO competenciaDAO = fake(CompetenciaDAO.class, Map.of(
                "obtenerFactores", args -> args[0].equals(evaluable) ? List.of(f1, f2) : List.of(fPocas)));
        PreguntaDAO preguntaDAO = fake(PreguntaDAO.class, Map.of(
                "obtenerPreguntasActivasPorFactor", args -> preguntas.get((Factor) args[0])));

        gestor = new GestorCuestionarios(evaluacionDAO, competenciaDAO, preguntaDAO);
    }

    @Test
    void validarClaveCandidato_conDocumentoCorrecto_devuelveElCuestionario() {
        CredencialesLoginDTO ok = new CredencialesLoginDTO(TipoAcceso.CANDIDATO, null, null,
                TipoDocumento.DNI, "30111222", "TEST0001");
        CredencialesLoginDTO otroDni = new CredencialesLoginDTO(TipoAcceso.CANDIDATO, null, null,
                TipoDocumento.DNI, "99999999", "TEST0001");
        CredencialesLoginDTO otraClave = new CredencialesLoginDTO(TipoAcceso.CANDIDATO, null, null,
                TipoDocumento.DNI, "30111222", "NOEXISTE");

        assertSame(cuestionario, gestor.validarClaveCandidato(ok));
        assertNull(gestor.validarClaveCandidato(otroDni));
        assertNull(gestor.validarClaveCandidato(otraClave));
    }

    @Test
    void recorridoCompleto_comenzarResponderYFinalizar() {
        BloqueDTO bloque = gestor.comenzarCuestionario(cuestionario.getId());

        // Paso 3: sólo F1 y F2 (F9 tiene 1 pregunta) → 4 preguntas en bloques de 3 y 1
        assertEquals(NombreEstadoCuestionario.EN_PROCESO, cuestionario.getNombreEstado());
        assertNotNull(cuestionario.getFechaHoraInicio());
        assertEquals(2, cuestionario.getBloques().size());
        assertTrue(llamadas.contains("guardarCuestionario"));
        assertEquals(1, bloque.getNroBloque());
        assertFalse(bloque.esUltimo());

        // 1.C: el bloque pendiente es el mismo
        assertEquals(1, gestor.obtenerBloquePendiente(cuestionario.getId()).getNroBloque());

        // Paso 7 y flujo 8.A
        BloqueDTO siguiente = gestor.responderBloque(respuestas(bloque));
        assertEquals(2, siguiente.getNroBloque());
        assertTrue(siguiente.esUltimo());
        assertEquals(2, gestor.obtenerBloquePendiente(cuestionario.getId()).getNroBloque());

        // Paso 8
        assertNull(gestor.responderBloque(respuestas(siguiente)));
        assertEquals(NombreEstadoCuestionario.COMPLETO, cuestionario.getNombreEstado());
        assertNotNull(cuestionario.getFechaHoraFin());
        assertEquals(1, cuestionario.getResultados().size());
        assertEquals(7f, cuestionario.getResultados().get(0).getPuntaje(), 0.0001); // todo BUENO = 7
        assertTrue(llamadas.indexOf("guardarResultados") < llamadas.lastIndexOf("actualizarCuestionario"));
        assertNull(gestor.obtenerBloquePendiente(cuestionario.getId()));
    }

    @Test
    void aceptarDosVeces_noVuelveASortear() {
        gestor.comenzarCuestionario(cuestionario.getId());
        var bloques = cuestionario.getBloques();
        BloqueDTO otraVez = gestor.comenzarCuestionario(cuestionario.getId());
        assertEquals(bloques, cuestionario.getBloques());
        assertEquals(1, otraVez.getNroBloque());
        assertEquals(1, llamadas.stream().filter("guardarCuestionario"::equals).count());
    }

    private RespuestasBloqueDTO respuestas(BloqueDTO bloque) {
        Map<UUID, UUID> respuestas = new LinkedHashMap<>();
        for (PreguntaBloqueDTO p : bloque.getPreguntas()) {
            respuestas.put(p.getPreguntaId(), DatosCU026.BUENO.getId());
        }
        return new RespuestasBloqueDTO(bloque.getCuestionarioId(), bloque.getNroBloque(), respuestas);
    }

    private Object registrar(String llamada) {
        llamadas.add(llamada);
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T fake(Class<T> tipo, Map<String, Function<Object[], Object>> metodos) {
        Map<String, Function<Object[], Object>> copia = new HashMap<>(metodos);
        return (T) Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[] {tipo}, (proxy, metodo, args) -> {
            Function<Object[], Object> f = copia.get(metodo.getName());
            if (f == null) {
                throw new UnsupportedOperationException(metodo.getName() + " no se usa en este test");
            }
            return f.apply(args);
        });
    }
}
