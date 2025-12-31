# Script PowerShell para ejecutar migración de PostgreSQL
# Ejecutar como: .\ejecutar-migracion.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Migración de Redención de Cupones" -ForegroundColor Cyan
Write-Host "  Lealtix - PostgreSQL" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Solicitar credenciales
$usuario = Read-Host "Usuario de PostgreSQL (por defecto: postgres)"
if ([string]::IsNullOrWhiteSpace($usuario)) {
    $usuario = "postgres"
}

$database = Read-Host "Nombre de la base de datos (por defecto: lealtix_db)"
if ([string]::IsNullOrWhiteSpace($database)) {
    $database = "lealtix_db"
}

$host_db = Read-Host "Host (por defecto: localhost)"
if ([string]::IsNullOrWhiteSpace($host_db)) {
    $host_db = "localhost"
}

$puerto = Read-Host "Puerto (por defecto: 5432)"
if ([string]::IsNullOrWhiteSpace($puerto)) {
    $puerto = "5432"
}

Write-Host ""
Write-Host "Configuración:" -ForegroundColor Yellow
Write-Host "  Usuario: $usuario" -ForegroundColor White
Write-Host "  Base de datos: $database" -ForegroundColor White
Write-Host "  Host: $host_db" -ForegroundColor White
Write-Host "  Puerto: $puerto" -ForegroundColor White
Write-Host ""

$confirmar = Read-Host "¿Ejecutar migración? (S/N)"
if ($confirmar -ne "S" -and $confirmar -ne "s") {
    Write-Host "Migración cancelada." -ForegroundColor Red
    exit
}

# Ruta del script SQL
$scriptPath = Join-Path $PSScriptRoot "migration_coupon_redemption_postgres.sql"

if (-not (Test-Path $scriptPath)) {
    Write-Host "ERROR: No se encontró el archivo de migración:" -ForegroundColor Red
    Write-Host $scriptPath -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Ejecutando migración..." -ForegroundColor Green

# Intentar ejecutar con psql
try {
    # Método 1: psql en PATH
    $env:PGPASSWORD = Read-Host "Contraseña de PostgreSQL" -AsSecureString
    $BSTR = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($env:PGPASSWORD)
    $env:PGPASSWORD = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($BSTR)

    psql -U $usuario -d $database -h $host_db -p $puerto -f $scriptPath

    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "✅ Migración completada exitosamente!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Próximos pasos:" -ForegroundColor Yellow
        Write-Host "1. Reinicia tu aplicación Spring Boot" -ForegroundColor White
        Write-Host "2. Verifica los endpoints en Swagger" -ForegroundColor White
        Write-Host "3. Prueba la redención de cupones" -ForegroundColor White
    } else {
        Write-Host ""
        Write-Host "❌ Error al ejecutar la migración" -ForegroundColor Red
        Write-Host "Código de error: $LASTEXITCODE" -ForegroundColor Red
    }
} catch {
    Write-Host ""
    Write-Host "❌ Error: No se pudo ejecutar psql" -ForegroundColor Red
    Write-Host "Mensaje: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Opciones alternativas:" -ForegroundColor Yellow
    Write-Host "1. Ejecutar manualmente desde psql:" -ForegroundColor White
    Write-Host "   psql -U $usuario -d $database" -ForegroundColor Cyan
    Write-Host "   \i '$scriptPath'" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "2. Usar pgAdmin (GUI):" -ForegroundColor White
    Write-Host "   - Abrir pgAdmin" -ForegroundColor Cyan
    Write-Host "   - Query Tool → Abrir archivo → Ejecutar" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "3. Cambiar temporalmente hibernate.ddl-auto a 'update'" -ForegroundColor White
} finally {
    # Limpiar contraseña
    $env:PGPASSWORD = $null
}

Write-Host ""
Read-Host "Presiona Enter para salir"

