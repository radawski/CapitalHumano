package capitalhumano.servicios;

import capitalhumano.dominio.Cuestionario;
import java.util.List;

public interface ServicioExportacionResultados {

    void exportarResultados(List<Cuestionario> cuestionarios, String nombrePuesto, String carpeta);
}
