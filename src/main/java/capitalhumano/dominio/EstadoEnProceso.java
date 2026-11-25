package capitalhumano.dominio;

import capitalhumano.enumeraciones.NombreEstadoCuestionario;

public class EstadoEnProceso extends EstadoCuestionario {

    @Override
    public NombreEstadoCuestionario getNombre() {
        return NombreEstadoCuestionario.EN_PROCESO;
    }

    @Override
    public boolean permitirIngreso() {
        throw new UnsupportedOperationException("TODO #14");
    }

    @Override
    public void completar(Cuestionario cuestionario) {
        throw new UnsupportedOperationException("TODO #14");
    }

    @Override
    public void marcarIncompleto(Cuestionario cuestionario) {
        throw new UnsupportedOperationException("TODO #14");
    }
}
