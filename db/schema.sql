-- ============================================================================
-- TP Diseño de Sistemas 2026 - Grupo 26
-- Sistema de Gestión de Evaluaciones de Perfiles - Capit@l Humano
-- Esquema relacional para PostgreSQL 16
-- Derivado de: Diseño Definitivo / 3 - DER (der-grupo26.puml)
-- ============================================================================

-- ============================================================================
-- 1. ENTIDADES DE SEGURIDAD Y ACTORES
-- ============================================================================

CREATE TABLE consultor (
    id_consultor   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre_usuario VARCHAR(100) NOT NULL UNIQUE,
    nombre         VARCHAR(100) NOT NULL,
    apellido       VARCHAR(100) NOT NULL,
    email          VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE candidato (
    nro_candidato    INTEGER PRIMARY KEY,
    apellido         VARCHAR(100) NOT NULL,
    nombre           VARCHAR(100) NOT NULL,
    tipo_documento   VARCHAR(10)  NOT NULL CHECK (tipo_documento IN ('DNI', 'LE', 'LC', 'PP')),
    nro_documento    VARCHAR(30)  NOT NULL,
    fecha_nacimiento DATE         NOT NULL,
    genero           VARCHAR(1)   NOT NULL CHECK (genero IN ('H', 'M')),
    nacionalidad     VARCHAR(100) NOT NULL,
    email            VARCHAR(150) NOT NULL,
    escolaridad      VARCHAR(150) NOT NULL,
    eliminado        BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_candidato_documento UNIQUE (tipo_documento, nro_documento)
);

-- ============================================================================
-- 2. CATÁLOGO DE EVALUACIÓN
-- ============================================================================

CREATE TABLE puesto (
    cod_puesto  VARCHAR(50)  PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL UNIQUE,
    descripcion TEXT         NOT NULL DEFAULT '',
    empresa     VARCHAR(150) NOT NULL,
    activo      BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE competencia (
    cod_competencia VARCHAR(50)  PRIMARY KEY,
    nombre          VARCHAR(150) NOT NULL UNIQUE,
    descripcion     TEXT         NOT NULL DEFAULT '',
    tipo            VARCHAR(20)  NOT NULL CHECK (tipo IN ('TECNICA', 'ACTITUDINAL')),
    activa          BOOLEAN      NOT NULL DEFAULT TRUE
);

-- Relación M:N REQUIERE (PUESTO <-> COMPETENCIA)
CREATE TABLE puesto_competencia (
    cod_puesto         VARCHAR(50)   NOT NULL REFERENCES puesto(cod_puesto),
    cod_competencia    VARCHAR(50)   NOT NULL REFERENCES competencia(cod_competencia),
    ponderacion_minima NUMERIC(4, 2) NOT NULL CHECK (ponderacion_minima >= 0 AND ponderacion_minima <= 10),
    PRIMARY KEY (cod_puesto, cod_competencia)
);

CREATE TABLE factor (
    cod_factor      VARCHAR(50)  PRIMARY KEY,
    cod_competencia VARCHAR(50)  NOT NULL REFERENCES competencia(cod_competencia),
    nombre          VARCHAR(150) NOT NULL,
    descripcion     TEXT         NOT NULL DEFAULT '',
    nro_orden       INTEGER      NOT NULL CHECK (nro_orden > 0),
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_factor_competencia_orden UNIQUE (cod_competencia, nro_orden)
);

CREATE TABLE opcion_de_respuesta (
    cod_opcion  VARCHAR(50)  PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL UNIQUE,
    descripcion TEXT         NOT NULL DEFAULT '',
    activa      BOOLEAN      NOT NULL DEFAULT TRUE
);

-- Entidad débil VALOR_OPCION: usa clave subrogada UUID (ValorOpcion.id) + UNIQUE (cod_opcion, orden)
CREATE TABLE valor_opcion (
    id_valor_opcion UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    cod_opcion      VARCHAR(50)  NOT NULL REFERENCES opcion_de_respuesta(cod_opcion),
    orden           INTEGER      NOT NULL CHECK (orden > 0),
    descripcion     VARCHAR(200) NOT NULL,
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_valor_opcion_orden UNIQUE (cod_opcion, orden),
    CONSTRAINT uq_valor_opcion_descripcion UNIQUE (cod_opcion, descripcion)
);

-- El id UUID de Pregunta se guarda en cod_pregunta (decisión §6 del README del diseño)
CREATE TABLE pregunta (
    cod_pregunta  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    cod_factor    VARCHAR(50)  NOT NULL REFERENCES factor(cod_factor),
    cod_opcion    VARCHAR(50)  NOT NULL REFERENCES opcion_de_respuesta(cod_opcion),
    nombre        VARCHAR(150) NOT NULL,
    enunciado     VARCHAR(500) NOT NULL,
    descripcion   TEXT         NOT NULL DEFAULT '',
    tipo_pregunta VARCHAR(30)  NOT NULL DEFAULT 'SINGLE_CHOICE' CHECK (tipo_pregunta IN ('SINGLE_CHOICE')),
    activa        BOOLEAN      NOT NULL DEFAULT TRUE
);

-- CU021: nombre y enunciado únicos por factor entre preguntas activas
CREATE UNIQUE INDEX uq_pregunta_factor_nombre_activa
    ON pregunta (cod_factor, nombre)
    WHERE activa = TRUE;

CREATE UNIQUE INDEX uq_pregunta_factor_enunciado_activa
    ON pregunta (cod_factor, enunciado)
    WHERE activa = TRUE;

-- Relación M:N PONDERA (PREGUNTA <-> VALOR_OPCION). La suma = 10 por pregunta (R2) se valida en GestorPreguntas.
CREATE TABLE pregunta_valor_opcion (
    cod_pregunta    UUID          NOT NULL REFERENCES pregunta(cod_pregunta),
    id_valor_opcion UUID          NOT NULL REFERENCES valor_opcion(id_valor_opcion),
    ponderacion     NUMERIC(5, 2) NOT NULL CHECK (ponderacion >= 0 AND ponderacion <= 10),
    PRIMARY KEY (cod_pregunta, id_valor_opcion)
);

-- ============================================================================
-- 3. EVALUACIÓN Y CUESTIONARIOS
-- ============================================================================

-- tiempo_maximo y tiempo_maximo_activo se almacenan en segundos (mapean a java.time.Duration)
CREATE TABLE configuracion_cuestionario (
    id_configuracion              UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    instrucciones                 TEXT    NOT NULL,
    cantidad_preguntas_por_bloque INTEGER NOT NULL CHECK (cantidad_preguntas_por_bloque > 0),
    maximo_accesos                INTEGER NOT NULL CHECK (maximo_accesos > 0),
    tiempo_maximo                 BIGINT  NOT NULL CHECK (tiempo_maximo > 0),
    tiempo_maximo_activo          BIGINT  NOT NULL CHECK (tiempo_maximo_activo > 0)
);

CREATE TABLE evaluacion (
    id_evaluacion  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    cod_puesto     VARCHAR(50) NOT NULL REFERENCES puesto(cod_puesto),
    fecha_creacion DATE        NOT NULL DEFAULT CURRENT_DATE,
    activa         BOOLEAN     NOT NULL DEFAULT TRUE
);

CREATE TABLE cuestionario (
    id_cuestionario          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    id_evaluacion            UUID        NOT NULL REFERENCES evaluacion(id_evaluacion),
    nro_candidato            INTEGER     NOT NULL REFERENCES candidato(nro_candidato),
    id_configuracion         UUID        NOT NULL REFERENCES configuracion_cuestionario(id_configuracion),
    clave                    VARCHAR(8)  NOT NULL UNIQUE CHECK (LENGTH(clave) = 8 AND clave ~ '^[A-Z0-9]+$'),
    estado                   VARCHAR(20) NOT NULL CHECK (estado IN ('ACTIVO', 'EN_PROCESO', 'COMPLETO', 'INCOMPLETO', 'SIN_CONTESTAR')),
    fecha_creacion           TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_hora_inicio        TIMESTAMP   NULL,
    fecha_hora_fin           TIMESTAMP   NULL,
    ultimo_ingreso           TIMESTAMP   NULL,
    cantidad_accesos         INTEGER     NOT NULL DEFAULT 0 CHECK (cantidad_accesos >= 0),
    fecha_hora_cambio_estado TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- R1: un candidato puede tener a lo sumo un cuestionario ACTIVO o EN_PROCESO simultáneamente
CREATE UNIQUE INDEX uq_cuestionario_candidato_vigente
    ON cuestionario (nro_candidato)
    WHERE estado IN ('ACTIVO', 'EN_PROCESO');

CREATE TABLE bloque_cuestionario (
    id_cuestionario    UUID    NOT NULL REFERENCES cuestionario(id_cuestionario) ON DELETE CASCADE,
    nro_bloque         INTEGER NOT NULL CHECK (nro_bloque > 0),
    cantidad_preguntas INTEGER NOT NULL CHECK (cantidad_preguntas > 0),
    contestado         BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id_cuestionario, nro_bloque)
);

-- Entidad débil asociativa PREGUNTA_SELECCIONADA (R4 garantizada por FK compuesta al bloque; R7 por UNIQUE)
CREATE TABLE pregunta_seleccionada (
    id_cuestionario      UUID      NOT NULL,
    nro_orden            INTEGER   NOT NULL CHECK (nro_orden > 0),
    nro_bloque           INTEGER   NOT NULL,
    cod_pregunta         UUID      NOT NULL REFERENCES pregunta(cod_pregunta),
    id_valor_opcion      UUID      NULL REFERENCES valor_opcion(id_valor_opcion),
    fecha_hora_respuesta TIMESTAMP NULL,
    PRIMARY KEY (id_cuestionario, nro_orden),
    CONSTRAINT fk_preg_sel_cuestionario FOREIGN KEY (id_cuestionario)
        REFERENCES cuestionario(id_cuestionario) ON DELETE CASCADE,
    CONSTRAINT fk_preg_sel_bloque FOREIGN KEY (id_cuestionario, nro_bloque)
        REFERENCES bloque_cuestionario(id_cuestionario, nro_bloque) ON DELETE CASCADE,
    CONSTRAINT uq_preg_sel_cuestionario_pregunta UNIQUE (id_cuestionario, cod_pregunta)
);

-- ============================================================================
-- 4. RESULTADOS
-- ============================================================================

CREATE TABLE resultado_competencia (
    id_cuestionario  UUID          NOT NULL REFERENCES cuestionario(id_cuestionario) ON DELETE CASCADE,
    cod_competencia  VARCHAR(50)   NOT NULL REFERENCES competencia(cod_competencia),
    puntaje_obtenido NUMERIC(5, 2) NOT NULL CHECK (puntaje_obtenido >= 0 AND puntaje_obtenido <= 10),
    PRIMARY KEY (id_cuestionario, cod_competencia)
);

CREATE TABLE resultado_factor (
    id_cuestionario  UUID          NOT NULL,
    cod_competencia  VARCHAR(50)   NOT NULL,
    cod_factor       VARCHAR(50)   NOT NULL REFERENCES factor(cod_factor),
    puntaje_obtenido NUMERIC(5, 2) NOT NULL CHECK (puntaje_obtenido >= 0 AND puntaje_obtenido <= 10),
    PRIMARY KEY (id_cuestionario, cod_competencia, cod_factor),
    CONSTRAINT fk_res_factor_res_comp FOREIGN KEY (id_cuestionario, cod_competencia)
        REFERENCES resultado_competencia(id_cuestionario, cod_competencia) ON DELETE CASCADE
);

-- ============================================================================
-- 5. AUDITORÍA Y EJECUCIÓN DE PROCESOS
-- ============================================================================

CREATE TABLE registro_auditoria (
    id_registro  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    id_consultor UUID         NOT NULL REFERENCES consultor(id_consultor),
    fecha_hora   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    operacion    VARCHAR(50)  NOT NULL,
    entidad      VARCHAR(100) NOT NULL,
    id_entidad   VARCHAR(100) NOT NULL
);

CREATE TABLE registro_ejecucion (
    id_ejecucion UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    fecha_hora   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    accion       VARCHAR(10) NOT NULL CHECK (accion IN ('INICIO', 'FIN'))
);

-- ============================================================================
-- 6. ÍNDICES DE APOYO PARA CONSULTAS FRECUENTES
-- ============================================================================

CREATE INDEX idx_factor_competencia ON factor (cod_competencia);
CREATE INDEX idx_valor_opcion_opcion ON valor_opcion (cod_opcion);
CREATE INDEX idx_pregunta_factor ON pregunta (cod_factor);
CREATE INDEX idx_pregunta_opcion ON pregunta (cod_opcion);
CREATE INDEX idx_evaluacion_puesto ON evaluacion (cod_puesto);
CREATE INDEX idx_cuestionario_evaluacion ON cuestionario (id_evaluacion);
CREATE INDEX idx_cuestionario_candidato ON cuestionario (nro_candidato);
CREATE INDEX idx_preg_sel_bloque ON pregunta_seleccionada (id_cuestionario, nro_bloque);
CREATE INDEX idx_auditoria_consultor ON registro_auditoria (id_consultor);
CREATE INDEX idx_auditoria_entidad ON registro_auditoria (entidad, id_entidad);
