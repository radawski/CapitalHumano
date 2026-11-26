package capitalhumano.dtos;

import capitalhumano.enumeraciones.ResultadoIngreso;
import java.util.UUID;

public class IngresoCuestionarioDTO {

    private ResultadoIngreso resultado;
    private UUID cuestionarioId;
    private String instrucciones;
    private String mensaje;

    public IngresoCuestionarioDTO(ResultadoIngreso resultado, String mensaje) {
        this.resultado = resultado;
        this.mensaje = mensaje;
    }

    public IngresoCuestionarioDTO(ResultadoIngreso resultado, UUID cuestionarioId) {
        this.resultado = resultado;
        this.cuestionarioId = cuestionarioId;
    }

    public IngresoCuestionarioDTO(ResultadoIngreso resultado, UUID cuestionarioId, String instrucciones) {
        this.resultado = resultado;
        this.cuestionarioId = cuestionarioId;
        this.instrucciones = instrucciones;
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
