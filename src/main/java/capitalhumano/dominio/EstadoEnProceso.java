package capitalhumano.dominio;

import capitalhumano.enumeraciones.NombreEstadoCuestionario;

public class EstadoEnProceso extends EstadoCuestionario {

    @Override
    public NombreEstadoCuestionario getNombre() {
        return NombreEstadoCuestionario.EN_PROCESO;
    }

    @Override
    public boolean permitirIngreso() {
        return true;
    }

    @Override
    public void completar(Cuestionario cuestionario) {
        cuestionario.setEstado(new EstadoCompleto());
    }

    @Override
    public void marcarIncompleto(Cuestionario cuestionario) {
        cuestionario.setEstado(new EstadoIncompleto());
    }
}
