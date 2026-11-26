package capitalhumano.controladores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import capitalhumano.adapters.AdaptadorGeneracionIAExterna;
import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Factor;
import capitalhumano.dominio.OpcionDeRespuesta;
import capitalhumano.dominio.Pregunta;
import capitalhumano.dominio.ValorOpcion;
import capitalhumano.dtos.DatosPreguntaDTO;
import capitalhumano.dtos.ResultadoPreguntaDTO;
import capitalhumano.enumeraciones.TipoCompetencia;
import capitalhumano.persistencia.CompetenciaDAO;
import capitalhumano.persistencia.PreguntaDAO;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GestorPreguntasTest {

    private PreguntaDAOFalso preguntaDAO;
    private GestorPreguntas gestor;
    private Factor factor;
    private ValorOpcion vMalo;
    private ValorOpcion vRegular;
    private ValorOpcion vBueno;
    private ValorOpcion vMuyBueno;
    private OpcionDeRespuesta opcion;

    @BeforeEach
    void setUp() {
        preguntaDAO = new PreguntaDAOFalso();
        gestor = new GestorPreguntas(new CompetenciaDAOFalso(), preguntaDAO, new AdaptadorGeneracionIAExterna());

        Competencia comp = new Competencia("COMP01", "PROGRAMACION JAVA", "", TipoCompetencia.TECNICA, true);
        factor = new Factor("FAC01", "POO", "", 1, true, comp);

        vMalo = new ValorOpcion(UUID.randomUUID(), 1, "MALO", true);
        vRegular = new ValorOpcion(UUID.randomUUID(), 2, "REGULAR", true);
        vBueno = new ValorOpcion(UUID.randomUUID(), 3, "BUENO", true);
        vMuyBueno = new ValorOpcion(UUID.randomUUID(), 4, "MUY BUENO", true);
        opcion = new OpcionDeRespuesta("OPC01", "ESCALA", "", true, List.of(vMalo, vRegular, vBueno, vMuyBueno));
    }

    @Test
    void validarPonderacionesAceptaSumaExactaDiezConEnterosYDecimales() throws Exception {
        assertTrue(invocarValidarPonderaciones(mapa(0.0, 1.0, 3.0, 6.0)));
        assertTrue(invocarValidarPonderaciones(mapa(2.5, 2.5, 2.5, 2.5)));
        assertTrue(invocarValidarPonderaciones(mapa(0.0, 0.0, 0.0, 10.0)));
    }

    @Test
    void validarPonderacionesRechazaSumaDistintaDeDiez() throws Exception {
        assertFalse(invocarValidarPonderaciones(mapa(1.0, 2.0, 3.0, 2.0)));
        assertFalse(invocarValidarPonderaciones(mapa(4.0, 4.0, 3.0, 0.0)));
    }

    @Test
    void validarPonderacionesRechazaValoresNegativosONulosOVacio() throws Exception {
        assertFalse(invocarValidarPonderaciones(mapa(-2.0, 4.0, 4.0, 4.0)));
        assertFalse(invocarValidarPonderaciones(Map.of()));
        assertFalse(invocarValidarPonderaciones(null));

        Map<ValorOpcion, Double> conNull = new LinkedHashMap<>();
        conNull.put(vMalo, null);
        conNull.put(vMuyBueno, 10.0);
        assertFalse(invocarValidarPonderaciones(conNull));
    }

    @Test
    void darDeAltaPreguntaGuardaCuandoDatosYPonderacionesSonValidos() {
        DatosPreguntaDTO datos = new DatosPreguntaDTO(
                factor, "herencia en java", "como aplica herencia?", "desc", opcion, mapa(0.0, 1.0, 3.0, 6.0));

        ResultadoPreguntaDTO resultado = gestor.darDeAltaPregunta(datos);

        assertTrue(resultado.isExitoso());
        assertTrue(resultado.getErrores().isEmpty());
        assertNotNull(preguntaDAO.ultimaGuardada);
        assertEquals("HERENCIA EN JAVA", preguntaDAO.ultimaGuardada.getNombre());
        assertEquals(4, preguntaDAO.ultimaGuardada.getPonderaciones().size());
    }

    @Test
    void darDeAltaPreguntaRechazaPonderacionesInvalidasSinGuardar() {
        DatosPreguntaDTO datos = new DatosPreguntaDTO(
                factor, "NUEVA", "ENUNCIADO NUEVO", "", opcion, mapa(1.0, 2.0, 3.0, 3.0));

        ResultadoPreguntaDTO resultado = gestor.darDeAltaPregunta(datos);

        assertFalse(resultado.isExitoso());
        assertFalse(resultado.getErrores().isEmpty());
        assertEquals(0, preguntaDAO.cantidadGuardadas);
    }

    @Test
    void darDeAltaPreguntaRechazaNombreOEnunciadoDuplicadoEnElFactor() {
        preguntaDAO.nombreExistente = true;
        preguntaDAO.enunciadoExistente = true;
        DatosPreguntaDTO datos = new DatosPreguntaDTO(
                factor, "DUPLICADA", "ENUNCIADO DUPLICADO", "", opcion, mapa(0.0, 1.0, 3.0, 6.0));

        ResultadoPreguntaDTO resultado = gestor.darDeAltaPregunta(datos);

        assertFalse(resultado.isExitoso());
        assertEquals(2, resultado.getErrores().size());
        assertEquals(0, preguntaDAO.cantidadGuardadas);
    }

    private boolean invocarValidarPonderaciones(Map<ValorOpcion, Double> ponderaciones) throws Exception {
        Method metodo = GestorPreguntas.class.getDeclaredMethod("validarPonderaciones", Map.class);
        metodo.setAccessible(true);
        return (boolean) metodo.invoke(gestor, ponderaciones);
    }

    private Map<ValorOpcion, Double> mapa(double p1, double p2, double p3, double p4) {
        Map<ValorOpcion, Double> map = new LinkedHashMap<>();
        map.put(vMalo, p1);
        map.put(vRegular, p2);
        map.put(vBueno, p3);
        map.put(vMuyBueno, p4);
        return map;
    }

    private static class PreguntaDAOFalso implements PreguntaDAO {
        boolean nombreExistente;
        boolean enunciadoExistente;
        int cantidadGuardadas;
        Pregunta ultimaGuardada;

        @Override
        public void guardar(Pregunta pregunta) {
            this.cantidadGuardadas++;
            this.ultimaGuardada = pregunta;
        }

        @Override
        public boolean existePorNombre(Factor factor, String nombre) {
            return nombreExistente;
        }

        @Override
        public boolean existePorEnunciado(Factor factor, String enunciado) {
            return enunciadoExistente;
        }

        @Override public void actualizar(Pregunta pregunta) {}
        @Override public void eliminarLogico(Pregunta pregunta) {}
        @Override public List<Pregunta> buscar(Competencia c, Factor f, String n) { return List.of(); }
        @Override public List<Pregunta> obtenerPreguntasActivasPorFactor(Factor f) { return List.of(); }
        @Override public void guardarOpcionDeRespuesta(OpcionDeRespuesta o) {}
        @Override public void actualizarOpcionDeRespuesta(OpcionDeRespuesta o) {}
        @Override public void eliminarLogicoOpcionDeRespuesta(OpcionDeRespuesta o) {}
        @Override public List<OpcionDeRespuesta> buscarOpcionesDeRespuesta(String n) { return List.of(); }
    }

    private static class CompetenciaDAOFalso implements CompetenciaDAO {
        @Override public void guardar(Competencia c) {}
        @Override public void actualizar(Competencia c) {}
        @Override public void eliminarLogico(Competencia c) {}
        @Override public List<Competencia> buscarPorNombre(String n) { return List.of(); }
        @Override public List<Factor> obtenerFactores(Competencia c) { return List.of(); }
        @Override public void guardarFactor(Factor f) {}
        @Override public void actualizarFactor(Factor f) {}
        @Override public void eliminarLogicoFactor(Factor f) {}
    }
}
