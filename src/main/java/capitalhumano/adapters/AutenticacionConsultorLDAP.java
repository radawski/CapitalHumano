package capitalhumano.adapters;

import capitalhumano.dtos.CredencialesLoginDTO;
import capitalhumano.dtos.ResultadoLoginDTO;
import capitalhumano.servicios.EstrategiaAutenticacion;

public class AutenticacionConsultorLDAP implements EstrategiaAutenticacion {

    private AdaptadorLDAP directorio;

    public AutenticacionConsultorLDAP(AdaptadorLDAP directorio) {
        this.directorio = directorio;
    }

    @Override
    public ResultadoLoginDTO autenticar(CredencialesLoginDTO credenciales) {
        throw new UnsupportedOperationException("TODO #18");
    }
}
