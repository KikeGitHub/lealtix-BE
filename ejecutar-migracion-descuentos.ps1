# Script para ejecutar la migración de descuentos
# Ejecutar con: .\ejecutar-migracion-descuentos.ps1

$DB_USER = $env:DB_USER
$DB_PASSWORD = $env:DB_PASSWORD
$DB_NAME = "lealtix_db"
$DB_HOST = "localhost"
$DB_PORT = "5432"

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "Migración: Agregar campos de descuento" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""

if (-not $DB_USER) {
    Write-Host "ERROR: Variable de entorno DB_USER no encontrada" -ForegroundColor Red
    Write-Host "Ejecuta: `$env:DB_USER='tu_usuario'" -ForegroundColor Yellow
    exit 1
}

if (-not $DB_PASSWORD) {
    Write-Host "ERROR: Variable de entorno DB_PASSWORD no encontrada" -ForegroundColor Red
    Write-Host "Ejecuta: `$env:DB_PASSWORD='tu_password'" -ForegroundColor Yellow
    exit 1
}

Write-Host "Conectando a PostgreSQL..." -ForegroundColor Yellow
Write-Host "Host: $DB_HOST" -ForegroundColor Gray
Write-Host "Base de datos: $DB_NAME" -ForegroundColor Gray
Write-Host ""

# Crear el script SQL temporal
$sqlScript = @"
-- Agregar campos de montos
ALTER TABLE coupon_redemption
ADD COLUMN IF NOT EXISTS original_amount NUMERIC(10, 2),
ADD COLUMN IF NOT EXISTS discount_amount NUMERIC(10, 2),
ADD COLUMN IF NOT EXISTS final_amount NUMERIC(10, 2);

-- Agregar campos de tipo y valor del cupón
ALTER TABLE coupon_redemption
ADD COLUMN IF NOT EXISTS coupon_type VARCHAR(50),
ADD COLUMN IF NOT EXISTS coupon_value NUMERIC(10, 2);

-- Crear índice para consultas de reportes por tipo de cupón
CREATE INDEX IF NOT EXISTS idx_redemption_coupon_type ON coupon_redemption(coupon_type);

-- Verificar columnas creadas
SELECT
    column_name,
    data_type,
    numeric_precision,
    numeric_scale,
    is_nullable
FROM information_schema.columns
WHERE table_name = 'coupon_redemption'
AND column_name IN ('original_amount', 'discount_amount', 'final_amount', 'coupon_type', 'coupon_value')
ORDER BY ordinal_position;
"@

# Guardar script temporal
$tempFile = "temp_migration.sql"
$sqlScript | Out-File -FilePath $tempFile -Encoding UTF8

# Ejecutar con psql
$env:PGPASSWORD = $DB_PASSWORD
try {
    Write-Host "Ejecutando migración..." -ForegroundColor Yellow
    psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f $tempFile

    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "==================================================" -ForegroundColor Green
        Write-Host "✓ Migración ejecutada exitosamente" -ForegroundColor Green
        Write-Host "==================================================" -ForegroundColor Green
    } else {
        Write-Host ""
        Write-Host "==================================================" -ForegroundColor Red
        Write-Host "✗ Error al ejecutar la migración" -ForegroundColor Red
        Write-Host "==================================================" -ForegroundColor Red
    }
} finally {
    # Limpiar
    Remove-Item $tempFile -ErrorAction SilentlyContinue
    Remove-Item Env:\PGPASSWORD -ErrorAction SilentlyContinue
}

Write-Host ""
Write-Host "Presiona Enter para continuar..." -ForegroundColor Gray
Read-Host

