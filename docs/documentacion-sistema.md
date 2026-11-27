# Documentación del Sistema - Capit@l Humano

**Sistema de Gestión de Evaluaciones de Perfiles**  
Trabajo Práctico de Diseño de Sistemas 2026 · Grupo 26 (Etapas 7 y 8)

---

## 1. Alcance de la entrega final

La implementación cubre los casos de uso definidos para las Etapas 7 y 8 junto con el acceso al sistema:

* **CU001 - Autenticar usuario** (consultor mediante LDAP simulado y candidato mediante documento + clave, con control de acceso por perfil).
* **CU020 - Gestionar preguntas** (búsqueda filtrada y grilla paginada de preguntas activas).
* **CU023 - Baja lógica de pregunta con auditoría** (patrón Observer sobre bajas de catálogo).
* **CU021 - Dar de alta pregunta** (selección de competencia, factor, opción de respuesta, ponderaciones que suman 10 y generación asistida por IA simulada).
* **CU025 - Evaluar a candidatos** (selección de candidatos, verificación de cuestionarios vigentes, selección de puesto y competencias evaluables, generación de claves en estado `ACTIVO`, persistencia y exportación a `.xls`).
* **CU026 - Completar cuestionario** (validaciones de acceso y plazos, sorteo y armado de bloques, wizard progresivo y cálculo de resultados).
* **CU028 - Emitir orden de mérito** (resumen por puesto, selección de evaluación o `TODAS`, clasificación en bloques e impresión).

Los casos de uso **CU006** (importación de candidatos), **CU007** (exportación de resultados), **CU022** (modificar pregunta), **CU027** (reporte comparativo), **CU032** (finalización automática de cuestionarios vencidos), **CU033 real** (API externa de IA, opcional) y los CRUD de puestos, competencias, factores y opciones de respuesta conservan sus clases y contratos del diagrama de clases en el esqueleto del proyecto, pero quedan fuera del alcance funcional de esta entrega.

---

## 2. Manual de instalación

### 2.1 Requisitos previos

| Componente | Versión requerida / usada |
| :--- | :--- |
| **JDK (Java)** | Java 21 o superior |
| **Base de datos** | PostgreSQL 16 (compatible con 16+) |
| **Servidor de aplicaciones** | Apache Tomcat 10.1 (Jakarta Servlet 6 + JSP 3.1) |
| **Build** | Maven Wrapper (`./mvnw` / `mvnw.cmd`) |

### 2.2 Creación y carga de la base de datos

1. Se debe tener PostgreSQL en ejecución (puerto por defecto `5432`).
2. Ejecutar el script de recreación de la base de datos desde la raíz del repositorio:

   * **Windows (PowerShell):**
     ```powershell
     .\db\reset-db.ps1
     ```
     El script detecta `psql.exe` en el `PATH` o en `C:\Program Files\PostgreSQL\*\bin\psql.exe`, crea la base `capital_humano` si no existe, limpia el esquema `public`, ejecuta `db/schema.sql` y aplica los archivos de `db/seed/` en orden alfabético.

   * **Linux / macOS / Git Bash:**
     ```bash
     ./db/reset-db.sh
     ```

   * **Carga manual (DBeaver / pgAdmin 4):**
     1. Crear la base vacía `capital_humano`.
     2. Ejecutar `db/schema.sql`.
     3. Ejecutar `db/seed/00-base.sql`.

### 2.3 Configuración de la aplicación

La conexión JDBC se configura en `src/main/resources/application.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/capital_humano
db.usuario=postgres
db.password=postgres
```

### 2.4 Compilación, pruebas y generación del `.war`

Desde la raíz del proyecto, ejecutar:

* **Windows:**
  ```powershell
  .\mvnw.cmd clean test package
  ```
* **Linux / macOS:**
  ```bash
  ./mvnw clean test package
  ```

