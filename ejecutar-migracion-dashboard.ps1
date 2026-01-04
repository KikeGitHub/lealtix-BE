# Script PowerShell para ejecutar la migración de índices del Dashboard
# Fecha: 2026-01-03
# Descripción: Agrega índices compuestos y campo purchase_amount

# Configuración de conexión (ajustar según tu entorno)
$DB_HOST = "localhost"
$DB_PORT = "5432"
$DB_NAME = "lealtix_db"
$DB_USER = "postgres"  # Cambiar por tu usuario
$MIGRATION_FILE = "migration_dashboard_indexes.sql"

# Colores para output
function Write-Success { param($msg) Write-Host $msg -ForegroundColor Green }
function Write-Error-Custom { param($msg) Write-Host $msg -ForegroundColor Red }
function Write-Info { param($msg) Write-Host $msg -ForegroundColor Cyan }

Write-Info "=========================================="
Write-Info "  MIGRACIÓN DASHBOARD - LEALTIX"
Write-Info "=========================================="
Write-Host ""

# Verificar que existe el archivo de migración
if (-not (Test-Path $MIGRATION_FILE)) {
    Write-Error-Custom "❌ ERROR: No se encontró el archivo $MIGRATION_FILE"
    Write-Error-Custom "   Asegúrate de ejecutar este script desde la carpeta del proyecto."
    exit 1
}

Write-Success "✅ Archivo de migración encontrado: $MIGRATION_FILE"
Write-Host ""

# Verificar que psql está disponible
try {
    $psqlVersion = psql --version 2>&1
    Write-Success "✅ PostgreSQL Client detectado: $psqlVersion"
} catch {
    Write-Error-Custom "❌ ERROR: No se encontró 'psql' en el PATH"
    Write-Error-Custom "   Instala PostgreSQL client o agrega psql al PATH del sistema."
    exit 1
}

Write-Host ""
Write-Info "Configuración de conexión:"
Write-Host "  Host: $DB_HOST"
Write-Host "  Puerto: $DB_PORT"
Write-Host "  Base de datos: $DB_NAME"
Write-Host "  Usuario: $DB_USER"
Write-Host ""

# Solicitar contraseña
$securePassword = Read-Host "Ingresa la contraseña de PostgreSQL" -AsSecureString
$BSTR = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
$DB_PASSWORD = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($BSTR)

Write-Host ""
Write-Info "Ejecutando migración..."
Write-Host ""

# Ejecutar migración
$env:PGPASSWORD = $DB_PASSWORD
try {
    psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f $MIGRATION_FILE

    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Success "=========================================="
        Write-Success "  ✅ MIGRACIÓN COMPLETADA EXITOSAMENTE"
        Write-Success "=========================================="
        Write-Host ""
        Write-Info "Índices creados:"
        Write-Host "  • idx_tenant_customer_tenant_created"
        Write-Host "  • idx_coupon_campaign_created"
        Write-Host "  • idx_coupon_campaign_status"
        Write-Host "  • idx_redemption_tenant_date"
        Write-Host "  • idx_redemption_campaign_date"
        Write-Host ""
        Write-Info "Campo agregado:"
        Write-Host "  • purchase_amount en coupon_redemption"
        Write-Host ""
        Write-Success "✅ El backend está listo para el dashboard."
        Write-Host ""
    } else {
        Write-Error-Custom "❌ ERROR: La migración falló con código $LASTEXITCODE"
        exit 1
    }
} catch {
    Write-Error-Custom "❌ ERROR durante la ejecución de la migración:"
    Write-Error-Custom $_.Exception.Message
    exit 1
} finally {
    # Limpiar variable de entorno de contraseña
    Remove-Item Env:\PGPASSWORD -ErrorAction SilentlyContinue
}

Write-Info "Próximos pasos:"
Write-Host "  1. Compilar proyecto: mvn clean package"
Write-Host "  2. Ejecutar aplicación"
Write-Host "  3. Probar endpoints en: http://localhost:8080/swagger-ui/index.html"
Write-Host ""

