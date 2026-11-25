package capitalhumano.controladores;

import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Puesto;
import capitalhumano.persistencia.CompetenciaDAO;
import capitalhumano.persistencia.PuestoDAO;
import capitalhumano.servicios.ServicioGeneracionIA;
import java.util.List;

public class GestorPuestos {

    private PuestoDAO puestoDAO;
    private CompetenciaDAO competenciaDAO;
    private ServicioGeneracionIA servicioIA;

    public GestorPuestos(PuestoDAO puestoDAO, CompetenciaDAO competenciaDAO, ServicioGeneracionIA servicioIA) {
        this.puestoDAO = puestoDAO;
        this.competenciaDAO = competenciaDAO;
        this.servicioIA = servicioIA;
    }

    public List<Puesto> buscarPuestos(String codigo, String nombre, String empresa) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    public List<Competencia> obtenerCompetencias() {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    public void darDeAltaPuesto(Puesto puesto) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    public void modificarPuesto(Puesto puesto) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    public Puesto generarPuestoConIA(String descripcion) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }
}
