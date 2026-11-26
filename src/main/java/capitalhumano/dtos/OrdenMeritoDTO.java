package capitalhumano.dtos;

import capitalhumano.dominio.Evaluacion;
import capitalhumano.dominio.Puesto;
import capitalhumano.enumeraciones.NombreEstadoCuestionario;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class OrdenMeritoDTO extends Reporte {

    private String puestoCodigo;
    private String puestoNombre;
    private String empresa;
    private List<EvaluacionResumenDTO> evaluaciones;
    private List<ItemOrdenMeritoDTO> aprobados;
    private List<ItemOrdenMeritoDTO> noAprobados;
    private Map<NombreEstadoCuestionario, List<ItemOrdenMeritoDTO>> noCompletos;

    /** Desempate (el diseño no lo define): apellido y después nombre. */
    private static final Comparator<ItemOrdenMeritoDTO> POR_NOMBRE =
            Comparator.comparing(ItemOrdenMeritoDTO::getApellido, Comparator.nullsLast(String::compareTo))
                    .thenComparing(ItemOrdenMeritoDTO::getNombre, Comparator.nullsLast(String::compareTo));
    private static final Comparator<ItemOrdenMeritoDTO> POR_PUNTAJE_DESCENDENTE =
            Comparator.comparing(ItemOrdenMeritoDTO::getPuntajeObtenido,
                    Comparator.nullsLast(Comparator.<Float>reverseOrder())).thenComparing(POR_NOMBRE);

    public OrdenMeritoDTO(Puesto puesto, List<Evaluacion> evaluaciones) {
        this.puestoCodigo = puesto.getCodigo();
        this.puestoNombre = puesto.getNombre();
        this.empresa = puesto.getEmpresa();
        this.evaluaciones = new ArrayList<>();
        for (Evaluacion evaluacion : evaluaciones) {
            this.evaluaciones.add(new EvaluacionResumenDTO(evaluacion));
        }
        this.aprobados = new ArrayList<>();
        this.noAprobados = new ArrayList<>();
        // EnumMap: los grupos salen siempre en el orden de NombreEstadoCuestionario.
        this.noCompletos = new EnumMap<>(NombreEstadoCuestionario.class);
    }

    public void agregarAprobado(ItemOrdenMeritoDTO item) {
        aprobados.add(item);
    }

    public void agregarNoAprobado(ItemOrdenMeritoDTO item) {
        noAprobados.add(item);
    }

    public void agregarNoCompleto(NombreEstadoCuestionario estado, ItemOrdenMeritoDTO item) {
        noCompletos.computeIfAbsent(estado, e -> new ArrayList<>()).add(item);
    }

    /**
     * Aprobados y no aprobados por puntaje descendente; la posición se asigna después de ordenar
     * y solo a los aprobados. Dentro de cada estado, los no completos van por apellido y nombre.
     */
    public void ordenar() {
        aprobados.sort(POR_PUNTAJE_DESCENDENTE);
        noAprobados.sort(POR_PUNTAJE_DESCENDENTE);
        for (List<ItemOrdenMeritoDTO> grupo : noCompletos.values()) {
            grupo.sort(POR_NOMBRE);
        }
        for (int i = 0; i < aprobados.size(); i++) {
            aprobados.get(i).setPosicion(i + 1);
        }
    }

    public String getPuestoCodigo() {
        return puestoCodigo;
    }

    public String getPuestoNombre() {
        return puestoNombre;
    }

    public String getEmpresa() {
        return empresa;
    }

    public List<EvaluacionResumenDTO> getEvaluaciones() {
        return evaluaciones;
    }

    public List<ItemOrdenMeritoDTO> getAprobados() {
        return aprobados;
    }

    public List<ItemOrdenMeritoDTO> getNoAprobados() {
        return noAprobados;
    }

    public Map<NombreEstadoCuestionario, List<ItemOrdenMeritoDTO>> getNoCompletos() {
        return noCompletos;
    }
}
