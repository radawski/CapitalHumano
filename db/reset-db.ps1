<#
.SYNOPSIS
    Recrea desde cero la base de datos PostgreSQL 16 de Capit@l Humano
    ejecutando db/schema.sql y todos los archivos de db/seed/*.sql en orden.

.EXAMPLE
    .\db\reset-db.ps1
    .\db\reset-db.ps1 -User postgres -DbName capital_humano -Port 5432
#>
param(
    [string]$DbHost   = $(if ($env:PGHOST)     { $env:PGHOST }     else { "localhost" }),
    [int]   $Port     = $(if ($env:PGPORT)     { [int]$env:PGPORT } else { 5432 }),
    [string]$DbName   = $(if ($env:PGDATABASE) { $env:PGDATABASE } else { "capital_humano" }),
    [string]$User     = $(if ($env:PGUSER)     { $env:PGUSER }     else { "postgres" }),
    [string]$Password = $env:PGPASSWORD
)

$ErrorActionPreference = "Stop"

function Resolve-Psql {
    $cmd = Get-Command psql -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }

    $candidates = @(
        "C:\Program Files\PostgreSQL\16\bin\psql.exe"
    ) + (Get-ChildItem "C:\Program Files\PostgreSQL\*\bin\psql.exe" -ErrorAction SilentlyContinue |
         Sort-Object FullName -Descending |
         Select-Object -ExpandProperty FullName)

    foreach ($path in $candidates) {
        if (Test-Path $path) { return $path }
    }
    throw "No se encontró psql.exe. Instalá PostgreSQL 16 o agregá su carpeta bin al PATH."
}

$psql = Resolve-Psql
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$schemaFile = Join-Path $scriptDir "schema.sql"
$seedDir = Join-Path $scriptDir "seed"

if (-not (Test-Path $schemaFile)) {
    throw "No se encontró $schemaFile"
}

$prevPassword = $env:PGPASSWORD
if ($Password) {
    $env:PGPASSWORD = $Password
} elseif (-not $env:PGPASSWORD) {
    $secPass = Read-Host "Contraseña de PostgreSQL para el usuario '$User'" -AsSecureString
    $bstr = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($secPass)
    try {
        $env:PGPASSWORD = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($bstr)
    } finally {
        [System.Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)
    }
}

try {
    Write-Host "Usando psql: $psql" -ForegroundColor Cyan

    # 1. Crear la base si no existe
    $exists = & $psql -h $DbHost -p $Port -U $User -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname = '$DbName';"
    if ($LASTEXITCODE -ne 0) { throw "No se pudo conectar a PostgreSQL en ${DbHost}:${Port}." }

    if ($exists.Trim() -ne "1") {
        Write-Host "Creando base de datos '$DbName'..." -ForegroundColor Yellow
        & $psql -h $DbHost -p $Port -U $User -d postgres -v ON_ERROR_STOP=1 -c "CREATE DATABASE `"$DbName`";"
        if ($LASTEXITCODE -ne 0) { throw "Error al crear la base '$DbName'." }
    }

    # 2. Limpiar esquema public
    Write-Host "Limpiando esquema public en '$DbName'..." -ForegroundColor Yellow
    & $psql -h $DbHost -p $Port -U $User -d $DbName -v ON_ERROR_STOP=1 -c "DROP SCHEMA IF EXISTS public CASCADE; CREATE SCHEMA public;"
    if ($LASTEXITCODE -ne 0) { throw "Error al recrear el esquema public." }

    # 3. Aplicar schema.sql
    Write-Host "Ejecutando schema.sql..." -ForegroundColor Yellow
    & $psql -h $DbHost -p $Port -U $User -d $DbName -v ON_ERROR_STOP=1 -f $schemaFile
    if ($LASTEXITCODE -ne 0) { throw "Error al ejecutar schema.sql." }

    # 4. Aplicar seeds en orden alfabético
    $seedFiles = Get-ChildItem -Path $seedDir -Filter "*.sql" -ErrorAction SilentlyContinue | Sort-Object Name
    foreach ($seed in $seedFiles) {
        Write-Host "Ejecutando seed/$($seed.Name)..." -ForegroundColor Yellow
        & $psql -h $DbHost -p $Port -U $User -d $DbName -v ON_ERROR_STOP=1 -f $seed.FullName
        if ($LASTEXITCODE -ne 0) { throw "Error al ejecutar $($seed.Name)." }
    }

    Write-Host "Base de datos '$DbName' recreada y cargada correctamente." -ForegroundColor Green
} finally {
    $env:PGPASSWORD = $prevPassword
}
