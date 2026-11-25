package capitalhumano.dominio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Puesto extends SujetoAuditable {

    private String codigo;
    private String nombre;
    private String descripcion;
    private String empresa;
    private boolean activo;
    /** Asociación Puesto "0..*" -- "1..*" Competencia con su clase de asociación PonderacionPuesto. */
    private Map<Competencia, PonderacionPuesto> ponderaciones;

    /** Constructor de reconstrucción (no figura en el diagrama): lo usan los DAO. */
    public Puesto(String codigo, String nombre, String descripcion, String empresa, boolean activo,
                  Map<Competencia, PonderacionPuesto> ponderaciones) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.empresa = empresa;
        this.activo = activo;
        this.ponderaciones = new LinkedHashMap<>(ponderaciones);
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmpresa() {
        return empresa;
    }

    public List<Competencia> getCompetencias() {
        return new ArrayList<>(ponderaciones.keySet());
    }

    public double getPonderacionRequerida(Competencia competencia) {
        PonderacionPuesto ponderacion = ponderaciones.get(competencia);
        if (ponderacion == null) {
            throw new IllegalArgumentException(
                    "El puesto " + codigo + " no requiere la competencia " + competencia.getCodigo());
        }
        return ponderacion.getPonderacion();
    }

    @Override
    public void darDeBaja(String usuario) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Puesto otro && Objects.equals(codigo, otro.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }
}
