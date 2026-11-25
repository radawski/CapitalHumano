package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorImportacionCandidatos;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** {@code <<boundary>>} Importar candidatos. Fuera del alcance de la entrega final. (CU006) */
@WebServlet(urlPatterns = {"/importar-candidatos"})
public class PantallaImportarCandidatos extends HttpServlet {

    private GestorImportacionCandidatos gestorImportacionCandidatos;

    @Override
    public void init() {
        gestorImportacionCandidatos = InicializadorAplicacion.obtener(getServletContext(), GestorImportacionCandidatos.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("titulo", "Importar candidatos");
        request.getRequestDispatcher("/WEB-INF/jsp/importar-candidatos.jsp").forward(request, response);
    }
}
