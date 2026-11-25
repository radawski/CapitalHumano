package capitalhumano.dominio;

import capitalhumano.enumeraciones.NombreEstadoCuestionario;

/**
 * Patrón State. Cada estado concreto decide la transición con {@code cuestionario.setEstado(...)};
 * las transiciones que un estado no redefine son inválidas y lanzan excepción.
 */
public abstract class EstadoCuestionario {

    public abstract NombreEstadoCuestionario getNombre();

    public boolean permitirIngreso() {
        throw new UnsupportedOperationException("TODO #14");
    }

    public void iniciar(Cuestionario cuestionario) {
        throw new UnsupportedOperationException("TODO #14");
    }

    public void completar(Cuestionario cuestionario) {
        throw new UnsupportedOperationException("TODO #14");
    }

    public void marcarIncompleto(Cuestionario cuestionario) {
        throw new UnsupportedOperationException("TODO #14");
    }

    public void marcarSinContestar(Cuestionario cuestionario) {
        throw new UnsupportedOperationException("TODO #14");
    }
}
