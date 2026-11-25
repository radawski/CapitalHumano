package capitalhumano.persistencia;

import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Factor;
import capitalhumano.dominio.OpcionDeRespuesta;
import capitalhumano.dominio.Pregunta;
import java.util.List;

public class JdbcPreguntaDAO implements PreguntaDAO {

    private final ConexionBD conexionBD;

    public JdbcPreguntaDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public void guardar(Pregunta pregunta) {
        throw new UnsupportedOperationException("TODO #8");
    }

    @Override
    public void actualizar(Pregunta pregunta) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU022)");
    }

    @Override
    public void eliminarLogico(Pregunta pregunta) {
        throw new UnsupportedOperationException("TODO #4");
    }

    @Override
    public List<Pregunta> buscar(Competencia competencia, Factor factor, String nombrePregunta) {
        throw new UnsupportedOperationException("TODO #3");
    }

    @Override
    public boolean existePorNombre(Factor factor, String nombre) {
        throw new UnsupportedOperationException("TODO #8");
    }

    @Override
    public boolean existePorEnunciado(Factor factor, String enunciado) {
        throw new UnsupportedOperationException("TODO #8");
    }

    @Override
    public List<Pregunta> obtenerPreguntasActivasPorFactor(Factor factor) {
        throw new UnsupportedOperationException("TODO #2");
    }

    @Override
    public void guardarOpcionDeRespuesta(OpcionDeRespuesta opcion) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public void actualizarOpcionDeRespuesta(OpcionDeRespuesta opcion) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public void eliminarLogicoOpcionDeRespuesta(OpcionDeRespuesta opcion) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public List<OpcionDeRespuesta> buscarOpcionesDeRespuesta(String nombre) {
        throw new UnsupportedOperationException("TODO #8");
    }
}
