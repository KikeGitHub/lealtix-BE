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
