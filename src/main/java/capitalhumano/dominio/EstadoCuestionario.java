package capitalhumano.dominio;

import capitalhumano.enumeraciones.NombreEstadoCuestionario;

/**
 * Patrón State. Cada estado concreto decide la transición con {@code cuestionario.setEstado(...)};
 * las transiciones que un estado no redefine son inválidas y lanzan excepción.
 */
public abstract class EstadoCuestionario {

    public abstract NombreEstadoCuestionario getNombre();

    public boolean permitirIngreso() {
        return false;
    }

    public void iniciar(Cuestionario cuestionario) {
        transicionInvalida("iniciar");
    }

    public void completar(Cuestionario cuestionario) {
        transicionInvalida("completar");
    }

    public void marcarIncompleto(Cuestionario cuestionario) {
        transicionInvalida("marcarIncompleto");
    }

    public void marcarSinContestar(Cuestionario cuestionario) {
        transicionInvalida("marcarSinContestar");
    }

    private void transicionInvalida(String operacion) {
        throw new IllegalStateException(
                "Transición inválida '" + operacion + "' desde el estado " + getNombre());
    }
}
