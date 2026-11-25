package capitalhumano.dtos;

import capitalhumano.enumeraciones.TipoAcceso;
import java.util.UUID;

public class ResultadoLoginDTO {

    private boolean autenticado;
    private TipoAcceso tipoAcceso;
    private UUID consultorId;
    private String nombreUsuario;
    private UUID cuestionarioId;
    private String claveCuestionario;

    /** Constructor sin lógica (no figura en el diagrama): lo arman las estrategias de autenticación. */
    public ResultadoLoginDTO(boolean autenticado, TipoAcceso tipoAcceso, UUID consultorId, String nombreUsuario,
                             UUID cuestionarioId, String claveCuestionario) {
        this.autenticado = autenticado;
        this.tipoAcceso = tipoAcceso;
        this.consultorId = consultorId;
        this.nombreUsuario = nombreUsuario;
        this.cuestionarioId = cuestionarioId;
        this.claveCuestionario = claveCuestionario;
    }

    public boolean isAutenticado() {
        return autenticado;
    }

    public TipoAcceso getTipoAcceso() {
        return tipoAcceso;
    }

    public UUID getConsultorId() {
        return consultorId;
    }

    public UUID getCuestionarioId() {
        return cuestionarioId;
    }

    public String getClaveCuestionario() {
        return claveCuestionario;
    }
}
