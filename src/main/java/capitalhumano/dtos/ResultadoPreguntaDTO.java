package capitalhumano.dtos;

import java.util.List;

public class ResultadoPreguntaDTO {

    private boolean exitoso;
    private List<String> errores;

    public ResultadoPreguntaDTO(boolean exitoso, List<String> errores) {
        this.exitoso = exitoso;
        this.errores = errores;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public List<String> getErrores() {
        return errores;
    }
}
