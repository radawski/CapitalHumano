package capitalhumano.controladores;

import capitalhumano.adapters.RegistradorAuditoria;
import capitalhumano.dominio.Pregunta;
import capitalhumano.persistencia.CompetenciaDAO;
import capitalhumano.persistencia.PreguntaDAO;
import capitalhumano.persistencia.PuestoDAO;
import java.util.List;
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
        if (id == null) {
            throw new IllegalArgumentException("El identificador de la pregunta es obligatorio");
        }
        if (usuario == null || usuario.isBlank()) {
            throw new IllegalArgumentException("El usuario que realiza la baja es obligatorio");
        }

        List<Pregunta> activas = preguntaDAO.buscar(null, null, null);
        Pregunta pregunta = activas.stream()
                .filter(p -> id.equals(p.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró una pregunta activa con identificador: " + id));

        pregunta.registrarObservador(registrador);
        preguntaDAO.eliminarLogico(pregunta);
        pregunta.darDeBaja(usuario.trim());
    }

    public void darDeBajaOpcionDeRespuesta(String codigo, String usuario) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }
}
