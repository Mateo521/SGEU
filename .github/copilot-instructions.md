## Copilot instructions for SGEU (Spring Boot — Java 21)

Short, actionable guidance for an AI coding assistant working on this repository.

- Project type: Spring Boot web app (Java 21) que implementa un sistema de gestión de estacionamientos universitarios usando:
  - Thymeleaf templates + thymeleaf-layout-dialect
  - Spring Data JPA para persistencia
  - Spring Security (configuración mínima sin CSRF)
  - ZXing para generación/lectura de QR
  - Ver `pom.xml` y `src/main/java/com/unsl/sgeu/SgeuApplication.java`

- How to run locally (Windows PowerShell): use the included Maven wrapper in repo root

```powershell
./mvnw.cmd spring-boot:run
```

- Tests: run with

```powershell
./mvnw.cmd test
```

- Database: the app expects a MySQL datasource configured in `src/main/resources/application.yml`. Example values are present and point to a remote DB. Treat credentials as sensitive — do not commit different secrets to repo. For offline development, change `spring.datasource.url` to a local DB or use H2 and update `application.yml` appropriately.

- Key code layout and responsibilities
  - `src/main/java/com/unsl/sgeu/` — main package
  - `controllers/` — MVC controllers and request mappings (e.g., `LoginController.java`, `VehiculoController.java`)
  - `models/` — JPA entities and enums (e.g., `Empleado`, `Persona`, `Vehiculo`, `Rol`)
  - `repositories/` — Spring Data JPA repositories
  - `services/` and `servicesimpl/` — service interfaces and implementations; prefer using service layer for business logic and DB access
  - `config/` — app configuration: `SeguridadConfig.java` (SecurityFilterChain + PasswordEncoder), `SessionInterceptor.java`, `webconfig.java` (interceptor registration)
  - `resources/templates/` — Thymeleaf views; `layout.html` + `thymeleaf-layout-dialect` used for layouts

- Important runtime behaviors the agent should respect
  - Session enforcement is implemented via `SessionInterceptor` (registered in `webconfig`). Many routes are redirected to `/login` if `session.getAttribute("user")` is missing. When adding pages or endpoints, asegurarse que estos atributos de sesión están presentes cuando se requieren:
    - `user` - nombre de usuario autenticado
    - `rol` - rol del usuario (ADMINISTRADOR o GUARDIA)
    - `nombreCompleto` - nombre completo del usuario
    - `estacionamiento` - estacionamiento asignado al guardia
    - `usuarioId` - ID del usuario actual
  - Security config actualmente deshabilita CSRF y permite todas las requests (`SeguridadConfig`). Tener cuidado al modificar reglas de auth; el login está implementado en `LoginController` y almacena datos en sesión HTTP.
  - La aplicación inicializa datos semilla en `SgeuApplication.seedData` — modificar la lógica de seed afecta los datos de prueba locales.
  - Los controladores mantienen logs de depuración usando `System.out.println()` para trackear el flujo de la aplicación (ver `PrincipalController`, `LoginController`, `VehiculoController`)

- Conventions and patterns observed
  - Passwords: seed uses plain text (TODO to encrypt). Production code should use `PasswordEncoder` bean (`BCryptPasswordEncoder`) already declared in `SeguridadConfig`.
  - Enum-based roles (e.g., `Rol.Administrador`) are used instead of numeric codes.
  - Thymeleaf + layout dialect for templates; prefer updating fragments in `templates/` (e.g., `header.html`, `footer.html`, `layout.html`).
  - QR code logic uses ZXing libs (`com.google.zxing` in `pom.xml`) — see `QRController` and `QRImageController` for examples.

- Typical changes and where to make them
  - Add REST/Controller endpoints: 
    - Controladores en `controllers/` y plantillas correspondientes en `resources/templates/`
    - Para APIs REST usar `@RestController` + `@RequestMapping("/api/...")` (ver `EstacionamientoController.java`)
    - Para vistas MVC usar `@Controller` + `@GetMapping` + retornar nombre de plantilla (ver `PrincipalController.java`)
  - Add domain fields: 
    - Entidades en `models/` 
    - Repositorios en `repositories/`
    - Interfaces de servicio en `services/`
    - Implementaciones en `servicesimpl/`
  - Modificar manejo de sesión/autorización:
    - Interceptor de sesión: `config/SessionInterceptor.java`
    - Configuración de seguridad: `config/SeguridadConfig.java`
  - Funcionalidad QR:
    - Generación: `QRImageController.java`
    - Lectura: `QRController.java` + `resources/static/js/qr.js`

- Debugging tips specific to this repo
  - Log output: JPA SQL is enabled (`spring.jpa.show-sql: true` in `application.yml`). Use console logs (System.out.println used in several controllers) to trace flow quickly.
  - If a request is redirected to `/login`, check `SessionInterceptor.preHandle` and verify `session` attributes set by `LoginController`.

- Examples to reference when coding
  - Login flow: `LoginController.java` (sets `session` attributes) + `SessionInterceptor.java` (enforces session)
  - Seed data: `SgeuApplication.java` (shows how services/repositories are wired via CommandLineRunner)
  - Security bean: `SeguridadConfig.java` (PasswordEncoder + filter chain)

- Non-goals / things not in scope
  - Avoid changing production DB credentials in `application.yml`; if needed, use environment profiles (`application-dev.yml`) and document changes.

If anything here is unclear or you'd like me to expand sections (examples of common edits, query patterns, or a quick contributor checklist), tell me which parts to clarify and I'll iterate.
