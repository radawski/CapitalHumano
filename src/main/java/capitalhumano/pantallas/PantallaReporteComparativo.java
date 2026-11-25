package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorReporteComparativo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** {@code <<boundary>>} Reporte comparativo. Fuera del alcance de la entrega final. (CU027) */
@WebServlet(urlPatterns = {"/reporte-comparativo"})
public class PantallaReporteComparativo extends HttpServlet {

    private GestorReporteComparativo gestorReporteComparativo;

    @Override
    public void init() {
        gestorReporteComparativo = InicializadorAplicacion.obtener(getServletContext(), GestorReporteComparativo.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("titulo", "Reporte comparativo");
        request.getRequestDispatcher("/WEB-INF/jsp/reporte-comparativo.jsp").forward(request, response);
    }
}