Esto ejecuta los tests unitarios (hechos con JUnit 5) y empaqueta el artefacto desplegable en `target/capital-humano.war`. Las únicas bibliotecas incluidas dentro del `.war` son el driver JDBC de PostgreSQL, JSTL (JavaServer Pages Standard Tag Library) y Apache POI.

### 2.5 Despliegue y arranque

  ```powershell
  .\mvnw.cmd package cargo:run
  ```
Descarga y levanta Tomcat (versión 10.1.34) automáticamente con `capital-humano.war` desplegado.

La aplicación corre en:
`http://localhost:8080/capital-humano/`

### 2.6 Credenciales y datos de prueba cargados (`db/seed/00-base.sql`)

* **Consultor (LDAP simulado):** usuario `consultor`, contraseña `consultor`.
* **Puestos:**
  * `P001` (`DESARROLLADOR JAVA SENIOR`, `TECH SOLUTIONS SA`): evaluable, con 2 evaluaciones precargadas para CU028.
  * `P002` (`LIDER TECNICO`, `INNOVAR TIC SRL`): incluye `COMP03` con una única pregunta activa (para probar flujo 9.A de CU025).
  * `P003` (`ANALISTA FUNCIONAL`, `TECH SOLUTIONS SA`): sin evaluaciones (para probar flujo 6.A de CU028).
* **Candidatos y claves de cuestionarios en el seed (Evaluación 1 de `P001`):**
  * `1001` (`DNI 35111001` · `ACTIVO01`): `ACTIVO`, creado hoy, 0 accesos (CU025 flujo 6.A y CU026 paso 1).
  * `1002` (`DNI 36222002` · `VENCID01`): `ACTIVO` vencido por plazo (CU026 flujo 1.B).
  * `1003` (`LE 28333003` · `MAXACC01`): `ACTIVO` con 5 accesos (CU026 precondición de máximo de accesos).
  * `1004` (`DNI 37444004` · `PROCES01`): `EN_PROCESO`, bloque 1 de 3 contestado (CU026 flujos 1.C y 8.A).
  * `1005` (`DNI 34555005` · `ULTIMO01`): `EN_PROCESO`, falta solo el bloque 3 (CU026 paso 8 y cierre).
  * `1006` (`LC 29666006` · `TARDE001`): `EN_PROCESO` iniciado hace 3 hs (CU026 flujo 1.A).
  * `1007` (`DNI 33777007` · `COMPLE01`), `1008` (`DNI 35888008` · `COMPLE02`), `1009` (`PP AAB99009` · `COMPLE03`): `COMPLETO` (aprobados/no aprobado en CU028) y habilitados para nuevas evaluaciones en CU025.
  * `1010` (`INCOMP01`) y `1011` (`SINCON01`): no completos en CU028.
  * `1014` (`DNI 39141014`, `CASTRO, PAULA`): sin cuestionarios previos, habilitada para crear evaluaciones desde cero en CU025.

---

## 3. Manual de uso por caso de uso

Toda la aplicación está protegida por `FiltroAutenticacion`: al acceder a cualquier ruta sin sesión activa, redirige a `/login`. 
- El perfil **Consultor** accede al menú principal y a los casos de uso de gestión.
- El perfil **Candidato** accede exclusivamente a su cuestionario en `/cuestionario`.

### 3.1 CU001 — Autenticar usuario (`/login`)

1. Al ingresar a la aplicación se muestran dos tarjetas de acceso: **Ingresar como usuario consultor** y **Realizar cuestionario**, además del botón **Salir** (cierra la sesión y finaliza el ingreso).
2. **Ingreso como consultor:**
   * Ingresar **Usuario** y **Contraseña**.
   * Presionar **Ingresar**. Si las credenciales son válidas, redirige al **Menú principal**. Si no, muestra el mensaje de error sin mantener la sesión.
