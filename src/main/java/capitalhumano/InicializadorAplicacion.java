package capitalhumano;

import capitalhumano.adapters.AdaptadorExportacionClavesExcel;
import capitalhumano.adapters.AdaptadorExportacionResultadosCSV;
import capitalhumano.adapters.AdaptadorGeneracionIAExterna;
import capitalhumano.adapters.AdaptadorImportacionCSV;
import capitalhumano.adapters.AdaptadorLDAP;
import capitalhumano.adapters.FabricaCuestionarios;
import capitalhumano.adapters.RegistradorAuditoria;
import capitalhumano.controladores.GestorAutenticacion;
import capitalhumano.controladores.GestorBajasCatalogos;
import capitalhumano.controladores.GestorCompetencias;
import capitalhumano.controladores.GestorCuestionarios;
import capitalhumano.controladores.GestorEvaluacionCandidatos;
import capitalhumano.controladores.GestorExportacionResultados;
import capitalhumano.controladores.GestorFinalizacionCuestionarios;
import capitalhumano.controladores.GestorImportacionCandidatos;
import capitalhumano.controladores.GestorOpcionesRespuesta;
import capitalhumano.controladores.GestorOrdenMerito;
import capitalhumano.controladores.GestorPreguntas;
import capitalhumano.controladores.GestorPuestos;
import capitalhumano.controladores.GestorReporteComparativo;
import capitalhumano.persistencia.AuditoriaDAO;
import capitalhumano.persistencia.CandidatoDAO;
import capitalhumano.persistencia.CompetenciaDAO;
import capitalhumano.persistencia.ConexionBD;
import capitalhumano.persistencia.ConfiguracionDAO;
import capitalhumano.persistencia.EvaluacionDAO;
import capitalhumano.persistencia.JdbcAuditoriaDAO;
import capitalhumano.persistencia.JdbcCandidatoDAO;
import capitalhumano.persistencia.JdbcCompetenciaDAO;
import capitalhumano.persistencia.JdbcConfiguracionDAO;
import capitalhumano.persistencia.JdbcEvaluacionDAO;
import capitalhumano.persistencia.JdbcPreguntaDAO;
import capitalhumano.persistencia.JdbcPuestoDAO;
import capitalhumano.persistencia.PreguntaDAO;
import capitalhumano.persistencia.PuestoDAO;
import capitalhumano.servicios.ServicioGeneracionIA;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Infraestructura (no figura en el diagrama de clases): al arrancar la aplicación arma los DAO,
 * los adaptadores y los gestores, y los deja en el {@link ServletContext}. Cada pantalla obtiene
 * su gestor con {@link #obtener(ServletContext, Class)}.
 * <p>
 * La configuración sale de {@code application.properties} del classpath. Se puede reemplazar por
 * un archivo externo con la propiedad de sistema {@code capitalhumano.config} o la variable de
 * entorno {@code CAPITALHUMANO_CONFIG}.
 */
@WebListener
public class InicializadorAplicacion implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent evento) {
        ServletContext contexto = evento.getServletContext();
        Properties configuracion = cargarConfiguracion();
        ConexionBD conexionBD = new ConexionBD(configuracion);

        // DAO
        CandidatoDAO candidatoDAO = new JdbcCandidatoDAO(conexionBD);
        PuestoDAO puestoDAO = new JdbcPuestoDAO(conexionBD);
        CompetenciaDAO competenciaDAO = new JdbcCompetenciaDAO(conexionBD);
        PreguntaDAO preguntaDAO = new JdbcPreguntaDAO(conexionBD);
        EvaluacionDAO evaluacionDAO = new JdbcEvaluacionDAO(conexionBD);
        ConfiguracionDAO configuracionDAO = new JdbcConfiguracionDAO(conexionBD);
        AuditoriaDAO auditoriaDAO = new JdbcAuditoriaDAO(conexionBD);

        // Adaptadores
        ServicioGeneracionIA servicioIA = new AdaptadorGeneracionIAExterna();
        RegistradorAuditoria registradorAuditoria = new RegistradorAuditoria(auditoriaDAO);

        // Gestores
        GestorCuestionarios gestorCuestionarios = new GestorCuestionarios(evaluacionDAO, competenciaDAO, preguntaDAO);
        registrar(contexto, GestorCuestionarios.class, gestorCuestionarios);
        registrar(contexto, GestorAutenticacion.class, new GestorAutenticacion());
        registrar(contexto, AdaptadorLDAP.class, new AdaptadorLDAP());
        registrar(contexto, GestorImportacionCandidatos.class,
                new GestorImportacionCandidatos(new AdaptadorImportacionCSV(), candidatoDAO));
        registrar(contexto, GestorExportacionResultados.class,
                new GestorExportacionResultados(puestoDAO, evaluacionDAO, new AdaptadorExportacionResultadosCSV()));
        registrar(contexto, GestorPuestos.class, new GestorPuestos(puestoDAO, competenciaDAO, servicioIA));
        registrar(contexto, GestorCompetencias.class, new GestorCompetencias(competenciaDAO));
        registrar(contexto, GestorOpcionesRespuesta.class, new GestorOpcionesRespuesta(preguntaDAO));
        registrar(contexto, GestorEvaluacionCandidatos.class,
                new GestorEvaluacionCandidatos(candidatoDAO, puestoDAO, evaluacionDAO, configuracionDAO,
                        competenciaDAO, preguntaDAO, new AdaptadorExportacionClavesExcel(), new FabricaCuestionarios()));
        registrar(contexto, GestorPreguntas.class, new GestorPreguntas(competenciaDAO, preguntaDAO, servicioIA));
        registrar(contexto, GestorReporteComparativo.class, new GestorReporteComparativo(evaluacionDAO));
        registrar(contexto, GestorOrdenMerito.class, new GestorOrdenMerito(puestoDAO, evaluacionDAO));
        registrar(contexto, GestorFinalizacionCuestionarios.class,
                new GestorFinalizacionCuestionarios(auditoriaDAO, gestorCuestionarios));
        registrar(contexto, GestorBajasCatalogos.class,
                new GestorBajasCatalogos(registradorAuditoria, puestoDAO, competenciaDAO, preguntaDAO));

        contexto.log("Capit@l Humano: gestores, DAO y adaptadores listos");
    }

    /** Devuelve el objeto que el inicializador dejó en el contexto para esa clase. */
    public static <T> T obtener(ServletContext contexto, Class<T> clase) {
        Object objeto = contexto.getAttribute(clase.getName());
        if (objeto == null) {
            throw new IllegalStateException("No se inicializó " + clase.getSimpleName());
        }
        return clase.cast(objeto);
    }

    private static <T> void registrar(ServletContext contexto, Class<T> clase, T objeto) {
        contexto.setAttribute(clase.getName(), objeto);
    }

    private static Properties cargarConfiguracion() {
        Properties propiedades = new Properties();
        try (InputStream entrada = InicializadorAplicacion.class.getResourceAsStream("/application.properties")) {
            if (entrada == null) {
                throw new IllegalStateException("Falta application.properties en el classpath");
            }
            propiedades.load(entrada);
            String externo = System.getProperty("capitalhumano.config", System.getenv("CAPITALHUMANO_CONFIG"));
            if (externo != null && !externo.isBlank()) {
                try (InputStream archivo = Files.newInputStream(Path.of(externo))) {
                    propiedades.load(archivo);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer la configuración", e);
        }
        return propiedades;
    }
}
