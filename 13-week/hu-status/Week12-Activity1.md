# TeleMed IA — Security in Distributed Systems & Threat Modeling

**Unit 3 · Weekly + Planning · Corte 3 | MVP 3**  
**Proyecto:** TeleMed IA — Grupo 2 · Sistemas Distribuidos 2026-B  
**Fecha de revisión:** 10 de noviembre de 2026  
**Organización GitHub:** [code-corhuila](https://github.com/code-corhuila)  
**Ramas examinadas:** `develop` (código y despliegue) y `main` (documentación).  
**Alcance:** los 32 repositorios accesibles asociados con TeleMed IA; lectura de sus README, contratos, archivos de seguridad, pruebas y workflows relevantes. **No se ejecutaron aplicaciones, pruebas ni pipelines en esta revisión.**

> **Objetivo de la semana.** Proteger el sistema: autenticar en el borde, **validar los tokens y autorizar dentro de cada servicio**, asegurar llamadas entre servicios, almacenar contraseñas y credenciales de forma segura, y validar entradas. En la sesión de planificación, construir un **modelo de amenazas STRIDE** y un backlog de seguridad priorizado y verificable para MVP 3.

## 1. Resumen ejecutivo — estado real

| Requisito | Estado comprobado | Qué hay en TeleMed IA |
|---|---|---|
| Autenticación en el borde | **Parcial** | El gateway NGINX comprueba la **presencia** de `Authorization` en rutas protegidas, aplica CORS y límite por IP. **No verifica la firma JWT**; lo hacen los servicios. `/api/v1/auth` admite solicitudes públicas para iniciar sesión y recuperar contraseña. |
| Validación JWT en cada servicio | **Implementado en los servicios principales; endurecimiento pendiente** | Verificadores RS256 en Java, Python y Go. No todos comprueban el mismo conjunto de *claims* (especialmente `iss`) o restringen la familia de algoritmo de igual modo. |
| Autorización por rol y por recurso | **Implementado en múltiples servicios; no universalmente cerrado** | `ADMIN`, `PATIENT`, `PROFESSIONAL` y, para M2M, `SERVICE`; controles de propietario de citas, conversaciones y documentos; quedan hallazgos puntuales de STRIDE. |
| Servicio a servicio | **Implementado con JWT de servicio en varios flujos; parcial en el conjunto** | Identity emite JWT RS256 con `role=SERVICE`, `sub=clientId` y vida de 10 minutos. Worker, Document, Notifications, Professional y Workflow usan o documentan este patrón. Sin mTLS generalizado ni aislamiento lateral estricto acreditado. |
| Hash de contraseñas y secretos | **Implementado para contraseñas y credenciales registradas; gestión operativa parcial** | BCrypt para contraseñas de usuarios y hashes de `SERVICE_CLIENTS`; SHA-256 para ciertos tokens persistidos; variables de entorno para secretos. Falta verificar un gestor central de secretos y rotaciones por ambiente. |
| Validación de entradas | **Implementado en varias APIs** | Jakarta Bean Validation, Spring Validation, DTOs, FastAPI/Pydantic, restricciones e índices de DB, UUID e `Idempotency-Key`; revisar uniformidad y orden autorización/validación. |
| STRIDE y límites de confianza | **Documentado** | Modelo central y análisis específicos de Internet→Gateway→Identity, Worker→Document y servicio médico→DB. |
| Backlog de seguridad | **Existe, con issues abiertos** | Historias con prioridad, riesgo y criterios comprobables; destacan #149, #150, #152, #153 y #154. |
| Seguridad obligatoria en CI | **Parcial** | Hay builds, pruebas y verificaciones de BD; en los workflows principales revisados **no se identificó un pipeline homogéneo y obligatorio de SAST + escaneo CVE + secret scanning**. |
| HTTPS/mTLS y cifrado en reposo | **Pendiente de comprobación de despliegue final** | La política lo exige o recomienda, pero los archivos de desarrollo y los modelos STRIDE todavía documentan riesgos de transporte, red interna y cifrado. |

**Criterio:** *Implementado* significa que se observó código o prueba correspondiente, no que el despliegue en producción esté certificado. *Documentado* significa que existe decisión o diseño; no implica que esté desplegado.

## 2. Arquitectura y fronteras de confianza

```text
[TB1] Internet / navegador no confiable
    │ Angular shell y portales
    │ Authorization: Bearer <access_token>
    │ X-Correlation-Id
    ▼
[TB2] API Gateway — NGINX :8000
    ├─ rutas, CORS, rate limit por IP, cabeceras y errores
    ├─ exige presencia de Authorization en rutas protegidas
    └─ NO verifica criptográficamente el JWT
    │
    ├── /api/v1/auth/** ────────► Identity & Access API
    ├── /api/v1/patients/** ────► Patient Management API
    ├── /api/v1/professionals/** ► Professional Management API
    ├── /api/v1/agent/** ──────► Intelligent Agent API
    ├── /api/v1/appointments/** ► Appointment Scheduling API
    ├── /api/v1/consultations/**► Medical Consultation API
    ├── /api/v1/notifications/**► Notifications API
    ├── /api/v1/documents/** ──► Document Generation API
    └── /api/v1/sagas/** ──────► Workflow API
               │
[TB3] Cada API vuelve a autenticar y autorizar por sí misma
               │
[TB4] API ↔ API / Workflow / Worker: llamadas internas con SERVICE token
               │    (algunos flujos delegados conservan token de usuario)
[TB5] API ↔ PostgreSQL/MongoDB: cuentas y roles propios; separación por dominio
[TB6] Worker ↔ RabbitMQ ↔ consumidores: eventos y tareas asíncronas
[TB7] API ↔ proveedores externos / RustFS / modelos IA: datos y secretos externos
```

Cada flecha entre actores, servicios, bases y proveedores es un **límite de confianza**. Estar dentro de la red Docker `platform` **no equivale a estar autorizado**.

**Evidencia:** [Gateway README](https://github.com/code-corhuila/telemed-ia-api-gateway/blob/develop/README.md) · [NGINX `10-security.conf`](https://github.com/code-corhuila/telemed-ia-api-gateway/blob/develop/nginx/conf.d/10-security.conf) · [ruta pública de Identity](https://github.com/code-corhuila/telemed-ia-api-gateway/blob/develop/nginx/routes/identity.conf) · [ADR-022](https://github.com/code-corhuila/telemed-ia-docs/blob/main/05-architecture/decisions/records/ADR-022-service-to-service-authentication.md).

## 3. Implementación de los requisitos de Weekly

### 3.1 Autenticación en el borde (Gateway + Identity)

1. El **shell Angular** administra la sesión y adjunta el token Bearer y `X-Correlation-Id` a peticiones HTTP. Los portales remotos que reutilizan su `HttpClient` heredan este comportamiento; sus guardas son de interfaz, no la autorización final.
2. El **Gateway NGINX** concentra la entrada externa, reenvía al microservicio de destino, limita peticiones por IP (`20r/s`; la ruta de auth usa `burst=10`), controla CORS y devuelve errores normalizados. Para rutas protegidas, rechaza una petición sin cabecera `Authorization`.
3. **Identity & Access** maneja registro, login, refresh, logout, recuperación de contraseña y administración de cuentas. Tras validar las credenciales emite un JWT de acceso RS256; el cliente lo presenta en peticiones posteriores.
4. El **Gateway no verifica** firma, expiración ni rol. Cada API debe hacerlo también para proteger llamadas que no pasan por el Gateway.

**Archivos:** [Gateway `10-security.conf`](https://github.com/code-corhuila/telemed-ia-api-gateway/blob/develop/nginx/conf.d/10-security.conf), [Gateway `identity.conf`](https://github.com/code-corhuila/telemed-ia-api-gateway/blob/develop/nginx/routes/identity.conf), [Front README](https://github.com/code-corhuila/telemed-ia-front/blob/develop/README.md), [Identity README](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/blob/develop/README.md).

**Precisión:** esta solución implementa un *choke point* de **enrutamiento y filtrado inicial**, no un punto que autentique por completo a todos los usuarios. Por eso la seguridad real se decide en las APIs.

### 3.2 Verificación de JWT y autorización en cada servicio

| Servicio | Evidencia de implementación | Regla de autorización destacada |
|---|---|---|
| **Identity & Access** | [`JwtAccessTokenVerifier.java`](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/blob/develop/identity-and-access-adapters/src/main/java/com/telemed/identityaccess/adapter/out/security/JwtAccessTokenVerifier.java) | Verifica RS256, firma, `iss`, `sub`, `exp`, `role`, `jti`; las rutas administrativas exigen `ADMIN`. |
| **Patient Management** | [`Rs256Verifier.java`](https://github.com/code-corhuila/telemed-ia-patient-management-api/blob/develop/patient-adapters/src/main/java/com/telemedai/patient/infrastructure/config/security/Rs256Verifier.java) · [`PatientControllerAuthorizationIT`](https://github.com/code-corhuila/telemed-ia-patient-management-api/blob/develop/patient-app/src/test/java/com/telemedai/patient/infrastructure/adapters/in/rest/PatientControllerAuthorizationIT.java) | Endpoints `/api/patients/me` permiten `PATIENT`, no `ADMIN` ni `PROFESSIONAL`; usuario desde `sub`. |
| **Professional Management** | [README — Authentication](https://github.com/code-corhuila/telemed-ia-professional-management-api/blob/develop/README.md) | Registro de profesional: `ADMIN`; compensación por `SERVICE` de workflow; rechaza otro `sub` o acción. |
| **Intelligent Agent** | [`SecurityConfiguration.java`](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/blob/develop/intelligent-agent-app/src/main/java/com/telemed/intelligentagent/app/config/SecurityConfiguration.java) | Spring Resource Server con RS256 e `iss`: `/api/v1/agent/**` para paciente y resúmenes correspondientes para profesional; `denyAll` al resto. |
| **Appointment Scheduling** | [`auth.py`](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-api/blob/develop/src/appointment/adapter/inbound/http/auth.py) · [`test_ownership.py`](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-api/blob/develop/tests/adapter/inbound/http/test_ownership.py) | PyJWT fija RS256; FastAPI comprueba roles; pruebas de citas propias y ajenas evitan IDOR. |
| **Medical Consultation** | [`jwt.go`](https://github.com/code-corhuila/telemed-ia-medical-consultation-api/blob/develop/internal/infrastructure/adapters/inbound/http/jwt.go) | Middleware Go requiere Bearer y firma RSA y coloca `sub`/`role` en el contexto; la autorización de recursos depende de handlers y casos de uso. |
| **Notifications** | [README — Authentication](https://github.com/code-corhuila/telemed-ia-notifications-api/blob/develop/README.md) · [`JwtAccessTokenVerifier.java`](https://github.com/code-corhuila/telemed-ia-notifications-api/blob/develop/notifications-adapters/src/main/java/com/telemed/notifications/adapter/out/security/JwtAccessTokenVerifier.java) | Inbox siempre corresponde al `sub` del usuario; token `SERVICE` no habilita la bandeja de usuario. |
| **Document Generation** | [`SecurityConfiguration.java`](https://github.com/code-corhuila/telemed-ia-document-generation-api/blob/develop/document-generation-app/src/main/java/co/edu/corhuila/telemedia/documentgeneration/app/SecurityConfiguration.java) · [`DocumentQueryService.java`](https://github.com/code-corhuila/telemed-ia-document-generation-api/blob/develop/document-generation-core/src/main/java/co/edu/corhuila/telemedia/documentgeneration/application/usecase/DocumentQueryService.java) | El paciente solo lista/consulta documentos cuyo `patientId` corresponde a su token; la generación restringe `SERVICE` a clientes permitidos. |
| **Workflow** | [README](https://github.com/code-corhuila/telemed-ia-workflow/blob/develop/README.md) | Saga de alta profesional requiere `ADMIN`; sus llamadas internas usan **token propio**, no el Bearer del administrador. |

**HTTP esperado:** `401` = falta o falla autenticación; `403` = identidad válida, pero acción no permitida; para acceso a registros ajenos, los servicios deben aplicar controles de propiedad y el código de respuesta previsto en su contrato.

**Por endurecer:** algunos verificadores propios no comprueban actualmente `iss` o no exigen explícitamente `alg == RS256`. Por ejemplo, el verificador Java de Patient valida RS256/firma/exp/sub pero no `iss`; el middleware Go de Consultation acepta la **familia RSA** a través de `SigningMethodRSA` en vez de restringir exactamente RS256. Definir y probar un contrato uniforme de token para todas las tecnologías. No asumir que solo verificar la firma significa tener autorización completa.

### 3.3 Comunicación segura entre servicios — Zero Trust

**Patrón implementado (ADR-022):**

```text
Servicio llamador (worker / workflow / notifications / document / professional)
  └─ clientId + clientSecret, por configuración protegida
      └─ POST /internal/service-token en Identity & Access
          └─ Identity compara secret contra hash BCrypt registrado en SERVICE_CLIENTS
              └─ emite JWT RS256 (sub = clientId, role = SERVICE, ~10 min)
                  └─ llamador usa Authorization: Bearer <service_token>
                      └─ API destino valida firma/exp/claims
                          └─ API destino comprueba sub permitido + acción permitida
```

- **Identity:** [`ServiceTokenController.java`](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/blob/develop/identity-and-access-adapters/src/main/java/com/telemed/identityaccess/adapter/in/http/ServiceTokenController.java), [`ServiceTokenConfiguration.java`](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/blob/develop/identity-and-access-app/src/main/java/com/telemed/identityaccess/app/config/ServiceTokenConfiguration.java), [`ConfiguredServiceClients.java`](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/blob/develop/identity-and-access-adapters/src/main/java/com/telemed/identityaccess/adapter/out/security/ConfiguredServiceClients.java).
- **Workflow:** crea cuenta inactiva, crea perfil, activa cuenta; usa servicio `workflow` para participantes y aplica compensación si un paso falla. [README Workflow](https://github.com/code-corhuila/telemed-ia-workflow/blob/develop/README.md).
- **Worker:** consume eventos de RabbitMQ y solicita token para iniciar generación PDF. [README Worker](https://github.com/code-corhuila/telemed-ia-worker/blob/develop/README.md).
- **Notifications:** solicita token `notifications` para consultar contactos a Identity; el cliente renueva su token. [README Notifications](https://github.com/code-corhuila/telemed-ia-notifications-api/blob/develop/README.md).
- **Document Generation:** tiene cliente para pedir y reutilizar token de servicio al leer el resumen médico. [`IdentityServiceTokens.java`](https://github.com/code-corhuila/telemed-ia-document-generation-api/blob/develop/document-generation-adapters/src/main/java/co/edu/corhuila/telemedia/documentgeneration/adapter/out/identity/IdentityServiceTokens.java).

**Límite actual:** no se ha comprobado mTLS desplegado de forma integral. La red `platform`, los puertos no publicados y los tokens reducen exposición, pero un contenedor comprometido todavía puede intentar alcanzar otros servicios internos. Además, en flujos estrictamente ligados a la identidad del paciente, como Agent→Appointments, se observa propagación del contexto/token de usuario; este caso requiere justificar y delimitar explícitamente la delegación, sin convertirla en un token M2M con privilegios excesivos.

### 3.4 Hash de contraseñas, tokens y secretos

- Contraseñas de usuarios: `BCryptPasswordHasher` usa **BCrypt `$2a` con costo 10**; se verifica el hash y se contempla actualización si la configuración lo requiere. **Nunca se guarda la contraseña en texto plano.** [Código](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/blob/develop/identity-and-access-adapters/src/main/java/com/telemed/identityaccess/adapter/out/security/BCryptPasswordHasher.java).
- Clientes de servicio: `SERVICE_CLIENTS` se configura con pares `clientId:hashBCrypt`; el **secreto original** solo pertenece al ambiente del llamador. [Código](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/blob/develop/identity-and-access-adapters/src/main/java/com/telemed/identityaccess/adapter/out/security/ConfiguredServiceClients.java).
- Tokens de refresh y recuperación: los adaptadores de Identity aplican **SHA-256** a los tokens antes de persistirlos; son tokens aleatorios, no contraseñas que deban reemplazar BCrypt. [Repositorio Identity API](https://github.com/code-corhuila/telemed-ia-identity-and-access-api).
- Bases de datos: roles separados por dominio (`reader`/`writer`, con los permisos requeridos), credenciales en variables de entorno y restricciones de DCL; MongoDB dispone de `--auth` y pruebas de rechazo de acceso anónimo. [Identity DB](https://github.com/code-corhuila/telemed-ia-identity-and-access-db) · [Agent DB](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db) · [Infra Mongo](https://github.com/code-corhuila/telemed-ia-infra-mongo).
- **Precisión de términos:** se **hashean contraseñas y secretos usados como verificadores**; los secretos que un proceso necesita recuperar (por ejemplo, contraseña de conexión, clave privada o API key de proveedor) se **protegen en un gestor/variables seguras, no se hashean de forma irreversible**.

### 3.5 Validación de entradas e integridad

- **Spring/Java:** validación de DTOs (`@Valid`, restricciones de campos), `Idempotency-Key`, UUID, reglas de dominio y manejo controlado de errores. [Ejemplo Identity](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/blob/develop/identity-and-access-adapters/src/main/java/com/telemed/identityaccess/adapter/in/http/ServiceTokenController.java).
- **Python/FastAPI:** validación de parámetros y cuerpo, token/rol y pruebas de propiedad en Appointment. [Auth](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-api/blob/develop/src/appointment/adapter/inbound/http/auth.py).
- **PostgreSQL/MongoDB:** claves foráneas, índices y restricciones; MongoDB tiene `$jsonSchema` con `validationLevel: strict`, `validationAction: error`. [Agent DB](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db).
- **Acceso a objetos clínicos:** comprobaciones de propietario contra la identidad del token, no contra un `patientId` confiado del body. [DocumentQueryService](https://github.com/code-corhuila/telemed-ia-document-generation-api/blob/develop/document-generation-core/src/main/java/co/edu/corhuila/telemedia/documentgeneration/application/usecase/DocumentQueryService.java).

**Hallazgo:** en el análisis STRIDE Worker→Document se identificó que una petición de `PATIENT` a generación de documento con body inválido podía devolver `400` antes que `403`. Planificar **autorización antes de divulgar errores detallados de validación** en operaciones protegidas. [Análisis](https://github.com/code-corhuila/telemed-ia-docs/blob/main/05-architecture/security/stride-Sofialjure.md).

## 4. Planning — Threat model (STRIDE) para MVP 3

Los análisis de riesgo y amenazas están documentados en:

- [Modelo central STRIDE](https://github.com/code-corhuila/telemed-ia-docs/blob/main/05-architecture/security-threat-model.md).
- [STRIDE Internet → Gateway → Auth, @JulianaFerro1428](https://github.com/code-corhuila/telemed-ia-docs/blob/main/05-architecture/security/stride-JulianaFerro1428.md).
- [STRIDE Worker → Document, @Sofialjure](https://github.com/code-corhuila/telemed-ia-docs/blob/main/05-architecture/security/stride-Sofialjure.md).
- [STRIDE Medical Service → Medical DB](https://github.com/code-corhuila/telemed-ia-docs/blob/main/05-architecture/security/stride-BondrewdXD.md).

**Método:** para cada frontera, registrar activo/datos, actor, amenaza, probabilidad `L` (1–3), impacto `I` (1–3), puntuación `R = L × I`, control existente, mitigación faltante, dueño y prueba negativa. Un riesgo alto **no** se declara resuelto solo porque exista un ADR.

| STRIDE | Amenaza real o plausible | Frontera / evidencia | Mitigación actual y residual |
|---|---|---|---|
| **S — Spoofing** | Credential stuffing al login, cambiando IP; suplantar servicio `worker` con secreto robado. | Internet→Auth; Worker→Document | BCrypt + rate limit IP; hay contador por cuenta/lock progresivo en `LoginService`. Falta demostrar cierre de criterios y reforzar secreto/aislamiento de M2M. |
| **T — Tampering** | Cambiar `role` del JWT o falsificar `patientId`/`summaryId`. | Cliente→API; Worker→Document | Firma RS256; validaciones cruzadas de propiedad; ampliar pruebas negativas de token alterado y validación previa. |
| **R — Repudiation** | Administrador niega cambio de estado; no se identifica quién disparó una generación. | Admin→Identity; Worker→Document | Logs con `X-Correlation-Id`; falta auditoría de seguridad persistente con `actor sub`, recurso, antes/después. |
| **I — Information disclosure** | Enumerar correos registrados; revelar detalles de `400` a usuario que merecía `403`. | Register; Document | Recovery devuelve respuesta neutra; registro todavía describe `409` revelador; corrección en #153. |
| **D — Denial of service** | Flood distribuido del login, BCrypt costoso; repetición de eventos PDF; abuso del bloqueo. | Gateway→Auth; Worker→Document | Rate limit IP, idempotencia y límites de worker; faltan límites globales y por identidad; bloqueos deben caducar. |
| **E — Elevation of privilege** | Paciente llama ruta de admin; un contenedor interno modifica operaciones sensibles del actuador. | Identity; Document management | `ADMIN` verificado por backend; puerto de administración no expuesto al host, pero el switch interno requiere protección más fuerte. |

## 5. Backlog de seguridad priorizado para MVP 3

**Escala adoptada:** `P0` = debe atenderse para el objetivo de seguridad del corte; `P1` = alta; `P2` = media; `P3` = baja. Priorización basada en `L × I`, exposición, impacto clínico y controles actuales. Los ID `SEC-*` son **propuestas para esta documentación**, no issues que ya se hayan creado.

| Prioridad | Historia / riesgo | Estado y responsable propuesto | Criterio de aceptación verificable |
|---|---|---|---|
| **P0** | **#149**: autenticación de `worker` en generación PDF; secreto robado / movimiento lateral. | Issue [#149](https://github.com/code-corhuila/telemed-ia-docs/issues/149) **abierto** · Identity, Worker, Document, Infra. | Token `SERVICE` con `sub` no permitido → `403` y sin fila; token inválido → `401`; prueba de aislamiento de red; secreto distinto por ambiente; límite de intentos de `clientId`. |
| **P0** | **#152**: credential stuffing de login / bloqueo por cuenta. | [#152](https://github.com/code-corhuila/telemed-ia-docs/issues/152) **abierto**, pero **código de bloqueo existente en `develop`** · Identity API/DB. | 5 fallos/15 min → siguiente `429` incluso desde otra IP; expiración progresiva 1/5/15 min; reset tras login correcto; recovery 202 durante lock; test E2E verde en CI. |
| **P1** | **#153**: enumeración de usuarios por registro. | [#153](https://github.com/code-corhuila/telemed-ia-docs/issues/153) **abierto** · Identity + contrato OpenAPI. | Correo/documento existente o nuevo → respuesta pública indistinguible; no duplica cuenta; pruebas y contrato actualizados. |
| **P1** | **#154**: DoS distribuido de `/api/v1/auth`. | [#154](https://github.com/code-corhuila/telemed-ia-docs/issues/154) **abierto** · Gateway. | Límite **global**, además del límite por IP; ráfaga → `429` sin tocar Identity; otras rutas no se degradan; smoke en CI. |
| **P1** | **#150**: spam/replay contra generación de PDF. | [#150](https://github.com/code-corhuila/telemed-ia-docs/issues/150) **abierto** · Document/Worker. | Umbral por `sub` → `429`, idempotencia → un solo documento, segundo `summaryId` duplicado → `409`; métricas en tablero. |
| **P1** | **SEC-01**: contrato JWT uniforme Java/Python/Go. | Propuesto · dueños de cada API. | Rechazar `alg` distinto de RS256, firma inválida, `exp` vencido, `iss` incorrecto, `sub`/`role` ausentes; cada prueba negativa devuelve `401` sin ejecutar la acción. |
| **P1** | **SEC-02**: puertas de seguridad en CI. | Propuesto · DevOps + todas las APIs. | Escaneo de secretos, dependencias y SAST en cada PR; hallazgos según política bloquean merge; pruebas negativas RBAC/IDOR obligatorias. |
| **P1** | **SEC-03**: gestión del endpoint interno de pausa de generación. | Propuesto · Document + Infra; hallazgo STRIDE del equipo. | Un contenedor sin rol de mantenimiento no puede invocar la operación de pausa; `403` o conexión rechazada en red aislada; prueba en CI. |
| **P1** | **SEC-04**: autorización previa a exposición de detalles de validación. | Propuesto · Document Generation. | `PATIENT` sobre `/api/documents/generate`, incluso con cuerpo inválido, recibe `403` sin detalles de esquema; rol autorizado sigue recibiendo `400` detallado. |
| **P2** | **SEC-05**: auditoría de acciones críticas. | Propuesto · Identity + Document + Observabilidad. | Cada cambio administrativo o generación guarda `actor/sub`, acción, recurso, resultado y correlación; no registra secretos ni PII innecesaria. |
| **P2** | **SEC-06**: refresh-token reuse detection. | Propuesto · Identity API/DB. | Reutilizar un refresh revocado invalida la familia/sesión aplicable y genera evento de seguridad; pruebas de rotación y replay. |
| **P2** | **SEC-07**: transporte y secretos operativos. | Propuesto · Infra/DevOps. | Verificar HTTPS en ingreso, considerar mTLS/segmentación interna, rotación por ambiente, cifrado donde aplique y evidencia de despliegue. |

**Matiz importante del #152:** [`LoginService.java`](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/blob/develop/identity-and-access-core/src/main/java/com/telemed/identityaccess/application/usecase/LoginService.java), [`LoginAttempts.java`](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/blob/develop/identity-and-access-core/src/main/java/com/telemed/identityaccess/domain/model/LoginAttempts.java), [migración `007_create_login_attempts.sql`](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/blob/develop/01_ddl/03_tables/007_create_login_attempts.sql) y [`LoginAccountLockE2ETest.java`](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/blob/develop/identity-and-access-app/src/test/java/com/telemed/identityaccess/LoginAccountLockE2ETest.java) existen. **No se verificó el resultado de una ejecución real de CI** ni el cierre del issue. Por eso la tarea pendiente es confirmar todos los criterios con evidencia, no volver a desarrollar a ciegas el mismo control.

## 6. Seguridad en CI: evidencia vs. meta

### 6.1 Workflows existentes examinados

| Repositorios / alcance | Workflows observados | Qué verifican según configuración |
|---|---|---|
| Identity, Professional, Document, Agent, Notifications, Patient, Workflow, Worker (APIs) | `.github/workflows/ci.yml` | Maven `verify`/`clean verify`, pruebas y compilación; Workflow añade pruebas de su esquema. |
| Medical Consultation API | `.github/workflows/ci.yml` | `go vet`, `go build`, `go test -race`. |
| Gateway | `.github/workflows/ci.yml` | Docker build, NGINX `nginx -t` y smoke. |
| Infra Mongo | `.github/workflows/ci.yml` | Arranque autenticado, rechaza acceso sin credenciales y credenciales erróneas, comprueba privilegios de Agent DB. |
| Bases PostgreSQL y Agent DB | `db-ci.yml` / `ci.yml` | Migraciones, esquemas, permisos y/o reconstrucción según cada repo. |
| Shell y portales federados | `.github/workflows/ci.yml` cuando existe | Build y pruebas de UI propias del repositorio. |

**Ausencia observable:** en los workflows principales de APIs y Gateway examinados no hay pasos explícitos estandarizados para **Gitleaks, SAST y escaneo de CVE** que fallen el build de manera uniforme. El hecho de que `mvn verify` o `go test` existan **no prueba por sí mismo** que esos tres controles estén activos. Puede haber verificaciones locales o externas fuera de estos workflows; no se certificaron en esta inspección.

**Ejemplos:** [CI Identity](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/blob/develop/.github/workflows/ci.yml) · [CI Gateway](https://github.com/code-corhuila/telemed-ia-api-gateway/blob/develop/.github/workflows/ci.yml) · [CI Medical](https://github.com/code-corhuila/telemed-ia-medical-consultation-api/blob/develop/.github/workflows/ci.yml) · [CI Infra Mongo](https://github.com/code-corhuila/telemed-ia-infra-mongo/blob/develop/.github/workflows/ci.yml).

### 6.2 Pipeline de seguridad objetivo (propuesto, NO desplegado)

```text
PR a develop / qa / main
    │
    ├─ Compilación + tests unitarios/integración
    ├─ Pruebas negativas JWT (401), RBAC (403), propiedad/IDOR (403/404)
    ├─ Secret scanning (ej. Gitleaks, incluyendo política para historial)
    ├─ Dependency scanning (CVE y severidad definida)
    ├─ SAST (reglas acordadas por tecnología)
    ├─ Lint/configuración NGINX/Docker/IaC
    └─ QUALITY GATE: sin incumplimientos bloqueantes
             │
         PR aprobable
```

**Política a decidir por el equipo:** qué severidades de CVE/SAST bloquean, cómo se registran falsos positivos y excepciones con fecha, qué ramas son obligatorias y qué jobs deben ser **required status checks**. No incluir un token real o un secreto real en pruebas de fuga: usar valores sintéticos.

### 6.3 Pruebas negativas concretas para incorporar o conservar

| Prueba | Entrada | Resultado esperado |
|---|---|---|
| Sin autenticación | `GET /api/patients/me` sin Bearer | `401` y envelope común. |
| Token manipulado | JWT `alg=none`, expirado o firma de otra clave | `401`. |
| Role escalation | `PATIENT` sobre ruta administrativa de Identity | `403`. |
| IDOR en citas | Paciente A consulta/modifica cita de paciente B | `403` o código documentado, sin devolver datos. |
| IDOR en documentos | Paciente A consulta documento de paciente B | Acceso denegado, sin PDF ni metadatos sensibles. |
| Servicio falso | `SERVICE`, `sub=notifications`, llama endpoint reservado a `worker` | `403` y sin efectos persistidos. |
| Credential stuffing | 5 fallos mismos email / IP distintas; sexto con clave correcta | `429`, sin token, `Retry-After`. |
| Validación | DTO sin campo obligatorio, con rol autorizado | `400` con detalles controlados. |
| Prioridad autorización | Rol sin permiso + DTO inválido para PDF | `403` genérico, no detalles de campos. |
| Inyección | Campos con payload SQL/NoSQL construido para test | Rechazo o manejo como valor; no altera consultas ni datos no autorizados. |

**Pruebas existentes relevantes:** [Paciente 401/403](https://github.com/code-corhuila/telemed-ia-patient-management-api/blob/develop/patient-app/src/test/java/com/telemedai/patient/infrastructure/adapters/in/rest/PatientControllerAuthorizationIT.java), [Appointments ownership](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-api/blob/develop/tests/adapter/inbound/http/test_ownership.py), [AgentSecurityTest](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/blob/develop/intelligent-agent-app/src/test/java/com/telemed/intelligentagent/AgentSecurityTest.java), [LoginAccountLockE2ETest](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/blob/develop/identity-and-access-app/src/test/java/com/telemed/identityaccess/LoginAccountLockE2ETest.java).

Comandos de referencia **para ejecutar en cada repositorio en un entorno con dependencias configuradas**:

```bash
# APIs Java
mvn -B verify

# API Go: Medical Consultation
go vet ./...
go test -race -count=1 ./...

# API Python: Appointment Scheduling (instalar dependencias antes)
python -m pytest tests/adapter/inbound/http/test_auth.py tests/adapter/inbound/http/test_ownership.py

# Gateway: requiere Docker y red platform
bash tests/smoke.sh
```

> Esta revisión inspeccionó los archivos en GitHub, **no ejecutó estos comandos** ni confirmó si los pipelines remotos están verdes.

## 7. Inventario de los 32 repositorios revisados

Se agrupan por responsabilidad. Los enlaces llevan al repositorio y permiten revisar código, `README.md` y pruebas desde la rama indicada. Los portales no son la autoridad de seguridad: la protección se verifica en backend.

| Grupo | Repositorios | Relevancia de seguridad |
|---|---|---|
| Gobernanza y base histórica (2) | [`telemed-ia-docs`](https://github.com/code-corhuila/telemed-ia-docs), [`ai-telemedicine-chatbot-platform`](https://github.com/code-corhuila/ai-telemedicine-chatbot-platform) | ADR, política, STRIDE, backlog; el segundo contiene solo README de MVP, sin evidencia comparable de controles productivos. |
| Entrada, orquestación y mensajería (3) | [`telemed-ia-api-gateway`](https://github.com/code-corhuila/telemed-ia-api-gateway), [`telemed-ia-workflow`](https://github.com/code-corhuila/telemed-ia-workflow), [`telemed-ia-worker`](https://github.com/code-corhuila/telemed-ia-worker) | Límite de ingreso, autenticación inicial, tokens M2M, saga y consumidor de eventos. |
| Infraestructura (2) | [`telemed-ia-infra-postgres`](https://github.com/code-corhuila/telemed-ia-infra-postgres), [`telemed-ia-infra-mongo`](https://github.com/code-corhuila/telemed-ia-infra-mongo) | Secretos de entorno, cuentas de DB, privilegios, autenticación Mongo y controles de CI. |
| Shell (1) | [`telemed-ia-front`](https://github.com/code-corhuila/telemed-ia-front) | Sesión, Bearer, correlación y cliente HTTP compartido. |
| Identity & Access (3) | [`-db`](https://github.com/code-corhuila/telemed-ia-identity-and-access-db), [`-api`](https://github.com/code-corhuila/telemed-ia-identity-and-access-api), [`-portal`](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal) | Cuentas, JWT, roles, BCrypt, refresh, login, secretos de servicio. |
| Patient Management (3) | [`-db`](https://github.com/code-corhuila/telemed-ia-patient-management-db), [`-api`](https://github.com/code-corhuila/telemed-ia-patient-management-api), [`-portal`](https://github.com/code-corhuila/telemed-ia-patient-management-portal) | Datos personales; validación token, rol PATIENT y acceso `/me`. |
| Professional Management (3) | [`-db`](https://github.com/code-corhuila/telemed-ia-professional-management-db), [`-api`](https://github.com/code-corhuila/telemed-ia-professional-management-api), [`-portal`](https://github.com/code-corhuila/telemed-ia-professional-management-portal) | Altas administrativas, cuentas profesionales y compensaciones M2M. |
| Intelligent Agent (3) | [`-db`](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db), [`-api`](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api), [`-portal`](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal) | Resúmenes clínicos, JWT, límites de conversación, Mongo validado. |
| Appointment Scheduling (3) | [`-db`](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-db), [`-api`](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-api), [`-portal`](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-portal) | Citas y autorización por propietario; el portal tiene herramientas de desarrollo/mock no aptas como control productivo. |
| Medical Consultation (3) | [`-db`](https://github.com/code-corhuila/telemed-ia-medical-consultation-db), [`-api`](https://github.com/code-corhuila/telemed-ia-medical-consultation-api), [`-portal`](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal) | Datos clínicos, middleware JWT Go y acceso a resúmenes. |
| Notifications (3) | [`-db`](https://github.com/code-corhuila/telemed-ia-notifications-db), [`-api`](https://github.com/code-corhuila/telemed-ia-notifications-api), [`-portal`](https://github.com/code-corhuila/telemed-ia-notifications-portal) | Inbox privada, entrega de mensajes, RabbitMQ y llamada M2M a Identity. |
| Document Generation (3) | [`-db`](https://github.com/code-corhuila/telemed-ia-document-generation-db), [`-api`](https://github.com/code-corhuila/telemed-ia-document-generation-api), [`-portal`](https://github.com/code-corhuila/telemed-ia-document-generation-portal) | PDF clínicos, permiso por paciente, generación con servicio autorizado. |

**Total: 2 + 3 + 2 + 1 + (8 × 3) = 32 repositorios.**

## 8. Criterios de terminación de la actividad — MVP 3

- [x] Explicada la separación entre autenticación de borde y autorización dentro de cada API.
- [x] Identificadas implementaciones reales de JWT, RBAC, propiedad de datos y service tokens con rutas de archivo.
- [x] Identificado hash BCrypt de contraseñas y credenciales de servicio.
- [x] Localizadas reglas de validación de entradas y controles de bases de datos.
- [x] Localizados STRIDE y límites de confianza del sistema.
- [x] Relacionados riesgos con historias de seguridad y criterios de aceptación.
- [ ] Confirmar ejecución **exitosa** de las pruebas de seguridad del corte en CI (enlaces a runs).
- [ ] Resolver o aceptar formalmente riesgos de issues #149/#150/#152/#153/#154; no inferir cierre por tener código o una prueba.
- [ ] Implantar y exigir en PR los escaneos acordados de secretos, dependencias y SAST.
- [ ] Completar hardening: claims JWT uniformes, separación de red/mTLS si corresponde, auditoría y endpoints de administración.
- [ ] Documentar evidencias de despliegue seguro y responsables por historia antes de entregar MVP 3.

## 9. Conclusión

TeleMed IA **no deja toda su seguridad en el Gateway**. El Gateway cumple el papel de entrada y filtrado, mientras que las APIs validan sus propios tokens, comprueban roles y, en casos críticos, controlan que el usuario sea propietario del recurso. Identity centraliza emisión de JWT y hash BCrypt; los servicios emplean tokens M2M cortos bajo el ADR-022; bases y MongoDB aplican cuentas, privilegios y validadores.

El enfoque de **Zero Trust y defense in depth está parcialmente implementado**: las comprobaciones en cada API y los service tokens están presentes, pero no se puede dar por cerrada la segmentación interna, mTLS, la uniformidad de validadores JWT, los riesgos STRIDE abiertos ni los gates obligatorios de seguridad en CI. El trabajo del MVP 3 consiste en **convertir esos riesgos identificados en pruebas y controles demostrables**, no en afirmar que un escaneo final resuelve por sí solo la seguridad del sistema.

---

### Fuentes rectoras

- [Política de seguridad](https://github.com/code-corhuila/telemed-ia-docs/blob/main/00-governance/security-policy.md).
- [Reglas técnicas de seguridad](https://github.com/code-corhuila/telemed-ia-docs/blob/main/00-governance/security-rules.md).
- [ADR-007 — Authentication & Authorization](https://github.com/code-corhuila/telemed-ia-docs/blob/main/05-architecture/decisions/records/ADR-007-authentication-authorization.md).
- [ADR-022 — Service-to-Service Authentication](https://github.com/code-corhuila/telemed-ia-docs/blob/main/05-architecture/decisions/records/ADR-022-service-to-service-authentication.md).
- [STRIDE del proyecto](https://github.com/code-corhuila/telemed-ia-docs/tree/main/05-architecture/security).
- [Backlog de issues de seguridad](https://github.com/code-corhuila/telemed-ia-docs/issues?q=label%3Asecurity).

*Documento de revisión de repositorios; no reemplaza un pentest, auditoría formal o evidencia de pipeline ejecutado.*
