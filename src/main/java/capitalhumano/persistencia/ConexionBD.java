package capitalhumano.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Infraestructura de JDBC puro (no figura en el diagrama de clases): abre conexiones a PostgreSQL
 * con los datos de {@code application.properties}. La comparten todos los {@code Jdbc*DAO}.
 */
public class ConexionBD {

    private final String url;
    private final String usuario;
    private final String password;

    public ConexionBD(Properties configuracion) {
        this.url = configuracion.getProperty("db.url");
        this.usuario = configuracion.getProperty("db.usuario");
        this.password = configuracion.getProperty("db.password");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("No se encontró el driver JDBC de PostgreSQL", e);
        }
    }

    /** Devuelve una conexión nueva; quien la pide la cierra (try-with-resources). */
    public Connection getConexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, password);
    }
}
