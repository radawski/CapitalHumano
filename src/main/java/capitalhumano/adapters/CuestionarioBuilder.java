package capitalhumano.adapters;

import capitalhumano.dominio.Candidato;
import capitalhumano.dominio.ConfiguracionCuestionario;
import capitalhumano.dominio.Cuestionario;

/** Patrón Builder: FabricaCuestionarios crea uno por cuestionario. */
public class CuestionarioBuilder {

    private Candidato candidato;
    private ConfiguracionCuestionario configuracion;

    public CuestionarioBuilder conCandidato(Candidato candidato) {
        this.candidato = candidato;
        return this;
    }

    public CuestionarioBuilder conConfiguracion(ConfiguracionCuestionario configuracion) {
        this.configuracion = configuracion;
        return this;
    }

    public Cuestionario construir() {
        if (candidato == null) {
            throw new IllegalStateException("Debe asignar un candidato antes de construir el cuestionario");
        }
        if (configuracion == null) {
            throw new IllegalStateException("Debe asignar una configuración antes de construir el cuestionario");
        }
        return new Cuestionario(candidato, configuracion);
    }
}
