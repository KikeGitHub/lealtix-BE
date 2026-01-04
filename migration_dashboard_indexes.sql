-- =====================================================
-- MIGRACIÓN: Optimización de índices para Dashboard
-- Descripción: Agrega índices compuestos para queries de
--              reportes y el campo purchase_amount en
--              coupon_redemption
-- Fecha: 2026-01-03
-- =====================================================

-- 1. Agregar índice compuesto en tenant_customer para reportes de clientes
CREATE INDEX IF NOT EXISTS idx_tenant_customer_tenant_created
ON tenant_customer(tenant_id, created_at);

-- 2. Agregar índices compuestos en coupon para reportes de campañas
CREATE INDEX IF NOT EXISTS idx_coupon_campaign_created
ON coupon(campaign_id, created_at);

CREATE INDEX IF NOT EXISTS idx_coupon_campaign_status
ON coupon(campaign_id, status);

-- 3. Agregar índices compuestos en coupon_redemption para reportes
CREATE INDEX IF NOT EXISTS idx_redemption_tenant_date
ON coupon_redemption(tenant_id, redeemed_at);

CREATE INDEX IF NOT EXISTS idx_redemption_campaign_date
ON coupon_redemption(campaign_id, redeemed_at);

-- 4. Agregar campo purchase_amount en coupon_redemption (si no existe)
-- Este campo es un alias semántico de original_amount para mayor claridad en reportes
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'coupon_redemption'
        AND column_name = 'purchase_amount'
    ) THEN
        ALTER TABLE coupon_redemption
        ADD COLUMN purchase_amount NUMERIC(10, 2);

        -- Copiar valores de original_amount si existen
        UPDATE coupon_redemption
        SET purchase_amount = original_amount
        WHERE original_amount IS NOT NULL;

        RAISE NOTICE 'Campo purchase_amount agregado y sincronizado con original_amount';
    ELSE
        RAISE NOTICE 'Campo purchase_amount ya existe, omitiendo creación';
    END IF;
END $$;

-- 5. Comentarios en columnas para documentación
COMMENT ON COLUMN coupon_redemption.purchase_amount IS
'Monto total de la compra asociada a la redención del cupón. Campo semántico para reportes de ventas.';

COMMENT ON INDEX idx_tenant_customer_tenant_created IS
'Índice compuesto para queries de dashboard: clientes por tenant y rango de fechas';

COMMENT ON INDEX idx_redemption_tenant_date IS
'Índice compuesto para queries de dashboard: redenciones por tenant y rango de fechas';

COMMENT ON INDEX idx_redemption_campaign_date IS
'Índice compuesto para queries de dashboard: redenciones por campaña y rango de fechas';

-- =====================================================
-- FIN DE MIGRACIÓN
-- =====================================================

