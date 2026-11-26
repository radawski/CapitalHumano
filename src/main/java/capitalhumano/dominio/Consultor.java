package capitalhumano.dominio;

import java.util.UUID;

public class Consultor {

    private UUID id;
    private String nombreUsuario;
    private String nombre;
    private String apellido;
    private String email;

    /** Constructor de reconstrucción (no figura en el diagrama). */
    public Consultor(UUID id, String nombreUsuario, String nombre, String apellido, String email) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * Indica si el consultor puede autenticarse: tiene un nombre de usuario no vacío. La validación
     * de la contraseña no es del dominio sino del LDAP ({@code AutenticacionConsultorLDAP}).
     */
    public boolean autenticar() {
        return nombreUsuario != null && !nombreUsuario.isBlank();
    }
}
