-- Migración: Cambiar ID de coupon_redemption de BIGINT a VARCHAR(10) con UID
-- Fecha: 2025-12-30
-- Propósito: Implementar UID de 10 posiciones para identificar redenciones de forma única

-- IMPORTANTE: Realizar backup antes de ejecutar esta migración

-- Paso 1: Crear una nueva tabla temporal con la estructura correcta
CREATE TABLE coupon_redemption_new (
    id VARCHAR(10) NOT NULL PRIMARY KEY,
    coupon_id BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL,
    campaign_id BIGINT NOT NULL,
    customer_email VARCHAR(200),
    customer_name VARCHAR(200),
    redeemed_by VARCHAR(200) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    location VARCHAR(200),
    metadata TEXT,
    redeemed_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_redemption_coupon FOREIGN KEY (coupon_id) REFERENCES coupon(id),
    CONSTRAINT fk_redemption_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id),
    CONSTRAINT fk_redemption_campaign FOREIGN KEY (campaign_id) REFERENCES campaign(id)
);

-- Paso 2: Crear índices en la nueva tabla
CREATE INDEX idx_redemption_coupon ON coupon_redemption_new(coupon_id);
CREATE INDEX idx_redemption_tenant ON coupon_redemption_new(tenant_id);
CREATE INDEX idx_redemption_campaign ON coupon_redemption_new(campaign_id);
CREATE INDEX idx_redemption_date ON coupon_redemption_new(redeemed_at);
CREATE INDEX idx_redemption_channel ON coupon_redemption_new(channel);

-- Paso 3: Migrar datos existentes (si los hay) generando UIDs únicos
-- Nota: Esta función genera UIDs de 10 caracteres alfanuméricos
DO $$
DECLARE
    rec RECORD;
    new_uid VARCHAR(10);
    chars VARCHAR(32) := '0123456789BCDFGHJKLMNPQRSTVWXYZ';
    i INTEGER;
    uid_exists BOOLEAN;
BEGIN
    FOR rec IN SELECT * FROM coupon_redemption ORDER BY id
    LOOP
        -- Generar UID único
        LOOP
            new_uid := '';
            FOR i IN 1..10 LOOP
                new_uid := new_uid || substr(chars, (floor(random() * 32)::int + 1), 1);
            END LOOP;

            -- Verificar que no exista
            SELECT EXISTS(SELECT 1 FROM coupon_redemption_new WHERE id = new_uid) INTO uid_exists;
            EXIT WHEN NOT uid_exists;
        END LOOP;

        -- Insertar registro con nuevo UID
        INSERT INTO coupon_redemption_new (
            id, coupon_id, tenant_id, campaign_id,
            customer_email, customer_name, redeemed_by,
            channel, ip_address, user_agent, location,
            metadata, redeemed_at
        ) VALUES (
            new_uid, rec.coupon_id, rec.tenant_id, rec.campaign_id,
            rec.customer_email, rec.customer_name, rec.redeemed_by,
            rec.channel, rec.ip_address, rec.user_agent, rec.location,
            rec.metadata, rec.redeemed_at
        );
    END LOOP;
END $$;

-- Paso 4: Verificar que se migraron todos los registros
DO $$
DECLARE
    old_count INTEGER;
    new_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO old_count FROM coupon_redemption;
    SELECT COUNT(*) INTO new_count FROM coupon_redemption_new;

    IF old_count != new_count THEN
        RAISE EXCEPTION 'Error en migración: registros antiguos=%, nuevos=%', old_count, new_count;
    ELSE
        RAISE NOTICE 'Migración exitosa: % registros migrados', new_count;
    END IF;
END $$;

-- Paso 5: Hacer backup de la tabla antigua (renombrar)
ALTER TABLE coupon_redemption RENAME TO coupon_redemption_old;

-- Paso 6: Renombrar la nueva tabla
ALTER TABLE coupon_redemption_new RENAME TO coupon_redemption;

-- Paso 7: (OPCIONAL) Después de verificar que todo funciona, eliminar la tabla antigua
-- DROP TABLE coupon_redemption_old;

-- Verificación final
SELECT
    'Migración completada' as status,
    COUNT(*) as total_records,
    MIN(LENGTH(id)) as min_id_length,
    MAX(LENGTH(id)) as max_id_length
FROM coupon_redemption;

