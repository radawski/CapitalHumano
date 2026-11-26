package capitalhumano.adapters;

import capitalhumano.dtos.CredencialesLoginDTO;
import capitalhumano.dtos.ResultadoLoginDTO;
import capitalhumano.enumeraciones.TipoAcceso;
import capitalhumano.servicios.EstrategiaAutenticacion;

/** Estrategia de CU001 para el consultor: valida usuario y contraseña contra el LDAP de la organización. */
public class AutenticacionConsultorLDAP implements EstrategiaAutenticacion {

    private AdaptadorLDAP directorio;

    public AutenticacionConsultorLDAP(AdaptadorLDAP directorio) {
        this.directorio = directorio;
    }

    /**
     * Paso 5 de CU001. El consultor no se busca en la base (no hay {@code ConsultorDAO}), por eso el
     * resultado no lleva {@code consultorId}; la pantalla deja en sesión el usuario de las credenciales.
     */
    @Override
    public ResultadoLoginDTO autenticar(CredencialesLoginDTO credenciales) {
        String usuario = credenciales == null ? null : credenciales.getUsuario();
        String password = credenciales == null ? null : credenciales.getPassword();
        boolean autenticado = usuario != null && password != null
                && directorio.validarCredenciales(usuario.trim(), password);
        return new ResultadoLoginDTO(autenticado, TipoAcceso.CONSULTOR, null,
                autenticado ? usuario.trim() : null, null, null);
    }
}
