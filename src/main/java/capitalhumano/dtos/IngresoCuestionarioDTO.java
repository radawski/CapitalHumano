package capitalhumano.dtos;

import capitalhumano.enumeraciones.ResultadoIngreso;
import java.util.UUID;

public class IngresoCuestionarioDTO {

    private ResultadoIngreso resultado;
    private UUID cuestionarioId;
    private String instrucciones;
    private String mensaje;

    public IngresoCuestionarioDTO(ResultadoIngreso resultado, String mensaje) {
        throw new UnsupportedOperationException("TODO #14");
    }

    public IngresoCuestionarioDTO(ResultadoIngreso resultado, UUID cuestionarioId) {
        throw new UnsupportedOperationException("TODO #14");
    }

    public IngresoCuestionarioDTO(ResultadoIngreso resultado, UUID cuestionarioId, String instrucciones) {
        throw new UnsupportedOperationException("TODO #14");
    }

    public ResultadoIngreso getResultado() {
        return resultado;
    }

    public UUID getCuestionarioId() {
        return cuestionarioId;
    }

    public String getInstrucciones() {
        return instrucciones;
    }

    public String getMensaje() {
        return mensaje;
    }
}
