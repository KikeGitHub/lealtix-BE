-- ========================================
-- Script de Limpieza para Reintento de Migración V3
-- Fecha: 2025-12-27
-- Ejecutar ANTES de reiniciar el servidor
-- ========================================

-- 1. Ver estado actual de migraciones
SELECT version, description, type, script, success, installed_on
FROM flyway_schema_history
ORDER BY installed_rank;

-- 2. Eliminar el registro fallido de la migración V3
DELETE FROM flyway_schema_history WHERE version = '3';

-- 3. Limpiar tablas que pudieron crearse parcialmente
DROP TABLE IF EXISTS coupon CASCADE;
DROP TABLE IF EXISTS promotion_reward CASCADE;

-- 4. Eliminar columna last_click_at si se agregó parcialmente
ALTER TABLE campaign_result DROP COLUMN IF EXISTS last_click_at;

-- 5. Verificar que la limpieza fue exitosa
SELECT version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;

-- Deberías ver solo V1 y V2 en estado SUCCESS
-- La migración V3 NO debe aparecer

-- 6. Verificar que las tablas no existan
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_name IN ('promotion_reward', 'coupon');
-- Debe devolver 0 filas

-- 7. Verificar columnas de campaign_result
SELECT column_name, data_type
FROM information_schema.columns
WHERE table_name = 'campaign_result'
ORDER BY ordinal_position;
-- last_click_at NO debe aparecer

-- ========================================
-- LISTO PARA REINICIAR EL SERVIDOR
-- ========================================

