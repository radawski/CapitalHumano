package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorCompetencias;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** {@code <<boundary>>} Gestión de competencias. Fuera del alcance de la entrega final. */
@WebServlet(urlPatterns = {"/competencias"})
public class PantallaGestionarCompetencias extends HttpServlet {

    private GestorCompetencias gestorCompetencias;

    @Override
    public void init() {
        gestorCompetencias = InicializadorAplicacion.obtener(getServletContext(), GestorCompetencias.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("titulo", "Gestión de competencias");
        request.getRequestDispatcher("/WEB-INF/jsp/gestionar-competencias.jsp").forward(request, response);
    }
}
