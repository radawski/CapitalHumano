package capitalhumano.persistencia;

import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.PonderacionPuesto;
import capitalhumano.dominio.Puesto;
import capitalhumano.enumeraciones.TipoCompetencia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JdbcPuestoDAO implements PuestoDAO {

    private final ConexionBD conexionBD;

    public JdbcPuestoDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public void guardar(Puesto puesto) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public void actualizar(Puesto puesto) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public void eliminarLogico(Puesto puesto) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public List<Puesto> buscarPorFiltros(String codigo, String nombre, String empresa) {
        StringBuilder sql = new StringBuilder(
                "SELECT cod_puesto, nombre, descripcion, empresa, activo FROM puesto WHERE activo = TRUE");
        List<Object> parametros = new ArrayList<>();

        if (codigo != null && !codigo.isBlank()) {
            sql.append(" AND cod_puesto ILIKE ?");
            parametros.add("%" + codigo.trim() + "%");
        }
        if (nombre != null && !nombre.isBlank()) {
            sql.append(" AND nombre ILIKE ?");
            parametros.add("%" + nombre.trim() + "%");
        }
        if (empresa != null && !empresa.isBlank()) {
            sql.append(" AND empresa ILIKE ?");
            parametros.add("%" + empresa.trim() + "%");
        }
        sql.append(" ORDER BY cod_puesto");

        List<Puesto> puestos = new ArrayList<>();
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    puestos.add(new Puesto(
                            rs.getString("cod_puesto"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getString("empresa"),
                            rs.getBoolean("activo"),
                            Map.of()
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar puestos por filtros", e);
        }
        return puestos;
    }

    @Override
    public Puesto obtenerConCompetencias(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }
        String sqlPuesto = "SELECT cod_puesto, nombre, descripcion, empresa, activo FROM puesto WHERE cod_puesto = ?";
        String sqlCompetencias = """
                SELECT c.cod_competencia, c.nombre, c.descripcion, c.tipo, c.activa, pc.ponderacion_minima
                FROM puesto_competencia pc
                JOIN competencia c ON c.cod_competencia = pc.cod_competencia
                WHERE pc.cod_puesto = ?
                ORDER BY c.cod_competencia
                """;

        try (Connection conn = conexionBD.getConexion();
             PreparedStatement psPuesto = conn.prepareStatement(sqlPuesto)) {
            psPuesto.setString(1, codigo.trim());
            try (ResultSet rsPuesto = psPuesto.executeQuery()) {
                if (!rsPuesto.next()) {
                    return null;
                }
                String codPuesto = rsPuesto.getString("cod_puesto");
                String nombre = rsPuesto.getString("nombre");
                String descripcion = rsPuesto.getString("descripcion");
                String empresa = rsPuesto.getString("empresa");
                boolean activo = rsPuesto.getBoolean("activo");

                Map<Competencia, PonderacionPuesto> ponderaciones = new LinkedHashMap<>();
                try (PreparedStatement psComp = conn.prepareStatement(sqlCompetencias)) {
                    psComp.setString(1, codPuesto);
                    try (ResultSet rsComp = psComp.executeQuery()) {
                        while (rsComp.next()) {
                            Competencia comp = new Competencia(
                                    rsComp.getString("cod_competencia"),
                                    rsComp.getString("nombre"),
                                    rsComp.getString("descripcion"),
                                    TipoCompetencia.valueOf(rsComp.getString("tipo")),
                                    rsComp.getBoolean("activa")
                            );
                            PonderacionPuesto pond = new PonderacionPuesto(rsComp.getDouble("ponderacion_minima"));
                            ponderaciones.put(comp, pond);
                        }
                    }
                }
                return new Puesto(codPuesto, nombre, descripcion, empresa, activo, ponderaciones);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener puesto con competencias: " + codigo, e);
        }
    }
}
