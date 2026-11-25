package capitalhumano.dominio;

import java.util.ArrayList;
import java.util.List;

/** Sujeto del patrón Observer: las entidades de catálogo notifican sus bajas lógicas. */
public abstract class SujetoAuditable {

    private List<ObservadorAuditoria> observadores = new ArrayList<>();

    public void registrarObservador(ObservadorAuditoria observador) {
        throw new UnsupportedOperationException("TODO #4");
    }

    public void eliminarObservador(ObservadorAuditoria observador) {
        throw new UnsupportedOperationException("TODO #4");
    }

    public void notificarObservadores(RegistroAuditoria registro) {
        throw new UnsupportedOperationException("TODO #4");
    }

    public void darDeBaja(String usuario) {
        throw new UnsupportedOperationException("TODO #4");
    }

    protected RegistroAuditoria construirRegistro(String usuario, String operacion) {
        throw new UnsupportedOperationException("TODO #4");
    }
}
