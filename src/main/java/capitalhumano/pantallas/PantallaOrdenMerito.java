package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorOrdenMerito;
import capitalhumano.dtos.PuestoResumenDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/** {@code <<boundary>>} Orden de mérito (CU028: #5, #6 y #7). */
@WebServlet(urlPatterns = {"/orden-merito", "/orden-merito/*"})
public class PantallaOrdenMerito extends HttpServlet {

    private static final String JSP = "/WEB-INF/jsp/orden-merito.jsp";
    private static final String PREFIJO_SESION = "cu028.";
    private static final String ATTR_SESION_FILTRO_CODIGO = "cu028.filtroCodigo";
    private static final String ATTR_SESION_FILTRO_NOMBRE = "cu028.filtroNombre";
    private static final String ATTR_SESION_FILTRO_EMPRESA = "cu028.filtroEmpresa";
    private static final String ATTR_SESION_PUESTOS = "cu028.puestos";
    private static final String ATTR_SESION_BUSQUEDA_REALIZADA = "cu028.busquedaRealizada";

    private GestorOrdenMerito gestorOrdenMerito;

    @Override
    public void init() {
        gestorOrdenMerito = InicializadorAplicacion.obtener(getServletContext(), GestorOrdenMerito.class);
    }

    /** Pasos 1–2: pantalla de búsqueda (con los filtros y la grilla que hayan quedado en la sesión). */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        mostrarPantallaBusqueda(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        HttpSession session = request.getSession();

        if ("cancelar".equals(accion)) {
            // Flujo 2.A: el CU termina.
            limpiarSesion(session);
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        if ("buscar".equals(accion)) {
            ingresarCriteriosYBuscar(request, response, session);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/orden-merito");
    }

    /** Pasos 3–4: valida que haya al menos un criterio y muestra la grilla de puestos. */
    private void ingresarCriteriosYBuscar(HttpServletRequest request, HttpServletResponse response,
            HttpSession session) throws ServletException, IOException {
        String codigo = normalizarTexto(request.getParameter("codigo"));
        String nombre = normalizarTexto(request.getParameter("nombre"));
        String empresa = normalizarTexto(request.getParameter("empresa"));
        session.setAttribute(ATTR_SESION_FILTRO_CODIGO, codigo);
        session.setAttribute(ATTR_SESION_FILTRO_NOMBRE, nombre);
        session.setAttribute(ATTR_SESION_FILTRO_EMPRESA, empresa);

        if (!validarAlMenosUnCriterio(codigo, nombre, empresa)) {
            session.removeAttribute(ATTR_SESION_PUESTOS);
            session.removeAttribute(ATTR_SESION_BUSQUEDA_REALIZADA);
            request.setAttribute("error", "Ingrese al menos un criterio de búsqueda");
            mostrarPantallaBusqueda(request, response);
            return;
        }

        List<PuestoResumenDTO> puestos = gestorOrdenMerito.buscarPuestos(codigo, nombre, empresa);
        session.setAttribute(ATTR_SESION_PUESTOS, puestos);
        session.setAttribute(ATTR_SESION_BUSQUEDA_REALIZADA, Boolean.TRUE);
        response.sendRedirect(request.getContextPath() + "/orden-merito");
    }

    private void mostrarPantallaBusqueda(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("titulo", "Orden de mérito");
        request.setAttribute("paso", "busqueda");
        request.getRequestDispatcher(JSP).forward(request, response);
    }

    private static boolean validarAlMenosUnCriterio(String codigo, String nombre, String empresa) {
        return !codigo.isEmpty() || !nombre.isEmpty() || !empresa.isEmpty();
    }

    private static void limpiarSesion(HttpSession session) {
        for (String nombre : java.util.Collections.list(session.getAttributeNames())) {
            if (nombre.startsWith(PREFIJO_SESION)) {
                session.removeAttribute(nombre);
            }
        }
    }

    /** Los literales se ingresan siempre en mayúscula (requerimiento especial del CU). */
    private static String normalizarTexto(String valor) {
        return valor == null ? "" : valor.trim().toUpperCase(Locale.ROOT);
    }
}
