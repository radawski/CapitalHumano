package capitalhumano.dominio;

import capitalhumano.enumeraciones.NombreEstadoCuestionario;

public class EstadoCompleto extends EstadoCuestionario {

    @Override
    public NombreEstadoCuestionario getNombre() {
        return NombreEstadoCuestionario.COMPLETO;
    }
}
