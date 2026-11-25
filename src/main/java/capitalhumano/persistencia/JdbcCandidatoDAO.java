package capitalhumano.persistencia;

import capitalhumano.dominio.Candidato;
import capitalhumano.dtos.RegistroImportacionDTO;
import java.util.List;

public class JdbcCandidatoDAO implements CandidatoDAO {

    private final ConexionBD conexionBD;

    public JdbcCandidatoDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public RegistroImportacionDTO importarMasivo(List<Candidato> candidatos) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU006)");
    }

    @Override
    public List<Candidato> buscarPorFiltros(String apellido, String nombre, int nroCandidato) {
        throw new UnsupportedOperationException("TODO #10");
    }

    @Override
    public Candidato buscarPorNroCandidato(int nroCandidato) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU027)");
    }
}
