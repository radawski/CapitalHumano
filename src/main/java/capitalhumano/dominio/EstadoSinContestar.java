package capitalhumano.dominio;

import capitalhumano.enumeraciones.NombreEstadoCuestionario;

public class EstadoSinContestar extends EstadoCuestionario {

    @Override
    public NombreEstadoCuestionario getNombre() {
        return NombreEstadoCuestionario.SIN_CONTESTAR;
    }
}
