-- =====================================================
-- MIGRACIÓN: Agregar campos de cálculo de descuentos
-- Tabla: coupon_redemption
-- Fecha: 2026-01-03
-- Descripción: Agrega campos para almacenar el cálculo
--              de descuentos en la redención de cupones
-- =====================================================

-- Agregar campos de montos
ALTER TABLE coupon_redemption
ADD COLUMN IF NOT EXISTS original_amount NUMERIC(10, 2),
ADD COLUMN IF NOT EXISTS discount_amount NUMERIC(10, 2),
ADD COLUMN IF NOT EXISTS final_amount NUMERIC(10, 2);

-- Agregar campos de tipo y valor del cupón
ALTER TABLE coupon_redemption
ADD COLUMN IF NOT EXISTS coupon_type VARCHAR(50),
ADD COLUMN IF NOT EXISTS coupon_value NUMERIC(10, 2);

-- Agregar comentarios para documentación
COMMENT ON COLUMN coupon_redemption.original_amount IS 'Monto original de la cuenta antes del descuento';
COMMENT ON COLUMN coupon_redemption.discount_amount IS 'Monto del descuento aplicado';
COMMENT ON COLUMN coupon_redemption.final_amount IS 'Monto final después del descuento';
COMMENT ON COLUMN coupon_redemption.coupon_type IS 'Tipo de cupón: PERCENT_DISCOUNT, FIXED_AMOUNT, FREE_PRODUCT, BUY_X_GET_Y, CUSTOM';
COMMENT ON COLUMN coupon_redemption.coupon_value IS 'Valor del cupón (porcentaje o monto fijo)';

-- Crear índice para consultas de reportes por tipo de cupón
CREATE INDEX IF NOT EXISTS idx_redemption_coupon_type ON coupon_redemption(coupon_type);

-- Verificar la estructura actualizada
SELECT
    column_name,
    data_type,
    character_maximum_length,
    numeric_precision,
    numeric_scale,
    is_nullable
FROM information_schema.columns
WHERE table_name = 'coupon_redemption'
AND column_name IN ('original_amount', 'discount_amount', 'final_amount', 'coupon_type', 'coupon_value')
ORDER BY ordinal_position;

