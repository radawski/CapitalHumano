package capitalhumano.cu028;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/** DAO falsos para los tests de CU028 (mismo esquema que {@code GestorCuestionariosCU026Test}). */
final class Fakes {

    private Fakes() {
    }

    /** Implementa la interfaz con los métodos del mapa; cualquier otro lanza una excepción. */
    @SuppressWarnings("unchecked")
    static <T> T fake(Class<T> tipo, Map<String, Function<Object[], Object>> metodos) {
        Map<String, Function<Object[], Object>> copia = new HashMap<>(metodos);
        return (T) Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[] {tipo}, (proxy, metodo, args) -> {
            Function<Object[], Object> f = copia.get(metodo.getName());
            if (f == null) {
                throw new UnsupportedOperationException(metodo.getName() + " no se usa en este test");
            }
            return f.apply(args);
        });
    }
}
