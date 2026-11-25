package capitalhumano.adapters;

import capitalhumano.dominio.Candidato;
import capitalhumano.servicios.ServicioImportacionCandidatos;
import java.util.List;

public class AdaptadorImportacionCSV implements ServicioImportacionCandidatos {

    @Override
    public List<Candidato> leerCandidatos(String rutaArchivo) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU006)");
    }
}
