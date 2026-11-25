package capitalhumano.dominio;

import capitalhumano.enumeraciones.NombreEstadoCuestionario;

public class EstadoActivo extends EstadoCuestionario {

    @Override
    public NombreEstadoCuestionario getNombre() {
        return NombreEstadoCuestionario.ACTIVO;
    }

    @Override
    public boolean permitirIngreso() {
        throw new UnsupportedOperationException("TODO #14");
    }

    @Override
    public void iniciar(Cuestionario cuestionario) {
        throw new UnsupportedOperationException("TODO #14");
    }

    @Override
    public void marcarIncompleto(Cuestionario cuestionario) {
        throw new UnsupportedOperationException("TODO #14");
    }

    @Override
    public void marcarSinContestar(Cuestionario cuestionario) {
        throw new UnsupportedOperationException("TODO #14");
    }
}
