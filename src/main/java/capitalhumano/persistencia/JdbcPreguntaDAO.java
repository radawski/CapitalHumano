package capitalhumano.persistencia;

import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Factor;
import capitalhumano.dominio.OpcionDeRespuesta;
import capitalhumano.dominio.Pregunta;
import capitalhumano.dominio.PreguntaValorOpcion;
import capitalhumano.dominio.ValorOpcion;
import capitalhumano.enumeraciones.TipoCompetencia;
import capitalhumano.enumeraciones.TipoPregunta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JdbcPreguntaDAO implements PreguntaDAO {

    private final ConexionBD conexionBD;

    public JdbcPreguntaDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public void guardar(Pregunta pregunta) {
        if (pregunta == null) {
            throw new IllegalArgumentException("La pregunta no puede ser nula");
        }
        if (pregunta.getFactor() == null || pregunta.getFactor().getCodigo() == null) {
            throw new IllegalArgumentException("La pregunta debe pertenecer a un factor");
        }
        if (pregunta.getOpcion() == null || pregunta.getOpcion().getCodigo() == null) {
            throw new IllegalArgumentException("La pregunta debe tener una opción de respuesta");
        }

        String sqlPregunta = """
                INSERT INTO pregunta (
                    cod_pregunta, cod_factor, cod_opcion, nombre, enunciado, descripcion, tipo_pregunta, activa
                ) VALUES (?, ?, ?, ?, ?, ?, ?, TRUE)
                """;
        String sqlPonderacion = """
                INSERT INTO pregunta_valor_opcion (cod_pregunta, id_valor_opcion, ponderacion)
                VALUES (?, ?, ?)
                """;

        try (Connection conn = conexionBD.getConexion()) {
            boolean autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psPreg = conn.prepareStatement(sqlPregunta)) {
                    psPreg.setObject(1, pregunta.getId());
                    psPreg.setString(2, pregunta.getFactor().getCodigo());
                    psPreg.setString(3, pregunta.getOpcion().getCodigo());
                    psPreg.setString(4, pregunta.getNombre());
                    psPreg.setString(5, pregunta.getEnunciado());
                    psPreg.setString(6, pregunta.getDescripcion() != null ? pregunta.getDescripcion() : "");
                    TipoPregunta tipo = pregunta.getTipoPregunta() != null
                            ? pregunta.getTipoPregunta()
                            : TipoPregunta.SINGLE_CHOICE;
                    psPreg.setString(7, tipo.name());
                    psPreg.executeUpdate();
                }

                try (PreparedStatement psPond = conn.prepareStatement(sqlPonderacion)) {
                    for (Map.Entry<ValorOpcion, PreguntaValorOpcion> entry : pregunta.getPonderaciones().entrySet()) {
                        psPond.setObject(1, pregunta.getId());
                        psPond.setObject(2, entry.getKey().getId());
                        psPond.setDouble(3, entry.getValue().getPonderacion());
                        psPond.addBatch();
                    }
                    psPond.executeBatch();
                }

                conn.commit();
            } catch (SQLException | RuntimeException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    e.addSuppressed(rollbackEx);
                }
                throw e;
            } finally {
                conn.setAutoCommit(autoCommitOriginal);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la pregunta en la base de datos", e);
        }
    }

    @Override
    public void actualizar(Pregunta pregunta) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU022)");
    }

    @Override
    public void eliminarLogico(Pregunta pregunta) {
        if (pregunta == null || pregunta.getId() == null) {
            throw new IllegalArgumentException("La pregunta a eliminar no puede ser nula");
        }
        String sql = "UPDATE pregunta SET activa = FALSE WHERE cod_pregunta = ? AND activa = TRUE";
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, pregunta.getId());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new IllegalStateException(
                        "La pregunta no existe o ya se encuentra inactiva: " + pregunta.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar lógicamente la pregunta: " + pregunta.getId(), e);
        }
    }

    @Override
    public List<Pregunta> buscar(Competencia competencia, Factor factor, String nombrePregunta) {
        StringBuilder sql = new StringBuilder("""
                SELECT p.cod_pregunta, p.nombre AS preg_nombre, p.enunciado, p.descripcion AS preg_descripcion,
                       p.tipo_pregunta, p.activa AS preg_activa,
                       f.cod_factor, f.nombre AS fac_nombre, f.descripcion AS fac_descripcion,
                       f.nro_orden, f.activo AS fac_activo,
                       c.cod_competencia, c.nombre AS comp_nombre, c.descripcion AS comp_descripcion,
                       c.tipo AS comp_tipo, c.activa AS comp_activa,
                       o.cod_opcion, o.nombre AS op_nombre, o.descripcion AS op_descripcion, o.activa AS op_activa
                FROM pregunta p
                JOIN factor f ON f.cod_factor = p.cod_factor
                JOIN competencia c ON c.cod_competencia = f.cod_competencia
                JOIN opcion_de_respuesta o ON o.cod_opcion = p.cod_opcion
                WHERE p.activa = TRUE
                """);
        List<Object> parametros = new ArrayList<>();

        if (competencia != null && competencia.getCodigo() != null && !competencia.getCodigo().isBlank()) {
            sql.append(" AND c.cod_competencia = ?");
            parametros.add(competencia.getCodigo());
        }
        if (factor != null && factor.getCodigo() != null && !factor.getCodigo().isBlank()) {
            sql.append(" AND f.cod_factor = ?");
            parametros.add(factor.getCodigo());
        }
        if (nombrePregunta != null && !nombrePregunta.isBlank()) {
            sql.append(" AND p.nombre ILIKE ?");
            parametros.add("%" + nombrePregunta.trim() + "%");
        }
        sql.append(" ORDER BY c.nombre, f.nro_orden, p.nombre");

        List<Pregunta> preguntas = new ArrayList<>();
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Competencia comp = new Competencia(
                            rs.getString("cod_competencia"),
                            rs.getString("comp_nombre"),
                            rs.getString("comp_descripcion"),
                            TipoCompetencia.valueOf(rs.getString("comp_tipo")),
                            rs.getBoolean("comp_activa")
                    );
                    Factor fac = new Factor(
                            rs.getString("cod_factor"),
                            rs.getString("fac_nombre"),
                            rs.getString("fac_descripcion"),
                            rs.getInt("nro_orden"),
                            rs.getBoolean("fac_activo"),
                            comp
                    );
                    OpcionDeRespuesta opcion = new OpcionDeRespuesta(
                            rs.getString("cod_opcion"),
                            rs.getString("op_nombre"),
                            rs.getString("op_descripcion"),
                            rs.getBoolean("op_activa"),
                            List.of()
                    );
                    preguntas.add(new Pregunta(
                            rs.getObject("cod_pregunta", UUID.class),
                            rs.getString("preg_nombre"),
                            rs.getString("enunciado"),
                            rs.getString("preg_descripcion"),
                            TipoPregunta.valueOf(rs.getString("tipo_pregunta")),
                            rs.getBoolean("preg_activa"),
                            fac,
                            opcion,
                            Map.of()
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar preguntas", e);
        }
        return preguntas;
    }

    @Override
    public boolean existePorNombre(Factor factor, String nombre) {
        if (factor == null || factor.getCodigo() == null || nombre == null || nombre.isBlank()) {
            return false;
        }
        String sql = "SELECT 1 FROM pregunta WHERE cod_factor = ? AND UPPER(nombre) = UPPER(?) AND activa = TRUE LIMIT 1";
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, factor.getCodigo());
            ps.setString(2, nombre.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia de pregunta por nombre", e);
        }
    }

    @Override
    public boolean existePorEnunciado(Factor factor, String enunciado) {
        if (factor == null || factor.getCodigo() == null || enunciado == null || enunciado.isBlank()) {
            return false;
        }
        String sql = "SELECT 1 FROM pregunta WHERE cod_factor = ? AND UPPER(enunciado) = UPPER(?) AND activa = TRUE LIMIT 1";
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, factor.getCodigo());
            ps.setString(2, enunciado.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia de pregunta por enunciado", e);
        }
    }

    @Override
    public List<Pregunta> obtenerPreguntasActivasPorFactor(Factor factor) {
        if (factor == null) {
            return List.of();
        }
        String sqlPreguntas = """
                SELECT p.cod_pregunta, p.nombre, p.enunciado, p.descripcion, p.tipo_pregunta, p.activa,
                       o.cod_opcion, o.nombre AS op_nombre, o.descripcion AS op_descripcion, o.activa AS op_activa
                FROM pregunta p
                JOIN opcion_de_respuesta o ON o.cod_opcion = p.cod_opcion
                WHERE p.cod_factor = ? AND p.activa = TRUE
                ORDER BY p.nombre
                """;
        String sqlValores = """
                SELECT id_valor_opcion, orden, descripcion, activo
                FROM valor_opcion
                WHERE cod_opcion = ? AND activo = TRUE
                ORDER BY orden
                """;
        String sqlPonderaciones = """
                SELECT v.id_valor_opcion, v.orden, v.descripcion, v.activo, pvo.ponderacion
                FROM pregunta_valor_opcion pvo
                JOIN valor_opcion v ON v.id_valor_opcion = pvo.id_valor_opcion
                WHERE pvo.cod_pregunta = ?
                ORDER BY v.orden
                """;

        List<Pregunta> preguntas = new ArrayList<>();
        Map<String, OpcionDeRespuesta> opcionesCache = new HashMap<>();

        try (Connection conn = conexionBD.getConexion();
             PreparedStatement psPreg = conn.prepareStatement(sqlPreguntas);
             PreparedStatement psVal = conn.prepareStatement(sqlValores);
             PreparedStatement psPond = conn.prepareStatement(sqlPonderaciones)) {
            psPreg.setString(1, factor.getCodigo());
            try (ResultSet rsPreg = psPreg.executeQuery()) {
                while (rsPreg.next()) {
                    UUID codPregunta = rsPreg.getObject("cod_pregunta", UUID.class);
                    String codOpcion = rsPreg.getString("cod_opcion");

                    OpcionDeRespuesta opcion = opcionesCache.get(codOpcion);
                    if (opcion == null) {
                        List<ValorOpcion> valores = new ArrayList<>();
                        psVal.setString(1, codOpcion);
                        try (ResultSet rsVal = psVal.executeQuery()) {
                            while (rsVal.next()) {
                                valores.add(new ValorOpcion(
                                        rsVal.getObject("id_valor_opcion", UUID.class),
                                        rsVal.getInt("orden"),
                                        rsVal.getString("descripcion"),
                                        rsVal.getBoolean("activo")
                                ));
                            }
                        }
                        opcion = new OpcionDeRespuesta(
                                codOpcion,
                                rsPreg.getString("op_nombre"),
                                rsPreg.getString("op_descripcion"),
                                rsPreg.getBoolean("op_activa"),
                                valores
                        );
                        opcionesCache.put(codOpcion, opcion);
                    }

                    Map<ValorOpcion, Double> ponderaciones = new LinkedHashMap<>();
                    psPond.setObject(1, codPregunta);
                    try (ResultSet rsPond = psPond.executeQuery()) {
                        while (rsPond.next()) {
                            ValorOpcion valor = new ValorOpcion(
                                    rsPond.getObject("id_valor_opcion", UUID.class),
                                    rsPond.getInt("orden"),
                                    rsPond.getString("descripcion"),
                                    rsPond.getBoolean("activo")
                            );
                            ponderaciones.put(valor, rsPond.getDouble("ponderacion"));
                        }
                    }

                    preguntas.add(new Pregunta(
                            codPregunta,
                            rsPreg.getString("nombre"),
                            rsPreg.getString("enunciado"),
                            rsPreg.getString("descripcion"),
                            TipoPregunta.valueOf(rsPreg.getString("tipo_pregunta")),
                            rsPreg.getBoolean("activa"),
                            factor,
                            opcion,
                            ponderaciones
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener preguntas activas del factor: " + factor.getCodigo(), e);
        }
        return preguntas;
    }

    @Override
    public void guardarOpcionDeRespuesta(OpcionDeRespuesta opcion) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public void actualizarOpcionDeRespuesta(OpcionDeRespuesta opcion) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public void eliminarLogicoOpcionDeRespuesta(OpcionDeRespuesta opcion) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public List<OpcionDeRespuesta> buscarOpcionesDeRespuesta(String nombre) {
        StringBuilder sqlOpciones = new StringBuilder(
                "SELECT cod_opcion, nombre, descripcion, activa FROM opcion_de_respuesta WHERE activa = TRUE");
        boolean tieneNombre = nombre != null && !nombre.isBlank();
        if (tieneNombre) {
            sqlOpciones.append(" AND nombre ILIKE ?");
        }
        sqlOpciones.append(" ORDER BY nombre");

        String sqlValores = """
                SELECT id_valor_opcion, orden, descripcion, activo
                FROM valor_opcion
                WHERE cod_opcion = ? AND activo = TRUE
                ORDER BY orden
                """;

        List<OpcionDeRespuesta> opciones = new ArrayList<>();
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement psOp = conn.prepareStatement(sqlOpciones.toString());
             PreparedStatement psVal = conn.prepareStatement(sqlValores)) {
            if (tieneNombre) {
                psOp.setString(1, "%" + nombre.trim() + "%");
            }
            try (ResultSet rsOp = psOp.executeQuery()) {
                while (rsOp.next()) {
                    String codOpcion = rsOp.getString("cod_opcion");
                    List<ValorOpcion> valores = new ArrayList<>();
                    psVal.setString(1, codOpcion);
                    try (ResultSet rsVal = psVal.executeQuery()) {
                        while (rsVal.next()) {
                            valores.add(new ValorOpcion(
                                    rsVal.getObject("id_valor_opcion", UUID.class),
                                    rsVal.getInt("orden"),
                                    rsVal.getString("descripcion"),
                                    rsVal.getBoolean("activo")
                            ));
                        }
                    }
                    opciones.add(new OpcionDeRespuesta(
                            codOpcion,
                            rsOp.getString("nombre"),
                            rsOp.getString("descripcion"),
                            rsOp.getBoolean("activa"),
                            valores
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar opciones de respuesta", e);
        }
        return opciones;
    }
}
