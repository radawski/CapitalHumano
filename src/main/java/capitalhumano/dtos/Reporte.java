package capitalhumano.dtos;

import java.time.LocalDateTime;

public abstract class Reporte {

    private LocalDateTime fechaEmision = LocalDateTime.now();

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }
}
