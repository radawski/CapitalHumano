package capitalhumano.adapters;

import capitalhumano.controladores.GestorCuestionarios;
import capitalhumano.dtos.CredencialesLoginDTO;
import capitalhumano.dtos.ResultadoLoginDTO;
import capitalhumano.servicios.EstrategiaAutenticacion;

public class AutenticacionCandidatoClave implements EstrategiaAutenticacion {

    private GestorCuestionarios cuestionarios;

    public AutenticacionCandidatoClave(GestorCuestionarios cuestionarios) {
        this.cuestionarios = cuestionarios;
    }

    @Override
    public ResultadoLoginDTO autenticar(CredencialesLoginDTO credenciales) {
        throw new UnsupportedOperationException("TODO #18");
    }
}
