package capitalhumano.controladores;

import capitalhumano.dominio.OpcionDeRespuesta;
import capitalhumano.persistencia.PreguntaDAO;
import java.util.List;

public class GestorOpcionesRespuesta {

    private PreguntaDAO preguntaDAO;

    public GestorOpcionesRespuesta(PreguntaDAO preguntaDAO) {
        this.preguntaDAO = preguntaDAO;
    }

    public List<OpcionDeRespuesta> buscarOpciones(String nombre) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    public void darDeAltaOpcion(OpcionDeRespuesta opcion) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    public void modificarOpcion(OpcionDeRespuesta opcion) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }
}
