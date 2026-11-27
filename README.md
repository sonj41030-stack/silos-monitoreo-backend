# Silos Monitoreo - Backend

Microservicios en Spring Boot para la app Android de monitoreo de silos (DSY1105).

Repositorio de la app: https://github.com/sonj41030-stack/silos-monitoreo-android

## Integrantes

- Edixon Cabriless (sonj41030-stack)
- REEMPLAZAR: nombre y usuario de GitHub del segundo integrante

## Microservicios

| Servicio | Puerto | Base de datos | Responsabilidad |
|---|---|---|---|
| ms-auth | 8081 | silos_auth_db | Registro, login con JWT y gestion de usuarios |
| ms-silos | 8082 | silos_core_db | Granjas y silos (CRUD) y ajuste de nivel |
| ms-movimientos | 8083 | silos_mov_db | Registro e historial de movimientos |

Los tres usan MySQL y validan el mismo JWT (variable `JWT_SECRET`, minimo 32 caracteres).

## Endpoints

### ms-auth (8081)

| Metodo | Ruta | Acceso |
|---|---|---|
| POST | /api/auth/register | Publico |
| POST | /api/auth/login | Publico |
| GET | /api/auth/me | Autenticado |
| GET | /api/usuarios | ADMINISTRADOR |
| PUT | /api/usuarios/{id}/rol | ADMINISTRADOR |
| DELETE | /api/usuarios/{id} | ADMINISTRADOR |
| PUT | /api/usuarios/{id}/activar | ADMINISTRADOR |

### ms-silos (8082)

| Metodo | Ruta | Descripcion |
|---|---|---|
| GET | /api/granjas | Lista granjas con sus silos |
| GET | /api/granjas/{id} | Detalle de una granja |
| GET | /api/silos | Lista silos |
| GET | /api/silos/{id} | Detalle de un silo |
| POST | /api/silos | Crea un silo (ADMINISTRADOR) |
| PUT | /api/silos/{id} | Edita un silo (ADMINISTRADOR) |
| DELETE | /api/silos/{id} | Borrado logico (ADMINISTRADOR) |
| POST | /api/silos/{id}/movimientos | Ajusta el nivel por entrada o salida |

### ms-movimientos (8083)

| Metodo | Ruta | Descripcion |
|---|---|---|
| POST | /api/movimientos | Registra un movimiento |
| GET | /api/movimientos | Historial (filtro opcional `siloId`) |
| GET | /api/movimientos/{id} | Detalle de un movimiento |

Todos los endpoints, salvo register y login, requieren `Authorization: Bearer <token>`.
Swagger de cada servicio: `http://localhost:<puerto>/swagger-ui.html`.

## Usuarios de prueba

| Correo | Clave | Rol |
|---|---|---|
| admin@silos.com | admin123 | ADMINISTRADOR |
| operador@silos.com | operador123 | OPERADOR |
| jefe@silos.com | jefe123 | JEFATURA |

## Pasos para ejecutar

Requisitos: JDK 17 o superior y Docker.

1. Base de datos: `docker compose up -d` (en la raiz del repo).
2. ms-auth: `cd ms-auth` y `.\mvnw spring-boot:run`
3. ms-silos: `cd ms-silos` y `.\gradlew.bat bootRun`
4. ms-movimientos: `cd ms-movimientos` y `.\gradlew.bat bootRun`

Variables opcionales: `DB_PASSWORD` y `JWT_SECRET`. Los valores por defecto son
solo para desarrollo y evaluacion.

## Pruebas

- ms-auth: `.\mvnw test`
- ms-silos y ms-movimientos: `.\gradlew.bat test`

## Ramas

La rama principal es `main`. El trabajo se hizo en ramas `feature/...` y se integro
con Pull Requests.