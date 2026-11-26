package capitalhumano.dominio;

import capitalhumano.enumeraciones.NombreEstadoCuestionario;

public class EstadoActivo extends EstadoCuestionario {

    @Override
    public NombreEstadoCuestionario getNombre() {
        return NombreEstadoCuestionario.ACTIVO;
    }

    @Override
    public boolean permitirIngreso() {
        return true;
    }

    @Override
    public void iniciar(Cuestionario cuestionario) {
        cuestionario.setEstado(new EstadoEnProceso());
    }

    @Override
    public void marcarIncompleto(Cuestionario cuestionario) {
        cuestionario.setEstado(new EstadoIncompleto());
    }

    @Override
    public void marcarSinContestar(Cuestionario cuestionario) {
        cuestionario.setEstado(new EstadoSinContestar());
    }
}
