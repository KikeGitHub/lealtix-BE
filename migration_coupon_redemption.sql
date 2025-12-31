-- =====================================================
-- MIGRACIÓN: Sistema de Redención de Cupones - Lealtix
-- Versión: 1.0.0
-- Fecha: Diciembre 2024
-- =====================================================

-- =====================================================
-- 1. EXTENDER TABLA COUPON
-- =====================================================

-- Agregar campo qr_token (token seguro para redención)
ALTER TABLE coupon
ADD COLUMN qr_token VARCHAR(64) UNIQUE AFTER code;

-- Crear índice único en qr_token
CREATE UNIQUE INDEX idx_coupon_qr_token ON coupon(qr_token);

-- Agregar campo redeemed_by (quién redimió el cupón)
ALTER TABLE coupon
ADD COLUMN redeemed_by VARCHAR(200) AFTER redeemed_at;

-- Nota: Los campos redeemed_at, qr_url, redemption_metadata ya existen

-- =====================================================
-- 2. GENERAR qr_token PARA CUPONES EXISTENTES
-- =====================================================

-- Generar tokens únicos para cupones que no tienen
-- IMPORTANTE: Ejecutar solo una vez
UPDATE coupon
SET qr_token = CONCAT(
    LOWER(REPLACE(UUID(), '-', '')),
    LOWER(REPLACE(UUID(), '-', ''))
)
WHERE qr_token IS NULL OR qr_token = '';

-- =====================================================
-- 3. CREAR ENUM PARA REDEMPTION_CHANNEL
-- =====================================================

-- En MySQL no hay ENUM real, se valida en aplicación
-- Valores permitidos: QR_WEB, QR_ADMIN, MANUAL, API

-- =====================================================
-- 4. CREAR TABLA COUPON_REDEMPTION (Auditoría)
-- =====================================================

CREATE TABLE IF NOT EXISTS coupon_redemption (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Relaciones
    coupon_id BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL,
    campaign_id BIGINT NOT NULL,

    -- Información del cliente (desnormalizada para auditoría)
    customer_email VARCHAR(200),
    customer_name VARCHAR(200),

    -- Información de redención
    redeemed_by VARCHAR(200) NOT NULL COMMENT 'Usuario/email que ejecutó la redención',
    channel VARCHAR(50) NOT NULL COMMENT 'QR_WEB, QR_ADMIN, MANUAL, API',

    -- Metadatos de contexto
    ip_address VARCHAR(45) COMMENT 'Dirección IP del cliente',
    user_agent VARCHAR(500) COMMENT 'Información del navegador/app',
    location VARCHAR(200) COMMENT 'Punto de venta o ubicación',
    metadata TEXT COMMENT 'Información adicional en JSON',

    -- Timestamp
    redeemed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Llaves foráneas
    CONSTRAINT fk_redemption_coupon FOREIGN KEY (coupon_id)
        REFERENCES coupon(id) ON DELETE CASCADE,
    CONSTRAINT fk_redemption_campaign FOREIGN KEY (campaign_id)
        REFERENCES campaign(id) ON DELETE CASCADE,

    -- Índices para búsquedas rápidas
    INDEX idx_redemption_coupon (coupon_id),
    INDEX idx_redemption_tenant (tenant_id),
    INDEX idx_redemption_campaign (campaign_id),
    INDEX idx_redemption_date (redeemed_at),
    INDEX idx_redemption_channel (channel),
    INDEX idx_redemption_tenant_date (tenant_id, redeemed_at)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Tabla de auditoría de redenciones de cupones';

-- =====================================================
-- 5. VERIFICACIONES POST-MIGRACIÓN
-- =====================================================

-- Verificar que todos los cupones tienen qr_token
SELECT COUNT(*) as cupones_sin_token
FROM coupon
WHERE qr_token IS NULL OR qr_token = '';

-- Debería retornar 0

-- Verificar estructura de coupon_redemption
DESCRIBE coupon_redemption;

-- Verificar índices en coupon
SHOW INDEX FROM coupon WHERE Key_name LIKE '%qr_token%';

-- =====================================================
-- 6. DATOS DE PRUEBA (OPCIONAL - Solo para desarrollo)
-- =====================================================

-- Insertar redención de prueba (solo si existe un cupón de prueba)
/*
INSERT INTO coupon_redemption (
    coupon_id,
    tenant_id,
    campaign_id,
    customer_email,
    customer_name,
    redeemed_by,
    channel,
    location,
    redeemed_at
) VALUES (
    1,  -- ID de cupón existente
    1,  -- ID de tenant
    1,  -- ID de campaña
    'test@example.com',
    'Cliente de Prueba',
    'admin@restaurant.com',
    'MANUAL',
    'Sucursal Centro',
    NOW()
);
*/

-- =====================================================
-- 7. ROLLBACK (Solo en caso de emergencia)
-- =====================================================

/*
-- ADVERTENCIA: Esto eliminará toda la funcionalidad de redención

-- Eliminar tabla de auditoría
DROP TABLE IF EXISTS coupon_redemption;

-- Eliminar campos nuevos de coupon
ALTER TABLE coupon DROP COLUMN qr_token;
ALTER TABLE coupon DROP COLUMN redeemed_by;

-- Eliminar índice
DROP INDEX idx_coupon_qr_token ON coupon;
*/

-- =====================================================
-- 8. QUERIES ÚTILES PARA REPORTES
-- =====================================================

-- Tasa de redención por tenant
/*
SELECT
    t.id as tenant_id,
    t.nombreNegocio as tenant_name,
    COUNT(DISTINCT c.id) as total_cupones,
    COUNT(DISTINCT CASE WHEN c.status = 'SENT' THEN c.id END) as enviados,
    COUNT(DISTINCT CASE WHEN c.status = 'REDEEMED' THEN c.id END) as redimidos,
    ROUND(
        (COUNT(DISTINCT CASE WHEN c.status = 'REDEEMED' THEN c.id END) * 100.0) /
        NULLIF(COUNT(DISTINCT CASE WHEN c.status = 'SENT' THEN c.id END), 0),
        2
    ) as tasa_redencion
FROM tenant t
LEFT JOIN campaign cam ON cam.business_id = t.id
LEFT JOIN coupon c ON c.campaign_id = cam.id
GROUP BY t.id, t.nombreNegocio
ORDER BY tasa_redencion DESC;
*/

-- Redenciones por canal
/*
SELECT
    channel,
    COUNT(*) as total_redenciones,
    COUNT(DISTINCT tenant_id) as tenants_unicos
FROM coupon_redemption
GROUP BY channel
ORDER BY total_redenciones DESC;
*/

-- Top 10 campañas con mejor tasa de redención
/*
SELECT
    cam.id,
    cam.title,
    COUNT(DISTINCT CASE WHEN c.status = 'SENT' THEN c.id END) as enviados,
    COUNT(DISTINCT CASE WHEN c.status = 'REDEEMED' THEN c.id END) as redimidos,
    ROUND(
        (COUNT(DISTINCT CASE WHEN c.status = 'REDEEMED' THEN c.id END) * 100.0) /
        NULLIF(COUNT(DISTINCT CASE WHEN c.status = 'SENT' THEN c.id END), 0),
        2
    ) as tasa_redencion
FROM campaign cam
LEFT JOIN coupon c ON c.campaign_id = cam.id
GROUP BY cam.id, cam.title
HAVING enviados > 0
ORDER BY tasa_redencion DESC
LIMIT 10;
*/

-- =====================================================
-- FIN DE LA MIGRACIÓN
-- =====================================================

-- Registro de ejecución
SELECT 'Migración de redención de cupones completada exitosamente' as status;

