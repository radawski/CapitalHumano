package capitalhumano.controladores;

import capitalhumano.adapters.RegistradorAuditoria;
import capitalhumano.persistencia.CompetenciaDAO;
import capitalhumano.persistencia.PreguntaDAO;
import capitalhumano.persistencia.PuestoDAO;
import java.util.UUID;

public class GestorBajasCatalogos {

    private RegistradorAuditoria registrador;
    private PuestoDAO puestoDAO;
    private CompetenciaDAO competenciaDAO;
    private PreguntaDAO preguntaDAO;

    public GestorBajasCatalogos(RegistradorAuditoria registrador, PuestoDAO puestoDAO,
                                CompetenciaDAO competenciaDAO, PreguntaDAO preguntaDAO) {
        this.registrador = registrador;
        this.puestoDAO = puestoDAO;
        this.competenciaDAO = competenciaDAO;
        this.preguntaDAO = preguntaDAO;
    }

    public void darDeBajaPuesto(String codigo, String usuario) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    public void darDeBajaCompetencia(String codigo, String usuario) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    public void darDeBajaFactor(String codigo, String usuario) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    public void darDeBajaPregunta(UUID id, String usuario) {
        throw new UnsupportedOperationException("TODO #4");
    }

    public void darDeBajaOpcionDeRespuesta(String codigo, String usuario) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }
}
