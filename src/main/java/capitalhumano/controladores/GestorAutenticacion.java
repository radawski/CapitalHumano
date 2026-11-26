package capitalhumano.controladores;

import capitalhumano.dtos.CredencialesLoginDTO;
import capitalhumano.dtos.ResultadoLoginDTO;
import capitalhumano.servicios.EstrategiaAutenticacion;

/**
 * Contexto del patrón Strategy de CU001. Guarda la estrategia elegida, así que no se comparte entre
 * requests: {@code PantallaAutenticar} crea uno por cada intento de ingreso.
 */
public class GestorAutenticacion {

    private EstrategiaAutenticacion estrategia;

    public void seleccionarEstrategia(EstrategiaAutenticacion estrategia) {
        this.estrategia = estrategia;
    }

    public ResultadoLoginDTO autenticar(CredencialesLoginDTO credenciales) {
        if (estrategia == null) {
            throw new IllegalStateException("No se seleccionó la estrategia de autenticación");
        }
        return estrategia.autenticar(credenciales);
    }
}
