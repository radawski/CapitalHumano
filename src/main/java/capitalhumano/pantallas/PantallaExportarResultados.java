package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorExportacionResultados;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** {@code <<boundary>>} Exportar resultados. Fuera del alcance de la entrega final. (CU007) */
@WebServlet(urlPatterns = {"/exportar-resultados"})
public class PantallaExportarResultados extends HttpServlet {

    private GestorExportacionResultados gestorExportacionResultados;

    @Override
    public void init() {
        gestorExportacionResultados = InicializadorAplicacion.obtener(getServletContext(), GestorExportacionResultados.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("titulo", "Exportar resultados");
        request.getRequestDispatcher("/WEB-INF/jsp/exportar-resultados.jsp").forward(request, response);
    }
}
