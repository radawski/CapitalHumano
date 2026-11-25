package capitalhumano.dtos;

import capitalhumano.dominio.Candidato;
import capitalhumano.enumeraciones.TipoDocumento;
import java.util.Objects;

public class ClaveCandidatoDTO {

    private String apellido;
    private String nombre;
    private TipoDocumento tipoDocumento;
    private String nroDocumento;
    private String clave;

    public ClaveCandidatoDTO(Candidato candidato, String clave) {
        Objects.requireNonNull(candidato, "El candidato no puede ser nulo");
        this.apellido = candidato.getApellido();
        this.nombre = candidato.getNombre();
        this.tipoDocumento = candidato.getTipoDocumento();
        this.nroDocumento = candidato.getNroDocumento();
        this.clave = Objects.requireNonNull(clave, "La clave no puede ser nula");
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

    public String getClave() {
        return clave;
    }
}
