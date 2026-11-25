package capitalhumano.controladores;

import capitalhumano.dtos.CredencialesLoginDTO;
import capitalhumano.dtos.ResultadoLoginDTO;
import capitalhumano.servicios.EstrategiaAutenticacion;

public class GestorAutenticacion {

    private EstrategiaAutenticacion estrategia;

    public void seleccionarEstrategia(EstrategiaAutenticacion estrategia) {
        this.estrategia = estrategia;
    }

    public ResultadoLoginDTO autenticar(CredencialesLoginDTO credenciales) {
        throw new UnsupportedOperationException("TODO #18");
    }
}
