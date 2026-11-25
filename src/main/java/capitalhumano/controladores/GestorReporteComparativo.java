package capitalhumano.controladores;

import capitalhumano.dtos.ReporteComparativoDTO;
import capitalhumano.persistencia.EvaluacionDAO;

public class GestorReporteComparativo {

    private EvaluacionDAO evaluacionDAO;

    public GestorReporteComparativo(EvaluacionDAO evaluacionDAO) {
        this.evaluacionDAO = evaluacionDAO;
    }

    public ReporteComparativoDTO generarComparativo(String puestoCodigo, int nroCandidato) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU027)");
    }
}
