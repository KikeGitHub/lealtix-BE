-- Script para corregir el tipo de columna ID en coupon_redemption CON DATOS
-- De: BIGSERIAL a VARCHAR(10) conservando datos existentes
-- Fecha: 2025-12-30

-- IMPORTANTE: Hacer backup antes de ejecutar
-- pg_dump -U usuario -d lealtix -t coupon_redemption > backup_coupon_redemption.sql

BEGIN;

-- Paso 1: Agregar columna temporal para el nuevo ID
ALTER TABLE coupon_redemption
ADD COLUMN id_temp VARCHAR(10);

-- Paso 2: Generar UIDs para los registros existentes
DO $$
DECLARE
    rec RECORD;
    new_uid VARCHAR(10);
    chars VARCHAR(32) := '0123456789BCDFGHJKLMNPQRSTVWXYZ';
    i INTEGER;
    uid_exists BOOLEAN;
BEGIN
    FOR rec IN SELECT id FROM coupon_redemption ORDER BY id
    LOOP
        -- Generar UID único
        LOOP
            new_uid := '';
            FOR i IN 1..10 LOOP
                new_uid := new_uid || substr(chars, (floor(random() * 32)::int + 1), 1);
            END LOOP;

            -- Verificar que no exista
            SELECT EXISTS(SELECT 1 FROM coupon_redemption WHERE id_temp = new_uid) INTO uid_exists;
            EXIT WHEN NOT uid_exists;
        END LOOP;

        -- Asignar nuevo UID
        UPDATE coupon_redemption SET id_temp = new_uid WHERE id = rec.id;

        RAISE NOTICE 'ID % convertido a %', rec.id, new_uid;
    END LOOP;
END $$;

-- Paso 3: Eliminar la columna antigua y sus dependencias
ALTER TABLE coupon_redemption DROP CONSTRAINT coupon_redemption_pkey CASCADE;
DROP SEQUENCE IF EXISTS coupon_redemption_id_seq CASCADE;
ALTER TABLE coupon_redemption DROP COLUMN id;

-- Paso 4: Renombrar la columna temporal a id
ALTER TABLE coupon_redemption RENAME COLUMN id_temp TO id;

-- Paso 5: Agregar restricción NOT NULL y PRIMARY KEY
ALTER TABLE coupon_redemption ALTER COLUMN id SET NOT NULL;
ALTER TABLE coupon_redemption ADD PRIMARY KEY (id);

-- Paso 6: Recrear índices si los había
-- Los índices se recrearán automáticamente por JPA al iniciar la app

COMMIT;

-- Verificación
SELECT
    column_name,
    data_type,
    character_maximum_length,
    is_nullable
FROM information_schema.columns
WHERE table_name = 'coupon_redemption' AND column_name = 'id';

-- Verificar que todos los IDs tienen 10 caracteres
SELECT COUNT(*) as total_registros,
       COUNT(CASE WHEN LENGTH(id) = 10 THEN 1 END) as ids_correctos,
       COUNT(CASE WHEN LENGTH(id) != 10 THEN 1 END) as ids_incorrectos
FROM coupon_redemption;

-- Ver ejemplos de los nuevos IDs
SELECT id, coupon_id, redeemed_at
FROM coupon_redemption
ORDER BY redeemed_at DESC
LIMIT 5;

