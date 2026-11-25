package capitalhumano.adapters;

import capitalhumano.dtos.ClaveCandidatoDTO;
import capitalhumano.servicios.ServicioExportacionClaves;
import java.util.List;

public class AdaptadorExportacionClavesExcel implements ServicioExportacionClaves {

    @Override
    public void exportarClavesCandidatos(List<ClaveCandidatoDTO> claves, String nombrePuesto, String carpeta) {
        throw new UnsupportedOperationException("TODO #13");
    }
}
