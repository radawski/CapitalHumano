package capitalhumano.cu028;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import capitalhumano.dominio.Cuestionario;
import capitalhumano.dominio.Puesto;
import capitalhumano.dtos.ItemOrdenMeritoDTO;
import capitalhumano.dtos.OrdenMeritoDTO;
import capitalhumano.enumeraciones.NombreEstadoCuestionario;
import java.util.List;
import org.junit.jupiter.api.Test;

/** CU028 (#6): {@code OrdenMeritoDTO.ordenar()}. */
class OrdenarTest {

    private final Puesto p001 = DatosCU028.p001();

    @Test
    void ordenar_aprobadosPorPuntajeDescendenteConPosicionDespuesDeOrdenar() {
        OrdenMeritoDTO orden = new OrdenMeritoDTO(p001, List.of());
        orden.agregarAprobado(item(DatosCU028.completo("SANCHEZ", 6f, 7f)));   // 6,50
        orden.agregarAprobado(item(DatosCU028.completo("ROMERO", 8f, 8f)));    // 8,00
        orden.agregarAprobado(item(DatosCU028.completo("RUIZ", 7f, 7.5f)));    // 7,25

        orden.ordenar();

        assertEquals(List.of("ROMERO", "RUIZ", "SANCHEZ"), apellidos(orden.getAprobados()));
        assertEquals(List.of(1, 2, 3), orden.getAprobados().stream().map(ItemOrdenMeritoDTO::getPosicion).toList());
    }

    @Test
    void ordenar_empateDePuntaje_desempataPorApellido() {
        OrdenMeritoDTO orden = new OrdenMeritoDTO(p001, List.of());
        orden.agregarAprobado(item(DatosCU028.completo("ZAPATA", 7f, 7f)));
        orden.agregarAprobado(item(DatosCU028.completo("ACOSTA", 7f, 7f)));

        orden.ordenar();

        assertEquals(List.of("ACOSTA", "ZAPATA"), apellidos(orden.getAprobados()));
    }

    @Test
    void ordenar_noAprobadosOrdenadosYSinPosicion() {
        OrdenMeritoDTO orden = new OrdenMeritoDTO(p001, List.of());
        orden.agregarNoAprobado(item(DatosCU028.completo("TORRES", 3f, 8f)));  // 5,50
        orden.agregarNoAprobado(item(DatosCU028.completo("BRAVO", 5f, 9f)));   // 7,00

        orden.ordenar();

        assertEquals(List.of("BRAVO", "TORRES"), apellidos(orden.getNoAprobados()));
        orden.getNoAprobados().forEach(i -> assertNull(i.getPosicion()));
    }

    @Test
    void ordenar_noCompletosAgrupadosPorEstadoEnElOrdenDelEnum() {
        OrdenMeritoDTO orden = new OrdenMeritoDTO(p001, List.of());
        orden.agregarNoCompleto(NombreEstadoCuestionario.SIN_CONTESTAR, item(DatosCU028.sinContestar("VEGA")));
        orden.agregarNoCompleto(NombreEstadoCuestionario.ACTIVO, item(DatosCU028.activo("NUNEZ")));
        orden.agregarNoCompleto(NombreEstadoCuestionario.ACTIVO, item(DatosCU028.activo("GOMEZ")));

        orden.ordenar();

        assertEquals(List.of(NombreEstadoCuestionario.ACTIVO, NombreEstadoCuestionario.SIN_CONTESTAR),
                List.copyOf(orden.getNoCompletos().keySet()));
        assertEquals(List.of("GOMEZ", "NUNEZ"), apellidos(orden.getNoCompletos().get(NombreEstadoCuestionario.ACTIVO)));
        orden.getNoCompletos().values().forEach(g -> g.forEach(i -> assertNull(i.getPuntajeObtenido())));
    }

    private static ItemOrdenMeritoDTO item(Cuestionario cuestionario) {
        Float puntaje = cuestionario.getNombreEstado() == NombreEstadoCuestionario.COMPLETO
                ? cuestionario.getPuntajeTotal() : null;
        return new ItemOrdenMeritoDTO(cuestionario.getCandidato(), cuestionario, puntaje);
    }

    private static List<String> apellidos(List<ItemOrdenMeritoDTO> items) {
        return items.stream().map(ItemOrdenMeritoDTO::getApellido).toList();
    }
}
