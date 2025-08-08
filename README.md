# 💬 ForoHub API – Gestión de Tópicos, Usuarios y Respuestas (Beta 0.1)

¡Bienvenido a **ForoHub API**!
Una API REST segura y moderna para foros de discusión, construida con **Java 21+**, **Spring Boot** y **MySQL**. Soporta **autenticación JWT**, **paginación**, **validaciones**, **migraciones con Flyway** y CRUD completo de **tópicos**, **usuarios** y **respuestas**.

---

## 🚀 ¿Qué es ForoHub API?

Plataforma backend para manejar:

* **Tópicos** (título, mensaje, autor, curso, fecha y estado)
* **Usuarios** (login, roles)
* **Respuestas** (mensaje, autor, tópico)

Incluye **login con JWT** y control de acceso a todos los endpoints del foro.

---

## ✨ Características

* **Autenticación y autorización**
  Login en `/login` → **JWT**; protección de rutas con **filtro** y **roles** (`USER`, `ADMIN`).

* **CRUD completo**
  Crear, listar (con filtros y paginación), ver detalle, actualizar y eliminar **tópicos** y **respuestas**.
  CRUD de **usuarios** (crear/editar/borrar solo **ADMIN**).

* **Validaciones robustas**
  `@Valid` + anotaciones de Jakarta Validation en todos los DTOs.

* **Paginación, orden y filtros**
  Listado de tópicos paginado, ordenado por fecha y filtrable por **curso** y **año**.

* **Migraciones con Flyway**
  Esquema versionado: `topicos`, `usuarios`, `respuestas`, índices y restricciones.

* **Listo para producción**
  Estructura limpia por capas (entidades, repos, DTOs, controladores) y configuración de seguridad centralizada.

---

## 🛠️ Tecnologías

* Java 21+
* Spring Boot (Web, Validation, Security, Data JPA)
* JWT (Auth0 java-jwt)
* MySQL 8 + Flyway
* Lombok, DevTools
* Maven
* Insomnia / Postman para pruebas

---

## ⚡ Quick Start

1. **Clonar**

```bash
git clone https://github.com/tu-usuario/foru mhub-api.git
cd forumhub-api
```

2. **Configurar variables** (`src/main/resources/application.properties`)

```properties
server.port=8080

# DB
spring.datasource.url=jdbc:mysql://localhost:3306/demo_db?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password

# JPA / Flyway
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true

# JWT
jwt.secret=pon-una-clave-larga-y-aleatoria
jwt.expiration=3600000
```

> Crea la base `demo_db` en MySQL; **Flyway** aplicará automáticamente todas las migraciones al arrancar.

3. **Compilar y ejecutar**

```bash
mvn clean install
mvn spring-boot:run
```

---

## 🔐 Autenticación

### 1) Obtener token

```
POST http://localhost:8080/login
Content-Type: application/json

{
  "username": "joji",
  "password": "123456"
}
```

**Respuesta**

```json
{ "token": "eyJhbGciOi..." }
```

### 2) Usar el token

En cada request protegido agrega el header:

```
Authorization: Bearer eyJhbGciOi...
```

---

## 📚 Endpoints

### Auth

* `POST /login` → genera **JWT**.

### Tópicos (`/topicos` o `/tópicos`)  *(requiere JWT)*

* `POST /topicos` – crear tópico
  Body: `titulo`, `mensaje`, `autor`, `curso` (valida duplicados por `titulo+mensaje`)
* `GET /topicos` – listar (paginado)
  Query: `page`, `size`, `sort`, **`curso`**, **`anio`**
* `GET /topicos/{id}` – detalle
* `PUT /topicos/{id}` – actualizar (mismas reglas del POST + evita duplicados)
* `DELETE /topicos/{id}` – eliminar

### Respuestas (`/respuestas`)  *(requiere JWT)*

* `POST /respuestas` – crear respuesta
  Body: `mensaje`, `topicoId` (el **usuario** se toma del token)
* `GET /respuestas` – listar (paginado)
  Query opcional: `topicoId`, `usuarioId`
* `GET /respuestas/{id}` – detalle
* `PUT /respuestas/{id}` – actualizar mensaje
* `DELETE /respuestas/{id}` – eliminar

### Usuarios (`/usuarios`)

* `GET /usuarios` – listar (JWT `USER`/`ADMIN`)
* `GET /usuarios/{id}` – detalle (JWT `USER`/`ADMIN`)
* `POST /usuarios` – **crear** (solo **ADMIN**)
* `PUT /usuarios/{id}` – **actualizar** (solo **ADMIN**)
* `DELETE /usuarios/{id}` – **eliminar** (solo **ADMIN**)

---

## 🗂️ Estructura del Proyecto (resumen)

```
src/main/java/com/joji/demo
├─ api/                 # Controladores y DTOs
│  ├─ TopicoController.java
│  ├─ RespuestaController.java
│  ├─ UsuarioController.java
│  └─ dto/ (Topico*, Respuesta*, Usuario*)
├─ auth/                # Seguridad y usuarios
│  ├─ LoginController.java
│  ├─ TokenService.java
│  ├─ JwtAuthFilter.java
│  ├─ Usuario.java / UsuarioRepository.java
│  └─ JwtResponse.java / LoginRequest.java
├─ config/
│  └─ SecurityConfig.java
├─ domain/              # Entidades y repositorios
│  ├─ Topico.java / TopicoRepository.java / EstadoTopico.java
│  ├─ Respuesta.java / RespuestaRepository.java
│  └─ ...
└─ resources/
   ├─ application.properties
   └─ db/migration/
      ├─ V1__init.sql
      ├─ V2__tabla_topicos.sql
      ├─ V3__topicos_add_estado.sql
      ├─ V4__usuarios.sql
      └─ V5__respuestas.sql
```

---

## ✅ Reglas de negocio clave

* **Todos los campos obligatorios** en creación/actualización de tópicos.
* **Sin duplicados** de tópicos por `titulo + mensaje` (validación y restricción UNIQUE).
* **Estado** de tópico por defecto: `ABIERTO`.
* **Respuestas** siempre asocian el **autor** desde el token.
* **Control de acceso**:

  * `USER` y `ADMIN` pueden usar el foro.
  * Acciones sobre **usuarios** restringidas a `ADMIN` (configurable).

---

## 🧪 Pruebas rápidas (Insomnia/Postman)

1. `POST /login` → copiar token
2. `GET /topicos` → 200 (con header `Authorization`)
3. `POST /topicos` → 201
4. `PUT /topicos/{id}` → 200
5. `DELETE /topicos/{id}` → 204
6. `POST /respuestas` → 201
7. `GET /respuestas?topicoId=1` → 200

> Sin token ⇒ **401**. Token inválido ⇒ **401**. Sin rol suficiente ⇒ **403**.

---

## 🧭 Roadmap

* 🔒 Contraseñas **BCrypt** (+ migración de usuario inicial)
* 📘 Swagger/OpenAPI (`/swagger-ui`)
* 🔁 Refresh tokens
* 🌐 CORS por perfiles `dev`/`prod`
* 🗑️ Soft delete para tópicos/respuestas

---

## 🤝 Contribuir

¡Ideas, issues y PRs son bienvenidos!

* Abre un **Issue** para bugs/mejoras
* Envía un **Pull Request** con tu propuesta

**Contacto:** [yairandrezunig4@gmail.com](mailto:yairandrezunig4@gmail.com)

---

¿Listo para lanzar tu foro? **FórumHub API** te da el backend sólido para hacerlo. 🚀
