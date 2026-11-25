package capitalhumano.persistencia;

import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Factor;
import capitalhumano.dominio.OpcionDeRespuesta;
import capitalhumano.dominio.Pregunta;
import java.util.List;

public interface PreguntaDAO {

    void guardar(Pregunta pregunta);

    void actualizar(Pregunta pregunta);

    void eliminarLogico(Pregunta pregunta);

    List<Pregunta> buscar(Competencia competencia, Factor factor, String nombrePregunta);

    boolean existePorNombre(Factor factor, String nombre);

    boolean existePorEnunciado(Factor factor, String enunciado);

    List<Pregunta> obtenerPreguntasActivasPorFactor(Factor factor);

    void guardarOpcionDeRespuesta(OpcionDeRespuesta opcion);

    void actualizarOpcionDeRespuesta(OpcionDeRespuesta opcion);

    void eliminarLogicoOpcionDeRespuesta(OpcionDeRespuesta opcion);

    List<OpcionDeRespuesta> buscarOpcionesDeRespuesta(String nombre);
}
