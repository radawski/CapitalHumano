package capitalhumano.adapters;

import capitalhumano.dominio.ObservadorAuditoria;
import capitalhumano.dominio.RegistroAuditoria;
import capitalhumano.persistencia.AuditoriaDAO;

/** Observador concreto: persiste las bajas lógicas de los catálogos. */
public class RegistradorAuditoria implements ObservadorAuditoria {

    private AuditoriaDAO auditoriaDAO;

    public RegistradorAuditoria(AuditoriaDAO auditoriaDAO) {
        this.auditoriaDAO = auditoriaDAO;
    }

    @Override
    public void actualizar(RegistroAuditoria registro) {
        throw new UnsupportedOperationException("TODO #4");
    }
}
