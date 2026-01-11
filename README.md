# Lealtix Service

## 📊 **NUEVO: Dashboard de Reportes (2026-01-03)**

El backend ahora incluye **endpoints completos para dashboard de negocio** con 7 KPIs:
- ✅ Total de clientes y clientes nuevos por periodo
- ✅ Cupones creados vs redimidos con % de redención
- ✅ Ventas totales y ticket promedio
- ✅ Rendimiento completo por campaña

📖 **Ver documentación**: 
- Guía rápida: `IMPLEMENTACION_RAPIDA.md`
- Documentación técnica: `DASHBOARD_README.md`
- Resumen de cambios: `CAMBIOS_DASHBOARD.md`

🚀 **Para implementar**: Ejecutar `.\ejecutar-migracion-dashboard.ps1`

---

# Lealtix Service

Backend del proyecto **Lealtix**, encargado de gestionar el **pre-registro de usuarios y la generación de invitaciones** para la plataforma.

---

## 📌 Descripción

Este servicio backend está desarrollado en **Java Spring Boot** y tiene como objetivo:

- Recibir datos de pre-registro (nombre y email) desde el frontend Angular.
- Validar que el email no esté registrado previamente.
- Almacenar pre-registros en **PostgreSQL**.
- Generar invitaciones con token único para completar el registro del usuario.
- Mantener el estado de cada pre-registro e invitación (`PENDING`, `INVITED`, `REGISTERED`, etc.).

---

## 🔐 Seguridad y CORS (Actualizado 2026-01-10)

### Endpoints Públicos (No requieren autenticación)
- `POST /api/tenant-payment/create-payment-intent` - Crear intención de pago desde frontend
- `POST /api/preregistro` - Pre-registro de usuarios desde landing page
- `POST /api/stripe/webhook` - Webhook de Stripe (seguridad por firma)
- `OPTIONS /**` - Preflight CORS

### Endpoints Protegidos
- Todos los demás endpoints requieren JWT token
- Respuesta sin autenticación: `401 Unauthorized`

### Configuración CORS
Los orígenes permitidos se configuran en `application.properties`:
```properties
# Local
cors.allowed-origins=http://localhost:4200,http://localhost:4201

# Production
cors.allowed-origins=https://admin.lealtix.com.mx,https://lealtix.com.mx,https://www.lealtix.com.mx
```

### Seguridad del Webhook de Stripe
El webhook **no usa autenticación JWT** porque:
- Es llamado desde servidores de Stripe (no navegadores)
- La seguridad se maneja por **validación de firma** usando `stripe.webhook.secret`
- CSRF está deshabilitado (no es vulnerable a CSRF)

---

## 🛠 Tecnologías

- **Lenguaje:** Java 17+
- **Framework:** Spring Boot 3.x
- **Base de datos:** PostgreSQL
- **ORM:** Spring Data JPA / Hibernate
- **Gestión de dependencias:** Maven
- **Otros:** Lombok (opcional para reducir boilerplate)

---

## ⚡ Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/TuUsuario/lealtix_service.git
cd lealtix_service
