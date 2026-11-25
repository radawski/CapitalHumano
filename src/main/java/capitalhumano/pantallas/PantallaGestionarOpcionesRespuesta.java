package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorOpcionesRespuesta;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** {@code <<boundary>>} Gestión de opciones de respuesta. Fuera del alcance de la entrega final. */
@WebServlet(urlPatterns = {"/opciones-respuesta"})
public class PantallaGestionarOpcionesRespuesta extends HttpServlet {

    private GestorOpcionesRespuesta gestorOpcionesRespuesta;

    @Override
    public void init() {
        gestorOpcionesRespuesta = InicializadorAplicacion.obtener(getServletContext(), GestorOpcionesRespuesta.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("titulo", "Gestión de opciones de respuesta");
        request.getRequestDispatcher("/WEB-INF/jsp/gestionar-opciones-respuesta.jsp").forward(request, response);
    }
}
