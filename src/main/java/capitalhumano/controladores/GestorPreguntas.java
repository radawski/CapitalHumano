package capitalhumano.controladores;

import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Factor;
import capitalhumano.dominio.OpcionDeRespuesta;
import capitalhumano.dominio.Pregunta;
import capitalhumano.dominio.ValorOpcion;
import capitalhumano.dtos.DatosPreguntaDTO;
import capitalhumano.dtos.ResultadoPreguntaDTO;
import capitalhumano.persistencia.CompetenciaDAO;
import capitalhumano.persistencia.PreguntaDAO;
import capitalhumano.servicios.ServicioGeneracionIA;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GestorPreguntas {

    private static final double SUMA_PONDERACIONES_ESPERADA = 10.0;
    private static final double TOLERANCIA_PONDERACION = 1e-6;

    private CompetenciaDAO competenciaDAO;
    private PreguntaDAO preguntaDAO;
    private ServicioGeneracionIA servicioIA;

    public GestorPreguntas(CompetenciaDAO competenciaDAO, PreguntaDAO preguntaDAO, ServicioGeneracionIA servicioIA) {
        this.competenciaDAO = competenciaDAO;
        this.preguntaDAO = preguntaDAO;
        this.servicioIA = servicioIA;
    }

    public List<Competencia> obtenerCompetencias() {
        return competenciaDAO.buscarPorNombre("");
    }

    public List<Factor> obtenerFactores(Competencia competencia) {
        if (competencia == null) {
            return List.of();
        }
        return competenciaDAO.obtenerFactores(competencia);
    }

    public List<Pregunta> buscarPreguntas(Competencia competencia, Factor factor, String nombrePregunta) {
        String nombreNormalizado = (nombrePregunta == null || nombrePregunta.isBlank())
                ? null
                : nombrePregunta.trim().toUpperCase(Locale.ROOT);
        return preguntaDAO.buscar(competencia, factor, nombreNormalizado);
    }

    public List<OpcionDeRespuesta> obtenerOpcionesDeRespuesta() {
        return preguntaDAO.buscarOpcionesDeRespuesta("");
    }

    public List<ValorOpcion> obtenerValores(OpcionDeRespuesta opcion) {
        if (opcion == null) {
            return List.of();
        }
        return opcion.getValores();
    }

    public ResultadoPreguntaDTO darDeAltaPregunta(DatosPreguntaDTO datos) {
        List<String> errores = new ArrayList<>();
        if (datos == null) {
            errores.add("Los datos de la pregunta son obligatorios.");
            return new ResultadoPreguntaDTO(false, errores);
        }

        Factor factor = datos.getFactor();
        OpcionDeRespuesta opcion = datos.getOpcion();
        String nombre = datos.getNombre() == null ? "" : datos.getNombre().trim().toUpperCase(Locale.ROOT);
        String enunciado = datos.getEnunciado() == null ? "" : datos.getEnunciado().trim().toUpperCase(Locale.ROOT);
        String descripcion = datos.getDescripcion() == null ? "" : datos.getDescripcion().trim().toUpperCase(Locale.ROOT);
        Map<ValorOpcion, Double> ponderaciones = datos.getPonderaciones();

        if (factor == null || opcion == null || nombre.isEmpty() || enunciado.isEmpty()) {
            errores.add("Debe completar todos los datos obligatorios de la pregunta.");
            return new ResultadoPreguntaDTO(false, errores);
        }

        boolean faltanValores = false;
        if (ponderaciones == null || opcion.getValores() == null || opcion.getValores().isEmpty()) {
            faltanValores = true;
        } else {
            for (ValorOpcion valor : opcion.getValores()) {
                if (!ponderaciones.containsKey(valor) || ponderaciones.get(valor) == null) {
                    faltanValores = true;
                    break;
                }
            }
        }

        if (faltanValores || !validarPonderaciones(ponderaciones)) {
            errores.add("Las ponderaciones deben ser mayores o iguales a 0 y sumar exactamente 10 puntos.");
            return new ResultadoPreguntaDTO(false, errores);
        }

        boolean nombreExiste = preguntaDAO.existePorNombre(factor, nombre);
        boolean enunciadoExiste = preguntaDAO.existePorEnunciado(factor, enunciado);

        String nombreCompetencia = factor.getCompetencia() != null ? factor.getCompetencia().getNombre() : "";
        if (nombreExiste) {
            errores.add("La pregunta \"" + nombre + "\" ya está definida para la competencia "
                    + nombreCompetencia + " y el factor " + factor.getNombre() + ".");
        }
        if (enunciadoExiste) {
            errores.add("Ya existe una pregunta activa con ese enunciado para la competencia "
                    + nombreCompetencia + " y el factor " + factor.getNombre() + ".");
        }

        if (!nombreExiste && !enunciadoExiste) {
            Pregunta pregunta = new Pregunta(nombre, enunciado, descripcion, factor, opcion);
            for (ValorOpcion valor : opcion.getValores()) {
                pregunta.agregarPonderacion(valor, ponderaciones.get(valor));
            }
            preguntaDAO.guardar(pregunta);
        }

        return new ResultadoPreguntaDTO(errores.isEmpty(), errores);
    }

    public ResultadoPreguntaDTO modificarPregunta(Pregunta pregunta, DatosPreguntaDTO datos) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU022)");
    }

    public Pregunta generarPreguntaConIA(Competencia competencia, Factor factor) {
        if (competencia == null || factor == null) {
            throw new IllegalArgumentException("Debe seleccionar una competencia y un factor para generar con IA");
        }
        return servicioIA.generarPregunta(competencia, factor);
    }

    private boolean validarPonderaciones(Map<ValorOpcion, Double> ponderaciones) {
        if (ponderaciones == null || ponderaciones.isEmpty()) {
            return false;
        }
        double suma = 0.0;
        for (Double valor : ponderaciones.values()) {
            if (valor == null || Double.isNaN(valor) || Double.isInfinite(valor) || valor < 0.0) {
                return false;
            }
            suma += valor;
        }
        return Math.abs(suma - SUMA_PONDERACIONES_ESPERADA) < TOLERANCIA_PONDERACION;
    }
}
