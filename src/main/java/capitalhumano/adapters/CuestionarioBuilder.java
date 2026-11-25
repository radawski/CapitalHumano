package capitalhumano.adapters;

import capitalhumano.dominio.Candidato;
import capitalhumano.dominio.ConfiguracionCuestionario;
import capitalhumano.dominio.Cuestionario;

/** Patrón Builder: FabricaCuestionarios crea uno por cuestionario. */
public class CuestionarioBuilder {

    private Candidato candidato;
    private ConfiguracionCuestionario configuracion;

    public CuestionarioBuilder conCandidato(Candidato candidato) {
        throw new UnsupportedOperationException("TODO #12");
    }

    public CuestionarioBuilder conConfiguracion(ConfiguracionCuestionario configuracion) {
        throw new UnsupportedOperationException("TODO #12");
    }

    public Cuestionario construir() {
        throw new UnsupportedOperationException("TODO #12");
    }
}
