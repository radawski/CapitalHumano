package capitalhumano.adapters;

import capitalhumano.controladores.GestorCuestionarios;
import capitalhumano.dominio.Cuestionario;
import capitalhumano.dtos.CredencialesLoginDTO;
import capitalhumano.dtos.ResultadoLoginDTO;
import capitalhumano.enumeraciones.NombreEstadoCuestionario;
import capitalhumano.enumeraciones.TipoAcceso;
import capitalhumano.servicios.EstrategiaAutenticacion;

/** Estrategia de CU001 para el candidato: tipo y número de documento más la clave del cuestionario. */
public class AutenticacionCandidatoClave implements EstrategiaAutenticacion {

    private GestorCuestionarios cuestionarios;

    public AutenticacionCandidatoClave(GestorCuestionarios cuestionarios) {
        this.cuestionarios = cuestionarios;
    }

    /**
     * Paso 3 de CU001: la clave tiene que corresponder al documento ingresado y el cuestionario tiene
     * que estar "Activo" o "En proceso"; si no, flujo 3.A.2 (autenticado = false).
     */
    @Override
    public ResultadoLoginDTO autenticar(CredencialesLoginDTO credenciales) {
        Cuestionario cuestionario = cuestionarios.validarClaveCandidato(credenciales);
        if (cuestionario == null || !habilitado(cuestionario.getNombreEstado())) {
            return new ResultadoLoginDTO(false, TipoAcceso.CANDIDATO, null, null, null, null);
        }
        return new ResultadoLoginDTO(true, TipoAcceso.CANDIDATO, null, null,
                cuestionario.getId(), cuestionario.getClave());
    }

    private static boolean habilitado(NombreEstadoCuestionario estado) {
        return estado == NombreEstadoCuestionario.ACTIVO || estado == NombreEstadoCuestionario.EN_PROCESO;
    }
}
