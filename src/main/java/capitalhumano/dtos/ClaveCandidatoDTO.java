package capitalhumano.dtos;

import capitalhumano.dominio.Candidato;
import capitalhumano.enumeraciones.TipoDocumento;

public class ClaveCandidatoDTO {

    private String apellido;
    private String nombre;
    private TipoDocumento tipoDocumento;
    private String nroDocumento;
    private String clave;

    public ClaveCandidatoDTO(Candidato candidato, String clave) {
        throw new UnsupportedOperationException("TODO #12");
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
