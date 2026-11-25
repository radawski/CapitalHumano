package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorAutenticacion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** {@code <<boundary>>} Ingreso al sistema. En construcción: CU001 (#18). */
@WebServlet(urlPatterns = {"/login"})
public class PantallaAutenticar extends HttpServlet {

    private GestorAutenticacion gestorAutenticacion;

    @Override
    public void init() {
        gestorAutenticacion = InicializadorAplicacion.obtener(getServletContext(), GestorAutenticacion.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("titulo", "Ingreso al sistema");
        request.getRequestDispatcher("/WEB-INF/jsp/autenticar.jsp").forward(request, response);
    }
}
