package capitalhumano.servicios;

import capitalhumano.dtos.CredencialesLoginDTO;
import capitalhumano.dtos.ResultadoLoginDTO;

/** Patrón Strategy para CU001. */
public interface EstrategiaAutenticacion {

    ResultadoLoginDTO autenticar(CredencialesLoginDTO credenciales);
}
