package capitalhumano.controladores;

import capitalhumano.persistencia.AuditoriaDAO;

public class GestorFinalizacionCuestionarios {

    private AuditoriaDAO auditoriaDAO;
    private GestorCuestionarios gestorCuestionarios;

    public GestorFinalizacionCuestionarios(AuditoriaDAO auditoriaDAO, GestorCuestionarios gestorCuestionarios) {
        this.auditoriaDAO = auditoriaDAO;
        this.gestorCuestionarios = gestorCuestionarios;
    }

    public void ejecutarVencimiento() {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU032)");
    }
}
