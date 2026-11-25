package capitalhumano.persistencia;

import capitalhumano.dominio.Candidato;
import capitalhumano.dtos.RegistroImportacionDTO;
import java.util.List;

public interface CandidatoDAO {

    RegistroImportacionDTO importarMasivo(List<Candidato> candidatos);

    List<Candidato> buscarPorFiltros(String apellido, String nombre, int nroCandidato);

    Candidato buscarPorNroCandidato(int nroCandidato);
}
