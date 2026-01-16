# Script de validación rápida para el campo description en PromotionReward
# Ejecutar desde la raíz del proyecto: .\validate-description.ps1

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "VALIDACIÓN: Campo description en Promotion Reward" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# 1. Verificar que los archivos modificados existen
Write-Host "1. Verificando archivos modificados..." -ForegroundColor Yellow
$files = @(
    "src\main\java\com\lealtixservice\dto\ConfigureRewardRequest.java",
    "src\main\java\com\lealtixservice\dto\PromotionRewardDTO.java",
    "src\main\java\com\lealtixservice\entity\PromotionReward.java",
    "src\main\java\com\lealtixservice\service\impl\PromotionRewardServiceImpl.java",
    "src\main\resources\db\migration\V4__ensure_promotion_reward_description_length.sql",
    "src\test\java\com\lealtixservice\service\PromotionRewardServiceImplTest.java",
    "src\test\java\com\lealtixservice\controller\ConfigureRewardIntegrationTest.java"
)

$allExist = $true
foreach ($file in $files) {
    if (Test-Path $file) {
        Write-Host "   ✓ $file" -ForegroundColor Green
    } else {
        Write-Host "   ✗ $file - NO ENCONTRADO" -ForegroundColor Red
        $allExist = $false
    }
}

if (-not $allExist) {
    Write-Host ""
    Write-Host "ERROR: Algunos archivos no existen. Verifica la estructura del proyecto." -ForegroundColor Red
    exit 1
}

Write-Host ""

# 2. Compilar el proyecto
Write-Host "2. Compilando el proyecto..." -ForegroundColor Yellow
.\mvnw clean compile -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "   ✗ Error en compilación" -ForegroundColor Red
    exit 1
}
Write-Host "   ✓ Compilación exitosa" -ForegroundColor Green
Write-Host ""

# 3. Ejecutar tests específicos de description
Write-Host "3. Ejecutando tests de validación..." -ForegroundColor Yellow
.\mvnw -Dtest=PromotionRewardServiceImplTest,ConfigureRewardIntegrationTest test

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "============================================" -ForegroundColor Green
    Write-Host "✓ VALIDACIÓN EXITOSA" -ForegroundColor Green
    Write-Host "============================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Todos los tests pasaron correctamente." -ForegroundColor Green
    Write-Host "El campo description está correctamente implementado en:" -ForegroundColor Green
    Write-Host "  • DTOs (ConfigureRewardRequest, PromotionRewardDTO)" -ForegroundColor White
    Write-Host "  • Entity (PromotionReward)" -ForegroundColor White
    Write-Host "  • Service (validación y sanitización)" -ForegroundColor White
    Write-Host "  • Database (migración V4)" -ForegroundColor White
    Write-Host "  • Tests (cobertura completa)" -ForegroundColor White
    Write-Host ""
    Write-Host "Puedes probar el endpoint con:" -ForegroundColor Cyan
    Write-Host 'curl -X POST "http://localhost:8080/api/campaigns/123/reward" \' -ForegroundColor White
    Write-Host '  -H "Content-Type: application/json" \' -ForegroundColor White
    Write-Host '  -d ''{"rewardType":"PERCENT_DISCOUNT","description":"10% descuento","numericValue":10,"usageLimit":1000}''' -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "============================================" -ForegroundColor Red
    Write-Host "✗ VALIDACIÓN FALLIDA" -ForegroundColor Red
    Write-Host "============================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Algunos tests fallaron. Revisa el output anterior." -ForegroundColor Red
    Write-Host "Consulta el archivo VALIDACION_DESCRIPTION_REWARD.md para más detalles." -ForegroundColor Yellow
    exit 1
}

