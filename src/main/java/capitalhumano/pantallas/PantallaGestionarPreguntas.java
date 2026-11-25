package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorPreguntas;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** {@code <<boundary>>} Gestión de preguntas. En construcción: CU020 (#3), CU023 (#4) y CU021 en /preguntas/nueva (#8). */
@WebServlet(urlPatterns = {"/preguntas", "/preguntas/*"})
public class PantallaGestionarPreguntas extends HttpServlet {

    private GestorPreguntas gestorPreguntas;

    @Override
    public void init() {
        gestorPreguntas = InicializadorAplicacion.obtener(getServletContext(), GestorPreguntas.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("titulo", "Gestión de preguntas");
        request.getRequestDispatcher("/WEB-INF/jsp/gestionar-preguntas.jsp").forward(request, response);
    }
}
