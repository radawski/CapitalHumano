package capitalhumano.dominio;

import capitalhumano.enumeraciones.Genero;
import capitalhumano.enumeraciones.TipoDocumento;
import java.time.LocalDate;
import java.util.Objects;

public class Candidato {

    private int nroCandidato;
    private String apellido;
    private String nombre;
    private TipoDocumento tipoDocumento;
    private String nroDocumento;
    private LocalDate fechaNacimiento;
    private Genero genero;
    private String nacionalidad;
    private String email;
    private String escolaridad;
    private boolean eliminado;

    /** Constructor de reconstrucción (no figura en el diagrama): lo usan los DAO. */
    public Candidato(int nroCandidato, String apellido, String nombre, TipoDocumento tipoDocumento,
                     String nroDocumento, LocalDate fechaNacimiento, Genero genero, String nacionalidad,
                     String email, String escolaridad, boolean eliminado) {
        this.nroCandidato = nroCandidato;
        this.apellido = apellido;
        this.nombre = nombre;
        this.tipoDocumento = tipoDocumento;
        this.nroDocumento = nroDocumento;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.nacionalidad = nacionalidad;
        this.email = email;
        this.escolaridad = escolaridad;
        this.eliminado = eliminado;
    }

    public int getNroCandidato() {
        return nroCandidato;
    }

    public String getApellido() {
        return apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public String getDocumentoCompleto() {
        return tipoDocumento + " " + nroDocumento;
    }

    public boolean validarDocumento() {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU006)");
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Candidato otro && nroCandidato == otro.nroCandidato;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nroCandidato);
    }
}
