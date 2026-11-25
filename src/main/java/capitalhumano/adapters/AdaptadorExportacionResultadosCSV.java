package capitalhumano.adapters;

import capitalhumano.dominio.Cuestionario;
import capitalhumano.servicios.ServicioExportacionResultados;
import java.util.List;

public class AdaptadorExportacionResultadosCSV implements ServicioExportacionResultados {

    @Override
    public void exportarResultados(List<Cuestionario> cuestionarios, String nombrePuesto, String carpeta) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU007)");
    }
}
