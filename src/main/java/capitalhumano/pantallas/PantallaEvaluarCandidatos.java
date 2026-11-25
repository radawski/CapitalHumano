package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorEvaluacionCandidatos;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** {@code <<boundary>>} Evaluar candidatos. En construcción: CU025 (#10, #11, #12 y #13). */
@WebServlet(urlPatterns = {"/evaluar-candidatos", "/evaluar-candidatos/*"})
public class PantallaEvaluarCandidatos extends HttpServlet {

    private GestorEvaluacionCandidatos gestorEvaluacionCandidatos;

    @Override
    public void init() {
        gestorEvaluacionCandidatos = InicializadorAplicacion.obtener(getServletContext(), GestorEvaluacionCandidatos.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("titulo", "Evaluar candidatos");
        request.getRequestDispatcher("/WEB-INF/jsp/evaluar-candidatos.jsp").forward(request, response);
    }
}