3. **Ingreso como candidato:**
   * Seleccionar **Tipo de documento** (`DNI`, `LE`, `LC`, `PP`), ingresar **Número de documento** y **Clave de ingreso** (8 caracteres alfanuméricos).
   * Presionar **Ingresar**. Si la clave pertenece al documento y el cuestionario está en estado `ACTIVO` o `EN_PROCESO`, guarda los datos en sesión y redirige a `/cuestionario`.

### 3.2 CU020 — Gestionar preguntas (`/preguntas`)

1. Desde el **Menú principal**, elegir **Gestionar preguntas**.
2. Seleccionar al menos un criterio de búsqueda entre **Competencia**, **Factor** (se actualiza dinámicamente al elegir una competencia) y **Nombre de pregunta** (convertido automáticamente a mayúsculas).
3. Presionar **Buscar** para visualizar la grilla paginada (5 preguntas activas por página). Si no se ingresa ningún criterio, el sistema solicita al menos uno.
4. En cada fila se muestran competencia, factor, nombre, enunciado y acciones: **Modificar** permanece deshabilitado (CU022 fuera del alcance) y **Eliminar** inicia el flujo de baja lógica (**CU023**). El botón **Nuevo** redirige al alta (**CU021**).

### 3.3 CU023 — Baja de pregunta con auditoría (`/preguntas/eliminar`)

1. Desde la grilla de búsqueda de preguntas, presionar **Eliminar** en la fila deseada.
2. El sistema solicita confirmación (**Eliminar** / **Cancelar**) mostrando los datos de la pregunta.
3. Al confirmar, se realiza la baja lógica (`activa = false`) y se notifica al observador de auditoría (`RegistradorAuditoria`), registrando en `registro_auditoria` la operación, fecha/hora y el consultor autenticado en sesión. Luego actualiza la grilla mostrando el mensaje de confirmación.

### 3.4 CU021 — Dar de alta pregunta (`/preguntas/nueva`)

1. Ingresar mediante el botón **Nuevo** en Gestión de preguntas.
2. Seleccionar **Competencia** y **Factor**.
3. Completar **Nombre de Pregunta** (máx. 100 caracteres), **Pregunta** (máx. 500) y opcionalmente **Descripción** (máx. 500). Los textos se normalizan a mayúsculas.
4. Seleccionar una **Opción de Respuesta** para cargar su grilla de valores (orden y descripción de solo lectura) e ingresar la **Ponderación** numérica de cada valor.
5. Presionar **Guardar** (o **Cancelar** para volver a `/preguntas`):
   * El sistema valida campos obligatorios, longitudes máximas, que las ponderaciones sean `>= 0` y sumen `10` puntos, y que no exista otra pregunta activa con el mismo nombre o enunciado dentro del mismo factor.
   * Al guardarse con éxito, pregunta si se desea cargar otra pregunta.

### 3.5 CU025 — Evaluar a candidatos (`/evaluar-candidatos`)

1. Desde el **Menú principal**, elegir **Evaluar a candidatos**.
2. **Búsqueda y selección de candidatos (`/evaluar-candidatos`):**
   * Filtrar por **Apellido**, **Nombre** y/o **Nº de candidato** y presionar **Buscar**.
   * Marcar candidatos en *Resultados de búsqueda* y presionar **Agregar** (si no se marcó ninguno muestra aviso). Para quitar de *Candidatos a evaluar*, marcarlos y presionar **Quitar**.
   * Presionar **Siguiente**. Si algún candidato ya posee un cuestionario `ACTIVO` o `EN_PROCESO`, se abre un modal informando los candidatos no habilitados para quitarlos antes de continuar.
3. **Selección de puesto (`/evaluar-candidatos/puesto`):**
   * Elegir un puesto de la lista (función y empresa) y presionar **Siguiente** (si no se elige ninguno, muestra error).
4. **Competencias evaluables (`/evaluar-candidatos/competencias`):**
   * El sistema verifica que cada competencia requerida por el puesto tenga al menos un factor con `>= 2` preguntas activas. Si alguna no cumple, informa las competencias no evaluables y permite volver al menú principal con **Aceptar**.
   * Si todas son evaluables, muestra la grilla informativa de competencias y ponderaciones mínimas. Presionar **Siguiente**.
