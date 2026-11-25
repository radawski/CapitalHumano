package capitalhumano.servicios;

import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Factor;
import capitalhumano.dominio.Pregunta;
import capitalhumano.dominio.Puesto;

public interface ServicioGeneracionIA {

    Pregunta generarPregunta(Competencia competencia, Factor factor);

    Puesto generarPuesto(String descripcion);
}
