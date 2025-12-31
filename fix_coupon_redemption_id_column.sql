-- Script para corregir el tipo de columna ID en coupon_redemption
-- De: BIGSERIAL a VARCHAR(10)
-- Fecha: 2025-12-30

-- IMPORTANTE: Este script modifica la tabla existente
-- Ejecutar en ambiente de desarrollo/testing primero

BEGIN;

-- Paso 1: Eliminar la secuencia asociada si existe
DROP SEQUENCE IF EXISTS coupon_redemption_id_seq CASCADE;

-- Paso 2: Modificar el tipo de columna de BIGINT a VARCHAR(10)
-- NOTA: Solo funciona si la tabla está vacía o tiene pocos registros
ALTER TABLE coupon_redemption
ALTER COLUMN id TYPE VARCHAR(10);

-- Paso 3: Eliminar el valor por defecto (ya no es autoincremental)
ALTER TABLE coupon_redemption
ALTER COLUMN id DROP DEFAULT;

COMMIT;

-- Verificar el cambio
SELECT column_name, data_type, character_maximum_length
FROM information_schema.columns
WHERE table_name = 'coupon_redemption' AND column_name = 'id';

