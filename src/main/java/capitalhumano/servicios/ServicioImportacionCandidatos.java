package capitalhumano.servicios;

import capitalhumano.dominio.Candidato;
import java.util.List;

public interface ServicioImportacionCandidatos {

    List<Candidato> leerCandidatos(String rutaArchivo);
}
