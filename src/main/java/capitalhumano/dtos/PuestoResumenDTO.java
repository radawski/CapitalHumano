package capitalhumano.dtos;

import capitalhumano.dominio.Puesto;

public class PuestoResumenDTO {

    private String codigo;
    private String nombre;
    private String empresa;
    private int cantidadCandidatos;
    private int evaluacionesCompletas;

    public PuestoResumenDTO(Puesto puesto, int cantidadCandidatos, int evaluacionesCompletas) {
        this.codigo = puesto.getCodigo();
        this.nombre = puesto.getNombre();
        this.empresa = puesto.getEmpresa();
        this.cantidadCandidatos = cantidadCandidatos;
        this.evaluacionesCompletas = evaluacionesCompletas;
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

    public int getCantidadCandidatos() {
        return cantidadCandidatos;
    }

    public int getEvaluacionesCompletas() {
        return evaluacionesCompletas;
    }
}
