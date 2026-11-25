package capitalhumano.dominio;

import java.time.Duration;
import java.util.UUID;

public class ConfiguracionCuestionario {

    private UUID id;
    private String instrucciones;
    private int cantidadPreguntasPorBloque;
    private int maximoAccesos;
    private Duration tiempoMaximo;
    private Duration tiempoMaximoActivo;

    /** Constructor de reconstrucción (no figura en el diagrama): lo usan los DAO. */
    public ConfiguracionCuestionario(UUID id, String instrucciones, int cantidadPreguntasPorBloque,
                                     int maximoAccesos, Duration tiempoMaximo, Duration tiempoMaximoActivo) {
        this.id = id;
        this.instrucciones = instrucciones;
        this.cantidadPreguntasPorBloque = cantidadPreguntasPorBloque;
        this.maximoAccesos = maximoAccesos;
        this.tiempoMaximo = tiempoMaximo;
        this.tiempoMaximoActivo = tiempoMaximoActivo;
    }

    public String getInstrucciones() {
        return instrucciones;
    }

    public int getCantidadPreguntasPorBloque() {
        return cantidadPreguntasPorBloque;
    }

    public int getMaximoAccesos() {
        return maximoAccesos;
    }

    public Duration getTiempoMaximo() {
        return tiempoMaximo;
    }

    public Duration getTiempoMaximoActivo() {
        return tiempoMaximoActivo;
    }
}
