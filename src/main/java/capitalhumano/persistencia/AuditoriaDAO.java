package capitalhumano.persistencia;

import capitalhumano.dominio.RegistroAuditoria;
import capitalhumano.dominio.RegistroEjecucion;

public interface AuditoriaDAO {

    void registrar(RegistroAuditoria registro);

    void registrarEjecucion(RegistroEjecucion registro);
}
