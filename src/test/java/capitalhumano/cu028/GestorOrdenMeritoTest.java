package capitalhumano.cu028;

import static capitalhumano.cu028.Fakes.fake;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import capitalhumano.controladores.GestorOrdenMerito;
import capitalhumano.dominio.Evaluacion;
import capitalhumano.dominio.Puesto;
import capitalhumano.dtos.EvaluacionResumenDTO;
import capitalhumano.dtos.ItemOrdenMeritoDTO;
import capitalhumano.dtos.OrdenMeritoDTO;
import capitalhumano.enumeraciones.NombreEstadoCuestionario;
import capitalhumano.persistencia.EvaluacionDAO;
import capitalhumano.persistencia.PuestoDAO;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** CU028 pasos 5–7 (#6) con los datos del seed para P001 (EVAL1 y EVAL2). */
class GestorOrdenMeritoTest {

    private final Puesto p001 = DatosCU028.p001();

    private final Evaluacion eval1 = DatosCU028.evaluacion(p001,
            DatosCU028.completo("SANCHEZ", 6f, 7f),      // COMPLE01: 6,50
            DatosCU028.completo("ROMERO", 8f, 8f),       // COMPLE02: 8,00
            DatosCU028.completo("TORRES", 3f, 8f),       // COMPLE03: 5,50, falla COMP01
            DatosCU028.activo("GOMEZ"),
            DatosCU028.activo("FERNANDEZ"),
            DatosCU028.activo("LOPEZ"),
            DatosCU028.enProceso("MARTINEZ"),
            DatosCU028.enProceso("GARCIA"),
            DatosCU028.enProceso("DIAZ"),
            DatosCU028.incompleto("ALVAREZ"),
            DatosCU028.sinContestar("BENITEZ"));
    private final Evaluacion eval2 = DatosCU028.evaluacion(p001,
            DatosCU028.completo("RUIZ", 7f, 7.5f),       // EVAL2C01: 7,25
            DatosCU028.incompleto("CASTRO"));

    @Test
    void obtenerEvaluaciones_resumeCadaEvaluacionDelPuesto() {
        List<EvaluacionResumenDTO> resumenes = gestor().obtenerEvaluaciones("P001");

        assertEquals(2, resumenes.size());
        assertEquals(eval1.getId(), resumenes.get(0).getEvaluacionId());
        assertEquals(11, resumenes.get(0).getCantidadCandidatos());
        assertEquals(2, resumenes.get(1).getCantidadCandidatos());
    }

    @Test
    void obtenerEvaluaciones_puestoSinEvaluaciones_devuelveListaVacia() {
        GestorOrdenMerito gestor = new GestorOrdenMerito(
                fake(PuestoDAO.class, Map.of("obtenerConCompetencias", args -> p001)),
                fake(EvaluacionDAO.class, Map.of("obtenerEvaluacionesPorPuesto", args -> List.of())));

        assertTrue(gestor.obtenerEvaluaciones("P003").isEmpty());
    }

    @Test
    void generarOrdenMerito_todas_clasificaComoElSeed() {
        OrdenMeritoDTO orden = gestor().generarOrdenMerito("P001", List.of(eval1.getId(), eval2.getId()));

        assertEquals("P001", orden.getPuestoCodigo());
        assertEquals(2, orden.getEvaluaciones().size());
        assertEquals(List.of("ROMERO", "RUIZ", "SANCHEZ"), apellidos(orden.getAprobados()));
        assertEquals(List.of(8.00f, 7.25f, 6.50f),
                orden.getAprobados().stream().map(ItemOrdenMeritoDTO::getPuntajeObtenido).toList());
        assertEquals(List.of(1, 2, 3), orden.getAprobados().stream().map(ItemOrdenMeritoDTO::getPosicion).toList());
        assertEquals(List.of("TORRES"), apellidos(orden.getNoAprobados()));
        assertEquals(5.50f, orden.getNoAprobados().get(0).getPuntajeObtenido(), 1e-4);

        Map<NombreEstadoCuestionario, List<ItemOrdenMeritoDTO>> noCompletos = orden.getNoCompletos();
        assertEquals(3, noCompletos.get(NombreEstadoCuestionario.ACTIVO).size());
        assertEquals(3, noCompletos.get(NombreEstadoCuestionario.EN_PROCESO).size());
        assertEquals(List.of("ALVAREZ", "CASTRO"), apellidos(noCompletos.get(NombreEstadoCuestionario.INCOMPLETO)));
        assertEquals(1, noCompletos.get(NombreEstadoCuestionario.SIN_CONTESTAR).size());
        noCompletos.values().forEach(g -> g.forEach(i -> assertNull(i.getPuntajeObtenido())));
    }

    @Test
    void generarOrdenMerito_soloEval1_dejaAfueraLaSegunda() {
        OrdenMeritoDTO orden = gestor().generarOrdenMerito("P001", List.of(eval1.getId()));

        assertEquals(List.of("ROMERO", "SANCHEZ"), apellidos(orden.getAprobados()));
        assertEquals(List.of("ALVAREZ"),
                apellidos(orden.getNoCompletos().get(NombreEstadoCuestionario.INCOMPLETO)));
    }

    private GestorOrdenMerito gestor() {
        List<Evaluacion> todas = List.of(eval1, eval2);
        PuestoDAO puestoDAO = fake(PuestoDAO.class, Map.of("obtenerConCompetencias", args -> p001));
        EvaluacionDAO evaluacionDAO = fake(EvaluacionDAO.class, Map.of(
                "obtenerEvaluacionesPorPuesto", args -> todas,
                "obtenerEvaluaciones", args -> {
                    @SuppressWarnings("unchecked")
                    List<UUID> ids = (List<UUID>) args[0];
                    return todas.stream().filter(e -> ids.contains(e.getId())).toList();
                }));
        return new GestorOrdenMerito(puestoDAO, evaluacionDAO);
    }

    private static List<String> apellidos(List<ItemOrdenMeritoDTO> items) {
        return items.stream().map(ItemOrdenMeritoDTO::getApellido).toList();
    }
}
