package capitalhumano.dtos;

import capitalhumano.dominio.Factor;
import capitalhumano.dominio.OpcionDeRespuesta;
import capitalhumano.dominio.ValorOpcion;
import java.util.Map;

public class DatosPreguntaDTO {

    private Factor factor;
    private String nombre;
    private String enunciado;
    private String descripcion;
    private OpcionDeRespuesta opcion;
    private Map<ValorOpcion, Double> ponderaciones;

    public DatosPreguntaDTO(Factor factor, String nombre, String enunciado, String descripcion,
                            OpcionDeRespuesta opcion, Map<ValorOpcion, Double> ponderaciones) {
        this.factor = factor;
        this.nombre = nombre;
        this.enunciado = enunciado;
        this.descripcion = descripcion;
        this.opcion = opcion;
        this.ponderaciones = ponderaciones;
    }

    public Factor getFactor() {
        return factor;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public OpcionDeRespuesta getOpcion() {
        return opcion;
    }

    public Map<ValorOpcion, Double> getPonderaciones() {
        return ponderaciones;
    }
}
