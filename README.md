# CapitalHumano

La consultora “Capit@l Humano” es una consultora de recursos humanos de vanguardia de la República Argentina, focalizada en el mercado de las TIC. “Capit@l Humano” pone a disposición de las grandes corporaciones las mejores prácticas en procesos y provisión de servicios de Recursos Humanos.

Implementación del **Sistema de Gestión de Evaluaciones de Perfiles**: TP de Diseño de Sistemas 2026, Grupo 26, Etapas 7 y 8. La fuente de verdad es la carpeta `Diseño Definitivo/` (clases, secuencias, DER y despliegue).

Para el detalle completo de instalación, manual de uso por caso de uso, tabla de trazabilidad y checklist de entrega final, ver [docs/documentacion-sistema.md](docs/documentacion-sistema.md).

## Tecnología

La fija el diagrama de despliegue:

| Pieza | Versión |
|---|---|
| Java | 21 (compila con `--release 21`; también corre en JDK más nuevos) |
| Servidor | Tomcat 10.1 (Jakarta Servlet 6 + JSP 3.1), artefacto `capital-humano.war` |
| Base de datos | PostgreSQL 16, con JDBC puro (`java.sql`) |
| Configuración | `src/main/resources/application.properties` |

No se usa Spring ni ningún otro framework. Dentro del `.war` solo van el driver de PostgreSQL, JSTL y Apache POI, cada uno con sus dependencias propias. Maven y JUnit 5 se usan solo para compilar y probar.

## Cómo levantarlo

No hace falta instalar Maven: el repo trae el wrapper (`mvnw` / `mvnw.cmd`).

1. **Base de datos.** Creá la base `capital_humano` en PostgreSQL 16 y corré los scripts de `db/` (ver #2). Si tu usuario o tu clave no son `postgres`/`postgres`, no edites `application.properties`. Creá un archivo aparte, por ejemplo `application-local.properties` (está en `.gitignore`), con las claves que quieras pisar, y pasá su ruta con `-Dcapitalhumano.config=...` o con la variable de entorno `CAPITALHUMANO_CONFIG`.
2. **Compilar y empaquetar.**
   ```
   ./mvnw package          # Windows: mvnw.cmd package
   ```
   Genera `target/capital-humano.war`.
3. **Ejecutar.** Tenés dos opciones:
   - Rápido, para desarrollo: `./mvnw package cargo:run` baja un Tomcat 10.1 de Maven Central y despliega el `.war`.
   - Tomcat propio: copiá `target/capital-humano.war` a `webapps/` de un Tomcat 10.1.

   En los dos casos la app queda en <http://localhost:8080/capital-humano/>.

**Usuario de prueba (LDAP simulado):** `consultor` / `consultor`.

## Cómo está organizado el código

Los paquetes son los del diagrama de clases, debajo de `capitalhumano`:

| Paquete del diagrama | Paquete Java | Qué contiene |
|---|---|---|
| Enumeraciones | `enumeraciones` | Enums |
| Pantallas (Boundary) | `pantallas` | Un `HttpServlet` por `<<boundary>>`, con su JSP en `WEB-INF/jsp/` |
| Servicios (Interfaces) | `servicios` | Interfaces `Servicio*` y `EstrategiaAutenticacion` |
| DTOs | `dtos` | DTOs y reportes |
| Adapters | `adapters` | Adaptadores, estrategias, Builder, Factory y el observador de auditoría |
| Controladores | `controladores` | Los `<<control>>` (`Gestor*`), clases Java comunes |
| Persistencia (DAO) | `persistencia` | Interfaz `<<DAO>>` + `Jdbc<Nombre>DAO` + `ConexionBD` |
| Dominio | `dominio` | Entidades, State y Observer |

Fuera de los paquetes del diagrama solo está `capitalhumano.InicializadorAplicacion`, un `ServletContextListener` que al arrancar arma los DAO, los adaptadores y los gestores y los deja en el `ServletContext`. Cada pantalla obtiene su gestor en `init()`:

```java
gestorOrdenMerito = InicializadorAplicacion.obtener(getServletContext(), GestorOrdenMerito.class);
```

La plantilla base son los fragmentos `WEB-INF/jspf/cabecera.jspf` y `pie.jspf` más `css/estilos.css`. Para usarla, el servlet pone `request.setAttribute("titulo", "...")` y el JSP incluye los dos fragmentos. El usuario de la cabecera se lee de `sessionScope.usuario`, que completa el login (#18).

### Traducción del diagrama

- **Tipos:** `DateTime` → `java.time.LocalDateTime`, `Date` → `java.time.LocalDate`, `Duration` → `java.time.Duration` y `UUID` → `java.util.UUID`.
- **Asociaciones:** se implementan como atributos privados que se llaman igual que el extremo navegable (`Cuestionario.candidato`, `Evaluacion.cuestionarios`, `Puesto.ponderaciones`, etc.). `Puesto` guarda sus competencias en un `Map<Competencia, PonderacionPuesto>` (la clase de asociación), y `Pregunta` hace lo mismo en un `Map<ValorOpcion, PreguntaValorOpcion>`.
- **Constructores de reconstrucción:** las entidades tienen, además de los constructores del diagrama, un constructor público con todos sus atributos, marcado en el Javadoc como *"Constructor de reconstrucción (no figura en el diagrama)"*. Sirve para que los `Jdbc*DAO` armen los objetos que leen de la base sin agregar setters. No se usan para crear objetos nuevos: para eso están los constructores y las operaciones del diagrama.
- **Igualdad:** `equals`/`hashCode` usan el identificador (código o id) en las clases que se usan como clave de un `Map`.

### Quién completa cada método

Cada método con lógica lanza `UnsupportedOperationException("TODO #<issue>")`, con el número de la issue que lo implementa. Para encontrar lo que te toca:

```
git grep -n "TODO #5"
```

Lo que no se implementa en la entrega final lanza `UnsupportedOperationException("Fuera del alcance de la entrega final ...")`. Los getters, setters y constructores sin lógica ya están implementados.

Los adaptadores simulados ya funcionan:
- `AdaptadorLDAP` acepta solo `consultor`/`consultor`.
- `AdaptadorGeneracionIAExterna` devuelve siempre la misma pregunta. La versión real es la #9.

## Flujo de trabajo

GitHub Flow. `main` está protegida, así que todo entra por PR con una aprobación. Se usa una rama por issue (`feature/<n>-<desc>`) y merge con squash. Las firmas del diagrama de clases no se cambian; si hace falta cambiar una, el PR lleva el label `cambio-de-diseño` y actualiza el `.puml` en el mismo PR.
