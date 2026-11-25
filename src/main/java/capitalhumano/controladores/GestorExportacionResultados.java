package capitalhumano.controladores;

import capitalhumano.dominio.Puesto;
import capitalhumano.persistencia.EvaluacionDAO;
import capitalhumano.persistencia.PuestoDAO;
import capitalhumano.servicios.ServicioExportacionResultados;
import java.util.List;

public class GestorExportacionResultados {

    private PuestoDAO puestoDAO;
    private EvaluacionDAO evaluacionDAO;
    private ServicioExportacionResultados servicioExportacion;

    public GestorExportacionResultados(PuestoDAO puestoDAO, EvaluacionDAO evaluacionDAO,
                                       ServicioExportacionResultados servicioExportacion) {
        this.puestoDAO = puestoDAO;
        this.evaluacionDAO = evaluacionDAO;
        this.servicioExportacion = servicioExportacion;
    }

    public List<Puesto> buscarPuestos(String nombre, String empresa) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU007)");
    }

    public void exportarResultados(String puestoCodigo, String carpeta) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU007)");
    }
}
