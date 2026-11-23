# ms-auth

Microservicio de autenticacion y usuarios de Silos Monitoreo. Puerto 8081, base `silos_auth_db`.

## Ejecutar

1. Desde la raiz del repo: `docker compose up -d`
2. En esta carpeta: `.\mvnw spring-boot:run`
3. Swagger: http://localhost:8081/swagger-ui.html
4. Pruebas: `.\mvnw test`

Variables de entorno opcionales: `DB_PASSWORD`, `JWT_SECRET` (minimo 32 caracteres; los 3 microservicios deben usar el mismo).

## Usuarios de prueba

| Correo | Clave | Rol |
|---|---|---|
| operador@silos.com | operador123 | OPERADOR |
| admin@silos.com | admin123 | ADMINISTRADOR |
| jefe@silos.com | jefe123 | JEFATURA |

## Endpoints

| Metodo | Ruta | Acceso | Descripcion |
|---|---|---|---|
| POST | /api/auth/register | Publico | Crea cuenta y devuelve token (201) |
| POST | /api/auth/login | Publico | Inicia sesion y devuelve token JWT |
| GET | /api/auth/me | Autenticado | Perfil del usuario del token |
| GET | /api/usuarios | ADMINISTRADOR | Lista usuarios |
| PUT | /api/usuarios/{id}/rol | ADMINISTRADOR | Cambia el rol |
| DELETE | /api/usuarios/{id} | ADMINISTRADOR | Desactiva (baja logica) |
| PUT | /api/usuarios/{id}/activar | ADMINISTRADOR | Reactiva |

## Formato de error

Todos los errores devuelven JSON con timestamp, status, error, message y path. Codigos usados: 400 validacion, 401 sin token o credenciales malas, 403 sin permisos, 404 no existe, 409 conflicto.

## Pruebas

24 pruebas unitarias: AuthServiceTest, UsuarioServiceTest (Mockito sobre el repositorio) y JwtServiceTest. Coleccion Postman en `postman/ms-auth.postman_collection.json`.