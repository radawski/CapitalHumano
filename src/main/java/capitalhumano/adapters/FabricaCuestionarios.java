package capitalhumano.adapters;

import capitalhumano.dominio.Candidato;
import capitalhumano.dominio.ConfiguracionCuestionario;
import capitalhumano.dominio.Cuestionario;

/** Patrón Factory: usa un CuestionarioBuilder por cuestionario. */
public class FabricaCuestionarios {

    public Cuestionario crearCuestionario(Candidato candidato, ConfiguracionCuestionario configuracion) {
        return new CuestionarioBuilder().conCandidato(candidato).conConfiguracion(configuracion).construir();
    }
}
