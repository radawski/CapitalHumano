package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorCompetencias;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** {@code <<boundary>>} Gestión de factores. Fuera del alcance de la entrega final. */
@WebServlet(urlPatterns = {"/factores"})
public class PantallaGestionarFactores extends HttpServlet {

    private GestorCompetencias gestorCompetencias;

    @Override
    public void init() {
        gestorCompetencias = InicializadorAplicacion.obtener(getServletContext(), GestorCompetencias.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("titulo", "Gestión de factores");
        request.getRequestDispatcher("/WEB-INF/jsp/gestionar-factores.jsp").forward(request, response);
    }
}
