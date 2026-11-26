package capitalhumano.cu028;

import static capitalhumano.cu028.Fakes.fake;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import capitalhumano.controladores.GestorOrdenMerito;
import capitalhumano.dominio.Puesto;
import capitalhumano.dtos.PuestoResumenDTO;
import capitalhumano.persistencia.EvaluacionDAO;
import capitalhumano.persistencia.PuestoDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** CU028 pasos 3–4 (#5): búsqueda de puestos con su resumen. */
class GestorOrdenMeritoBusquedaTest {

    private static final Puesto P001 =
            new Puesto("P001", "DESARROLLADOR JAVA SENIOR", "", "TECH SOLUTIONS SA", true, Map.of());
    private static final Puesto P003 =
            new Puesto("P003", "ANALISTA FUNCIONAL", "", "TECH SOLUTIONS SA", true, Map.of());

    private final List<String> filtrosRecibidos = new ArrayList<>();

    @Test
    void buscarPuestos_conResultados_armaElResumenDeCadaPuesto() {
        GestorOrdenMerito gestor = gestor(List.of(P001, P003));

        List<PuestoResumenDTO> resumenes = gestor.buscarPuestos("", "", "TECH");

        assertEquals(List.of("", "", "TECH"), filtrosRecibidos);
        assertEquals(2, resumenes.size());
        PuestoResumenDTO p001 = resumenes.get(0);
        assertEquals("P001", p001.getCodigo());
        assertEquals("DESARROLLADOR JAVA SENIOR", p001.getNombre());
        assertEquals("TECH SOLUTIONS SA", p001.getEmpresa());
        assertEquals(13, p001.getCantidadCandidatos());
        assertEquals(4, p001.getEvaluacionesCompletas());
        PuestoResumenDTO p003 = resumenes.get(1);
        assertEquals("P003", p003.getCodigo());
        assertEquals(0, p003.getCantidadCandidatos());
        assertEquals(0, p003.getEvaluacionesCompletas());
    }

    @Test
    void buscarPuestos_sinCoincidencias_devuelveListaVacia() {
        GestorOrdenMerito gestor = gestor(List.of());

        assertTrue(gestor.buscarPuestos("X999", "", "").isEmpty());
    }

    /** Conteos como los del seed: P001 tiene 13 candidatos y 4 cuestionarios completos; P003, ninguno. */
    private GestorOrdenMerito gestor(List<Puesto> encontrados) {
        PuestoDAO puestoDAO = fake(PuestoDAO.class, Map.of("buscarPorFiltros", args -> {
            filtrosRecibidos.add((String) args[0]);
            filtrosRecibidos.add((String) args[1]);
            filtrosRecibidos.add((String) args[2]);
            return encontrados;
        }));
        EvaluacionDAO evaluacionDAO = fake(EvaluacionDAO.class, Map.of(
                "contarCandidatosPorPuesto", args -> P001.equals(args[0]) ? 13 : 0,
                "contarCuestionariosCompletosPorPuesto", args -> P001.equals(args[0]) ? 4 : 0));
        return new GestorOrdenMerito(puestoDAO, evaluacionDAO);
    }
}
