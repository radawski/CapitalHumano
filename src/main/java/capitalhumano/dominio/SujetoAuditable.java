package capitalhumano.dominio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Sujeto del patrón Observer: las entidades de catálogo notifican sus bajas lógicas. */
public abstract class SujetoAuditable {

    private List<ObservadorAuditoria> observadores = new ArrayList<>();

    public void registrarObservador(ObservadorAuditoria observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void eliminarObservador(ObservadorAuditoria observador) {
        observadores.remove(observador);
    }

    public void notificarObservadores(RegistroAuditoria registro) {
        for (ObservadorAuditoria observador : observadores) {
            observador.actualizar(registro);
        }
    }

    public void darDeBaja(String usuario) {
        notificarObservadores(construirRegistro(usuario, "BAJA_LOGICA"));
    }

    protected RegistroAuditoria construirRegistro(String usuario, String operacion) {
        return new RegistroAuditoria(usuario, LocalDateTime.now(), operacion, getClass().getSimpleName(), "");
    }
}
