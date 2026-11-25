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

    public boolean autenticar() {
        throw new UnsupportedOperationException("TODO #18");
    }
}
