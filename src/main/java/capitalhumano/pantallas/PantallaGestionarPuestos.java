package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorPuestos;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** {@code <<boundary>>} Gestión de puestos. Fuera del alcance de la entrega final. */
@WebServlet(urlPatterns = {"/puestos"})
public class PantallaGestionarPuestos extends HttpServlet {

    private GestorPuestos gestorPuestos;

    @Override
    public void init() {
        gestorPuestos = InicializadorAplicacion.obtener(getServletContext(), GestorPuestos.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("titulo", "Gestión de puestos");
        request.getRequestDispatcher("/WEB-INF/jsp/gestionar-puestos.jsp").forward(request, response);
    }
}
