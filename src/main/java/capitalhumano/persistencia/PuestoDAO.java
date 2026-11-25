package capitalhumano.persistencia;

import capitalhumano.dominio.Puesto;
import java.util.List;

public interface PuestoDAO {

    void guardar(Puesto puesto);

    void actualizar(Puesto puesto);

    void eliminarLogico(Puesto puesto);

    List<Puesto> buscarPorFiltros(String codigo, String nombre, String empresa);

    Puesto obtenerConCompetencias(String codigo);
}
