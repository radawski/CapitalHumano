package capitalhumano.persistencia;

import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Factor;
import java.util.List;

public interface CompetenciaDAO {

    void guardar(Competencia competencia);

    void actualizar(Competencia competencia);

    void eliminarLogico(Competencia competencia);

    List<Competencia> buscarPorNombre(String nombre);

    List<Factor> obtenerFactores(Competencia competencia);

    void guardarFactor(Factor factor);

    void actualizarFactor(Factor factor);

    void eliminarLogicoFactor(Factor factor);
}
