package capitalhumano.dtos;

public class ReporteComparativoDTO extends Reporte {

    private String puestoCodigo;
    private String puestoNombre;
    private int nroCandidato;
    private String apellidoCandidato;
    private String nombreCandidato;

    public String getPuestoCodigo() {
        return puestoCodigo;
    }

    public String getPuestoNombre() {
        return puestoNombre;
    }

    public int getNroCandidato() {
        return nroCandidato;
    }

    public String getApellidoCandidato() {
        return apellidoCandidato;
    }

    public String getNombreCandidato() {
        return nombreCandidato;
    }
}
