package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorOrdenMerito;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** {@code <<boundary>>} Orden de mérito. En construcción: CU028 (#5, #6 y #7). */
@WebServlet(urlPatterns = {"/orden-merito", "/orden-merito/*"})
public class PantallaOrdenMerito extends HttpServlet {

    private GestorOrdenMerito gestorOrdenMerito;

    @Override
    public void init() {
        gestorOrdenMerito = InicializadorAplicacion.obtener(getServletContext(), GestorOrdenMerito.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("titulo", "Orden de mérito");
        request.getRequestDispatcher("/WEB-INF/jsp/orden-merito.jsp").forward(request, response);
    }
}
