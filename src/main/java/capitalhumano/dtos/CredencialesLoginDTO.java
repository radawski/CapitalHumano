package capitalhumano.dtos;

import capitalhumano.enumeraciones.TipoAcceso;
import capitalhumano.enumeraciones.TipoDocumento;

public class CredencialesLoginDTO {

    private TipoAcceso tipoAcceso;
    private String usuario;
    private String password;
    private TipoDocumento tipoDocumento;
    private String nroDocumento;
    private String clave;

    /** Constructor sin lógica (no figura en el diagrama): lo arma PantallaAutenticar. */
    public CredencialesLoginDTO(TipoAcceso tipoAcceso, String usuario, String password,
                                TipoDocumento tipoDocumento, String nroDocumento, String clave) {
        this.tipoAcceso = tipoAcceso;
        this.usuario = usuario;
        this.password = password;
        this.tipoDocumento = tipoDocumento;
        this.nroDocumento = nroDocumento;
        this.clave = clave;
    }

    public TipoAcceso getTipoAcceso() {
        return tipoAcceso;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPassword() {
        return password;
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
