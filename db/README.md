# Base de datos — Capit@l Humano (PostgreSQL 16)

Esquema relacional, datos de prueba base y scripts de recreación para el TP de Diseño de Sistemas 2026 (Grupo 26 — Issue #2).

## Cómo recrear la base de datos

### En Windows (PowerShell)
```powershell
.\db\reset-db.ps1
```
El script busca `psql.exe` automáticamente en el `PATH` o en `C:\Program Files\PostgreSQL\16\bin`, crea la base `capital_humano` si no existe, limpia el esquema `public`, ejecuta `db/schema.sql` y aplica todos los archivos `db/seed/*.sql` en orden alfabético.

Parámetros opcionales: `-DbHost`, `-Port`, `-DbName`, `-User`, `-Password` (o variables `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD`).

### En Linux / macOS / Git Bash
```bash
./db/reset-db.sh
```

### Manualmente desde DBeaver o pgAdmin 4
1. Crear la base vacía `capital_humano`.
2. Ejecutar [`schema.sql`](schema.sql).
3. Ejecutar [`seed/00-base.sql`](seed/00-base.sql) (y cualquier otro `seed/*.sql` en orden numérico).

> **Regla de seeds del equipo:** no editar `00-base.sql` para datos propios de otro issue; agregar un archivo nuevo como `db/seed/30-cu028.sql`. Los scripts de reset lo levantan automáticamente.

---

## Convenciones técnicas para los DAOs (JDBC)

- **`pregunta.cod_pregunta`**: tipo `UUID` (mapea a `Pregunta.id: UUID`).
- **`valor_opcion.id_valor_opcion`**: tipo `UUID` (mapea a `ValorOpcion.id: UUID`), con `UNIQUE (cod_opcion, orden)` y `UNIQUE (cod_opcion, descripcion)`.
- **`configuracion_cuestionario.tiempo_maximo` y `tiempo_maximo_activo`**: tipo `BIGINT` en **segundos** (mapean directo a `java.time.Duration` con `Duration.ofSeconds(rs.getLong(...))` y `duration.getSeconds()`).
  - En el seed: `tiempo_maximo = 7200` (2 horas) y `tiempo_maximo_activo = 604800` (7 días).
- **`cuestionario`**: el índice único parcial `uq_cuestionario_candidato_vigente` garantiza R1 (un candidato tiene a lo sumo un cuestionario en `ACTIVO` o `EN_PROCESO` a la vez).
- **`registro_auditoria.id_consultor`**: FK a `consultor(id_consultor)`. Al registrar con `RegistroAuditoria.getUsuario()` (`nombre_usuario`), resolver `id_consultor` por `WHERE nombre_usuario = ?`.

---

## Escenarios cargados en `seed/00-base.sql`

### Consultores
| `nombre_usuario` | Nombre y apellido | Uso |
|---|---|---|
| `consultor` | Juan Pérez | Usuario fijo de desarrollo para auditoría (#4) y login simulado LDAP (#18) |
| `mgarcia` | María García | Consultor adicional |

### Catálogo: Competencias, Factores y Preguntas
- **Opciones de respuesta:**
  - `OPC01` (`ESCALA CUATRO NIVELES`): `MALO` (0), `REGULAR` (1), `BUENO` (3), `MUY BUENO` (6) → suman 10.
  - `OPC02` (`VERDADERO / FALSO`): `VERDADERO` (10), `FALSO` (0) → suman 10.
- **Competencias y factores:**
  - `COMP01` (`PROGRAMACION JAVA`, `TECNICA`) — **Evaluable**:
    - `FAC01` (`PROGRAMACION ORIENTADA A OBJETOS`): 3 preguntas activas (`...0001` a `...0003`)
    - `FAC02` (`BASES DE DATOS Y SQL`): 3 preguntas activas (`...0004` a `...0006`)
  - `COMP02` (`TRABAJO EN EQUIPO`, `ACTITUDINAL`) — **Evaluable**:
    - `FAC03` (`COMUNICACION EFECTIVA`): 3 preguntas activas (`...0007` a `...0009`)
    - `FAC04` (`RESOLUCION DE CONFLICTOS`): 3 preguntas activas (`...0010` a `...0012`) + **1 inactiva** (`20000000-0000-0000-0000-000000000013`, para verificar que CU020 no la muestra)
  - `COMP03` (`LIDERAZGO ESTRATEGICO`, `ACTITUDINAL`) — **No evaluable**:
    - `FAC05` (`VISION ESTRATEGICA`): solo 1 pregunta activa (`...0014`, < 2 preguntas → dispara flujo **9.A** de CU025)

### Puestos
| Código | Nombre | Empresa | Competencias (mínimo) | Escenario |
|---|---|---|---|---|
| `P001` | `DESARROLLADOR JAVA SENIOR` | `TECH SOLUTIONS SA` | `COMP01` (6.00), `COMP02` (5.00) | Evaluable, con **2 evaluaciones** (`EVAL01` y `EVAL02`) |
| `P002` | `LIDER TECNICO` | `INNOVAR TIC SRL` | `COMP01` (7.00), `COMP03` (6.00) | Competencia no evaluable → **CU025 flujo 9.A** (#11) |
| `P003` | `ANALISTA FUNCIONAL` | `TECH SOLUTIONS SA` | `COMP02` (5.00) | Sin evaluaciones → **CU028 flujo 6.A** (#6) |

### Configuración vigente
- `id_configuracion`: `30000000-0000-0000-0000-000000000001`
- `cantidad_preguntas_por_bloque`: `3`
- `maximo_accesos`: `5`
- `tiempo_maximo`: `7200` s (2 horas)
- `tiempo_maximo_activo`: `604800` s (7 días)

### Cuestionarios y claves de prueba (Puesto `P001`)

#### Evaluación 1 (`40000000-0000-0000-0000-000000000001`)
| Clave | Estado | Candidato (Nro / Doc) | Situación y puntajes | Lo usa |
|---|---|---|---|---|
| `ACTIVO01` | `ACTIVO` | 1001 — `DNI 35111001` (GOMEZ, LUCAS) | Creado hoy, 0 accesos (también sirve para **CU025 flujo 6.A**) | #10, #14 (paso 1), #15, #18 |
| `VENCID01` | `ACTIVO` | 1002 — `DNI 36222002` (FERNANDEZ, SOFIA) | Creado hace 10 días (> 7 días de plazo activo) | #14 (1.B) |
| `MAXACC01` | `ACTIVO` | 1003 — `LE 28333003` (RODRIGUEZ, MARTIN) | 5 accesos (el próximo ingreso supera el máximo) | #14 (precondición) |
| `PROCES01` | `EN_PROCESO` | 1004 — `DNI 37444004` (LOPEZ, VALENTINA) | Iniciado hace 10 min, bloque 1/3 contestado | #14 (1.C), #16 |
| `ULTIMO01` | `EN_PROCESO` | 1005 — `DNI 34555005` (MARTINEZ, NICOLAS) | Bloques 1 y 2 contestados; falta solo el bloque 3 (2 preguntas) | #16, #17 |
| `TARDE001` | `EN_PROCESO` | 1006 — `LC 29666006` (PEREZ, CAMILA) | Iniciado hace 3 hs (> 2 hs de tiempo máximo) | #14 (1.A) |
| `COMPLE01` | `COMPLETO` | 1007 — `DNI 33777007` (SANCHEZ, DIEGO) | `COMP01=6.50`, `COMP02=6.50`, **Total=6.50** (aprobado #2 en Eval 1) | #6 (aprobados) |
| `COMPLE02` | `COMPLETO` | 1008 — `DNI 35888008` (ROMERO, JULIETA) | `COMP01=8.00`, `COMP02=8.00`, **Total=8.00** (aprobado #1) | #6 (orden) |
| `COMPLE03` | `COMPLETO` | 1009 — `PP AAB99009` (TORRES, FEDERICO) | `COMP01=3.00` (< 6.00), `COMP02=8.00`, **Total=5.50** (no aprobado) | #6 (no aprobados) |
| `INCOMP01` | `INCOMPLETO` | 1010 — `DNI 38101010` (DIAZ, AGUSTINA) | 2 accesos, con último ingreso y bloque 1 contestado | #6 (no completos) |
| `SINCON01` | `SIN_CONTESTAR` | 1011 — `DNI 32111011` (ALVAREZ, TOMAS) | 0 accesos, vencido sin iniciar | #6 (no completos) |

#### Evaluación 2 (`40000000-0000-0000-0000-000000000002`) — selección y `TODAS` en CU028
| Clave | Estado | Candidato (Nro / Doc) | Situación y puntajes | Lo usa |
|---|---|---|---|---|
| `EVAL2C01` | `COMPLETO` | 1012 — `DNI 36121012` (RUIZ, FLORENCIA) | `COMP01=7.25`, `COMP02=7.25`, **Total=7.25** (en `TODAS` queda #2 entre `COMPLE02` y `COMPLE01`) | #6 (selección / TODAS) |
| `EVAL2I01` | `INCOMPLETO` | 1013 — `DNI 34131013` (MORENO, MATIAS) | 1 acceso, con fecha de inicio y último ingreso | #6 (selección / TODAS) |

#### Candidato libre (sin cuestionarios previos)
- **1014** — `DNI 39141014` (`CASTRO, PAULA`): habilitada para probar creación de evaluación desde cero en CU025 (#10–#12), junto con los candidatos `1007`–`1013` que ya no tienen cuestionarios vigentes.
