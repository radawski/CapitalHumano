package capitalhumano.servicios;

import capitalhumano.dtos.ClaveCandidatoDTO;
import java.util.List;

public interface ServicioExportacionClaves {

    void exportarClavesCandidatos(List<ClaveCandidatoDTO> claves, String nombrePuesto, String carpeta);
}
