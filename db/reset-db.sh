#!/usr/bin/env bash
# Recrea desde cero la base de datos PostgreSQL 16 de Capit@l Humano
# ejecutando db/schema.sql y todos los archivos de db/seed/*.sql en orden.
#
# Uso:
#   ./db/reset-db.sh
#   PGUSER=postgres PGPASSWORD=postgres PGDATABASE=capital_humano ./db/reset-db.sh

set -euo pipefail

PGHOST="${PGHOST:-localhost}"
PGPORT="${PGPORT:-5432}"
PGDATABASE="${PGDATABASE:-capital_humano}"
PGUSER="${PGUSER:-postgres}"

export PGHOST PGPORT PGUSER

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SCHEMA_FILE="${SCRIPT_DIR}/schema.sql"
SEED_DIR="${SCRIPT_DIR}/seed"

if ! command -v psql >/dev/null 2>&1; then
    echo "Error: no se encontró psql en el PATH." >&2
    exit 1
fi

# 1. Crear la base si no existe
EXISTS="$(psql -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname = '${PGDATABASE}';")"
if [ "${EXISTS}" != "1" ]; then
    echo "Creando base de datos '${PGDATABASE}'..."
    psql -d postgres -v ON_ERROR_STOP=1 -c "CREATE DATABASE \"${PGDATABASE}\";"
fi

# 2. Limpiar esquema public
echo "Limpiando esquema public en '${PGDATABASE}'..."
psql -d "${PGDATABASE}" -v ON_ERROR_STOP=1 -c "DROP SCHEMA IF EXISTS public CASCADE; CREATE SCHEMA public;"

# 3. Aplicar schema.sql
echo "Ejecutando schema.sql..."
psql -d "${PGDATABASE}" -v ON_ERROR_STOP=1 -f "${SCHEMA_FILE}"

# 4. Aplicar seeds en orden alfabético
for seed in "${SEED_DIR}"/*.sql; do
    [ -e "${seed}" ] || continue
    echo "Ejecutando seed/$(basename "${seed}")..."
    psql -d "${PGDATABASE}" -v ON_ERROR_STOP=1 -f "${seed}"
done

echo "Base de datos '${PGDATABASE}' recreada y cargada correctamente."
