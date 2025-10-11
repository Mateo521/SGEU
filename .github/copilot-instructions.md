## Copilot instructions for SGEU (Spring Boot — Java 21)

Short, actionable guidance for an AI coding assistant working on this repository.

- Project type: Spring Boot web app (Java 21) using Thymeleaf templates, Spring Data JPA, Spring Security (minimal config), and ZXing for QR functionality. See `pom.xml` and `src/main/java/com/unsl/sgeu/SgeuApplication.java`.

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
  - Session enforcement is implemented via `SessionInterceptor` (registered in `webconfig`). Many routes are redirected to `/login` if `session.getAttribute("user")` is missing. When adding pages or endpoints, ensure session attributes (`user`, `rol`, `nombreCompleto`, `estacionamiento`) are provided when required.
  - Security config currently disables CSRF and permits all requests (`SeguridadConfig`). Be careful when modifying auth rules; login is implemented in `LoginController` and stores data in HTTP session.
  - Application seeds data in `SgeuApplication.seedData` — adding or modifying seed logic affects local test data.

- Conventions and patterns observed
  - Passwords: seed uses plain text (TODO to encrypt). Production code should use `PasswordEncoder` bean (`BCryptPasswordEncoder`) already declared in `SeguridadConfig`.
  - Enum-based roles (e.g., `Rol.Administrador`) are used instead of numeric codes.
  - Thymeleaf + layout dialect for templates; prefer updating fragments in `templates/` (e.g., `header.html`, `footer.html`, `layout.html`).
  - QR code logic uses ZXing libs (`com.google.zxing` in `pom.xml`) — see `QRController` and `QRImageController` for examples.

- Typical changes and where to make them
  - Add REST/Controller endpoints: `controllers/` and corresponding templates in `resources/templates/`.
  - Add domain fields: `models/` + database mappings + repository updates in `repositories/` and service methods in `servicesimpl/`.
  - Modify session/authorization behavior: `config/SessionInterceptor.java` and `config/SeguridadConfig.java`.

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
