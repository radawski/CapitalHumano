package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorCuestionarios;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** {@code <<boundary>>} Completar cuestionario. En construcción: CU026 (#14, #15, #16 y #17). */
@WebServlet(urlPatterns = {"/cuestionario", "/cuestionario/*"})
public class PantallaCompletarCuestionario extends HttpServlet {

    private GestorCuestionarios gestorCuestionarios;

    @Override
    public void init() {
        gestorCuestionarios = InicializadorAplicacion.obtener(getServletContext(), GestorCuestionarios.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("titulo", "Completar cuestionario");
        request.getRequestDispatcher("/WEB-INF/jsp/completar-cuestionario.jsp").forward(request, response);
    }
}
