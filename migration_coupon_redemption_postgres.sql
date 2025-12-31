-- =====================================================
-- MIGRACIÓN POSTGRESQL: Sistema de Redención de Cupones - Lealtix
-- Versión: 1.0.0
-- Fecha: Diciembre 2024
-- =====================================================

-- =====================================================
-- 1. EXTENDER TABLA COUPON
-- =====================================================

-- Agregar campo qr_token (token seguro para redención)
ALTER TABLE coupon
ADD COLUMN IF NOT EXISTS qr_token VARCHAR(64) UNIQUE;

-- Crear índice único en qr_token
CREATE UNIQUE INDEX IF NOT EXISTS idx_coupon_qr_token ON coupon(qr_token);

-- Agregar campo redeemed_by (quién redimió el cupón)
ALTER TABLE coupon
ADD COLUMN IF NOT EXISTS redeemed_by VARCHAR(200);

-- Nota: Los campos redeemed_at, qr_url, redemption_metadata deberían existir

-- =====================================================
-- 2. GENERAR qr_token PARA CUPONES EXISTENTES
-- =====================================================

-- Generar tokens únicos para cupones que no tienen
-- PostgreSQL usa gen_random_uuid() o md5(random()::text)
UPDATE coupon
SET qr_token = md5(random()::text || clock_timestamp()::text || id::text)
WHERE qr_token IS NULL OR qr_token = '';

-- =====================================================
-- 3. CREAR TABLA COUPON_REDEMPTION (Auditoría)
-- =====================================================

CREATE TABLE IF NOT EXISTS coupon_redemption (
    id BIGSERIAL PRIMARY KEY,

    -- Relaciones
    coupon_id BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL,
    campaign_id BIGINT NOT NULL,

    -- Información del cliente (desnormalizada para auditoría)
    customer_email VARCHAR(200),
    customer_name VARCHAR(200),

    -- Información de redención
    redeemed_by VARCHAR(200) NOT NULL,
    channel VARCHAR(50) NOT NULL, -- QR_WEB, QR_ADMIN, MANUAL, API

    -- Metadatos de contexto
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    location VARCHAR(200),
    metadata TEXT,

    -- Timestamp
    redeemed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Llaves foráneas
    CONSTRAINT fk_redemption_coupon FOREIGN KEY (coupon_id)
        REFERENCES coupon(id) ON DELETE CASCADE,
    CONSTRAINT fk_redemption_campaign FOREIGN KEY (campaign_id)
        REFERENCES campaign(id) ON DELETE CASCADE
);

-- Crear índices para búsquedas rápidas
CREATE INDEX IF NOT EXISTS idx_redemption_coupon ON coupon_redemption(coupon_id);
CREATE INDEX IF NOT EXISTS idx_redemption_tenant ON coupon_redemption(tenant_id);
CREATE INDEX IF NOT EXISTS idx_redemption_campaign ON coupon_redemption(campaign_id);
CREATE INDEX IF NOT EXISTS idx_redemption_date ON coupon_redemption(redeemed_at);
CREATE INDEX IF NOT EXISTS idx_redemption_channel ON coupon_redemption(channel);
CREATE INDEX IF NOT EXISTS idx_redemption_tenant_date ON coupon_redemption(tenant_id, redeemed_at);

-- =====================================================
-- 4. VERIFICACIONES POST-MIGRACIÓN
-- =====================================================

-- Verificar que todos los cupones tienen qr_token
SELECT COUNT(*) as cupones_sin_token
FROM coupon
WHERE qr_token IS NULL OR qr_token = '';
-- Debería retornar 0

-- Verificar estructura de coupon_redemption
SELECT
    column_name,
    data_type,
    character_maximum_length,
    is_nullable
FROM information_schema.columns
WHERE table_name = 'coupon_redemption'
ORDER BY ordinal_position;

-- Verificar índices en coupon
SELECT
    i.relname as index_name,
    a.attname as column_name
FROM pg_class t
JOIN pg_index ix ON t.oid = ix.indrelid
JOIN pg_class i ON i.oid = ix.indexrelid
JOIN pg_attribute a ON a.attrelid = t.oid AND a.attnum = ANY(ix.indkey)
WHERE t.relname = 'coupon'
AND i.relname LIKE '%qr_token%';

-- =====================================================
-- FIN DE LA MIGRACIÓN
-- =====================================================

SELECT 'Migración de redención de cupones completada exitosamente' as status;

