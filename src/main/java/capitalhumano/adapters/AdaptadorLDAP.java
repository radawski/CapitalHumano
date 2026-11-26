package capitalhumano.adapters;

/**
 * Adaptador del servidor LDAP de la organización.
 * <p>
 * <b>Versión simulada</b> (#1): no hay LDAP en el entorno de la cátedra, así que acepta un único
 * consultor de prueba, {@value #USUARIO_PRUEBA} / {@value #PASSWORD_PRUEBA}. CU001 pide el usuario
 * siempre en mayúscula, así que el usuario se compara sin distinguir mayúsculas (la contraseña sí).
 */
public class AdaptadorLDAP {

    static final String USUARIO_PRUEBA = "consultor";
    static final String PASSWORD_PRUEBA = "consultor";

    public boolean validarCredenciales(String usuario, String password) {
        return USUARIO_PRUEBA.equalsIgnoreCase(usuario) && PASSWORD_PRUEBA.equals(password);
    }
}
