package capitalhumano.controladores;

import capitalhumano.dtos.RegistroImportacionDTO;
import capitalhumano.persistencia.CandidatoDAO;
import capitalhumano.servicios.ServicioImportacionCandidatos;

public class GestorImportacionCandidatos {

    private ServicioImportacionCandidatos servicioImportacion;
    private CandidatoDAO candidatoDAO;

    public GestorImportacionCandidatos(ServicioImportacionCandidatos servicioImportacion, CandidatoDAO candidatoDAO) {
        this.servicioImportacion = servicioImportacion;
        this.candidatoDAO = candidatoDAO;
    }

    public RegistroImportacionDTO importarCandidatos(String rutaArchivo) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU006)");
    }
}
