## Copilot instructions for SGEU (Spring Boot — Java 21)

Concise, actionable guidance for AI coding agents working on this repository.

### Arquitectura y componentes principales
- **Tipo de proyecto:** Aplicación web Spring Boot (Java 21) con Thymeleaf, Spring Data JPA, Spring Security (configuración mínima) y ZXing para QR. Ver `pom.xml` y `SgeuApplication.java`.
- **Estructura:**
  - `controllers/`: Controladores MVC (ej: `LoginController.java`, `VehiculoController.java`).
  - `models/`: Entidades JPA y enums (`Empleado`, `Persona`, `Vehiculo`, `Rol`).
  - `repositories/`: Repositorios Spring Data JPA.
  - `services/` y `servicesimpl/`: Interfaces y lógica de negocio. Usar siempre el service layer para acceso a datos y reglas de negocio.
  - `config/`: Configuración de seguridad, sesiones e interceptores (`SeguridadConfig.java`, `SessionInterceptor.java`, `webconfig.java`).
  - `resources/templates/`: Vistas Thymeleaf. Usa `layout.html` y fragmentos (`header.html`, `footer.html`).

### Flujos y comandos clave
- **Ejecutar localmente:**
  - `./mvnw.cmd spring-boot:run` (Windows)
- **Tests:**
  - `./mvnw.cmd test`
- **Base de datos:**
  - Configurada en `application.yml` (MySQL remoto por defecto). Para desarrollo offline, cambiar a H2 o una instancia local.

### Patrones y convenciones específicas
- **Sesiones:**
  - `SessionInterceptor` (registrado en `webconfig`) fuerza login en rutas protegidas. Asegura que los atributos de sesión (`user`, `rol`, `nombreCompleto`, `estacionamiento`) estén presentes tras login (`LoginController`).
- **Seguridad:**
  - CSRF deshabilitado y todas las rutas permitidas por defecto (`SeguridadConfig`). El login usa sesión HTTP, no tokens.
- **Roles:**
  - Usa enums (`Rol.Administrador`, etc.), no códigos numéricos.
- **Contraseñas:**
  - El seed usa texto plano (ver `SgeuApplication.seedData`). En producción, usar el `PasswordEncoder` (`BCryptPasswordEncoder`).
- **Vistas:**
  - Thymeleaf con layout dialect. Modificar fragmentos en `templates/` para cambios globales de UI.
- **QR:**
  - Lógica QR basada en ZXing (`QRController`, `QRImageController`).

### Ejemplos y referencias
- **Login y sesión:** `LoginController.java` (setea sesión), `SessionInterceptor.java` (valida sesión)
- **Seed de datos:** `SgeuApplication.java` (CommandLineRunner)
- **Seguridad:** `SeguridadConfig.java` (PasswordEncoder, filter chain)

### Cambios típicos y ubicaciones
- **Nuevos endpoints:** `controllers/` + vista en `templates/`
- **Nuevos campos de dominio:** `models/` + mapeo en repositorio y lógica en `servicesimpl/`
- **Cambios de sesión/autorización:** `config/SessionInterceptor.java`, `config/SeguridadConfig.java`

### Debug y buenas prácticas
- **Logs:** SQL de JPA habilitado (`spring.jpa.show-sql: true`). Usa `System.out.println` para trazas rápidas.
- **Redirección a login:** Si ocurre, revisa `SessionInterceptor.preHandle` y atributos de sesión en `LoginController`.

### No hacer
- No modificar credenciales productivas en `application.yml`. Usa perfiles (`application-dev.yml`) si es necesario.

¿Faltan ejemplos, flujos o convenciones? Indica qué sección ampliar o qué dudas tienes para iterar y mejorar estas instrucciones.