5. **Generación de claves y persistencia (`/evaluar-candidatos/claves`):**
   * Se muestran los candidatos seleccionados con sus claves alfanuméricas de 8 caracteres generadas en estado `ACTIVO`.
   * **Cancelar** descarta la evaluación sin guardarla en base de datos. **Finalizar** persiste la evaluación y sus cuestionarios en una transacción.
6. **Exportación a Excel (`/evaluar-candidatos/exportar`):**
   * El sistema ofrece exportar las claves a Excel: **No exportar (Finalizar)** vuelve al menú principal; **Exportar a Excel (.xls)** pasa a la confirmación de descarga (**Cancelar** retorna al paso anterior; **Guardar .xls** descarga el archivo `aaaa-mm-dd-hh-mm-NombreFunción.xls`).

### 3.6 CU026 — Completar cuestionario (`/cuestionario`)

1. Al iniciar sesión como candidato desde `/login`, el sistema registra el acceso y valida las reglas de ingreso:
   * Si superó el máximo de accesos, pasa a `INCOMPLETO` y rechaza el ingreso.
   * Si está `ACTIVO` y venció el plazo activo (7 días en el seed), pasa a `SIN_CONTESTAR` y rechaza.
   * Si está `EN_PROCESO` y venció el tiempo máximo desde su inicio (2 horas en el seed), pasa a `INCOMPLETO` y rechaza.
   * Si está `EN_PROCESO` dentro del tiempo, redirige automáticamente al bloque pendiente.
2. **Instrucciones:** si el cuestionario está `ACTIVO` y vigente, muestra las instrucciones de configuración. **Cancelar** mantiene el cuestionario `ACTIVO`; **Aceptar** sortea 2 preguntas por cada factor con `>= 2` preguntas activas, las mezcla en bloques según la configuración, pasa el estado a `EN_PROCESO` y abre el bloque 1.
3. **Pantallas por bloques (`/cuestionario/bloque`):** muestra las preguntas del bloque actual con sus opciones de respuesta. Es obligatorio responder todas las preguntas del bloque antes de avanzar. Cada bloque confirmado se persiste y avanza al siguiente.
4. **Cierre:** al confirmar el último bloque (**Finalizar**), el sistema calcula los puntajes por factor y por competencia, guarda los resultados, cambia el cuestionario a estado `COMPLETO` y muestra la confirmación **Cuestionario enviado**.

### 3.7 CU028 — Emitir orden de mérito (`/orden-merito`)

1. Desde el **Menú principal**, elegir **Emitir orden de mérito**.
2. Ingresar al menos un filtro entre **Código**, **Nombre del puesto** o **Empresa** y presionar **Buscar** (en mayúsculas). **Cancelar** finaliza y vuelve al menú.
3. En la grilla de puestos encontrados (con cantidad de candidatos y evaluaciones completas), presionar **Emitir orden** en la fila deseada.
   * Si el puesto no posee evaluaciones, informa el mensaje correspondiente.
   * Si posee evaluaciones, abre un modal para seleccionar una evaluación específica o **TODAS** y presionar **Emitir**.
4. En `/orden-merito/listado` se muestran:
   * Bloque 1: **Candidatos aprobados** (alcanzan todas las ponderaciones mínimas del puesto), ordenados por puntaje con su posición.
   * Bloque 2: **Candidatos no aprobados** (cuestionario completo que no alcanza algún mínimo).
   * Bloque 3: **Candidatos no completos**, agrupados por estado (`ACTIVO`, `EN_PROCESO`, `INCOMPLETO`, `SIN_CONTESTAR`), sin puntaje.
   * Tabla de **Totales** al pie y datos adicionales por candidato.
5. Presionar **Imprimir** para abrir la interfaz de impresión del navegador o **Volver** para regresar a la búsqueda.

---
