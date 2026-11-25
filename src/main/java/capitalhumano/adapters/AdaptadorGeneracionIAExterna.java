package capitalhumano.adapters;

import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Factor;
import capitalhumano.dominio.Pregunta;
import capitalhumano.dominio.Puesto;
import capitalhumano.enumeraciones.TipoPregunta;
import capitalhumano.servicios.ServicioGeneracionIA;
import java.util.Map;
import java.util.UUID;

/**
 * Adaptador del servicio de IA externo (CU033).
 * <p>
 * <b>Versión simulada</b> (#1): devuelve siempre la misma pregunta, sin llamar a ninguna API.
 * La versión real es la #9 (opcional).
 */
public class AdaptadorGeneracionIAExterna implements ServicioGeneracionIA {

    private String clienteIA = "simulado";

    @Override
    public Pregunta generarPregunta(Competencia competencia, Factor factor) {
        return new Pregunta(
                UUID.randomUUID(),
                "PREGUNTA GENERADA",
                "¿CON QUÉ FRECUENCIA APLICA " + factor.getNombre() + " EN SU TRABAJO DIARIO?",
                "PREGUNTA DE EJEMPLO GENERADA POR EL SERVICIO DE IA SIMULADO",
                TipoPregunta.SINGLE_CHOICE,
                true,
                factor,
                null,
                Map.of());
    }

    @Override
    public Puesto generarPuesto(String descripcion) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }
}
