# Session 1 — Secure Configuration and Feature Flags

## TeleMed-IA — Identity & Access

This document describes how the practices from **Session 1** are implemented in the **TeleMed-IA** project.

The goal is to harden the service configuration before MVP 2 by ensuring that:

- configuration is externalized;
- secrets are never committed to Git;
- required variables are validated at startup;
- secrets are injected securely at runtime;
- repositories are protected with secret scanning;
- new capabilities can be released safely using feature flags.

---

## 1. Environment configuration

TeleMed-IA follows the **12-factor configuration principle**.

The application code remains the same across environments such as:

```text
development
qa
production
```

Only environment variables change.

Configuration must not depend on code such as:

```java
if (env.equals("prod")) {
    // production configuration
}
```

Instead, values are provided externally through environment variables.

Example:

```env
DB_URL=jdbc:postgresql://postgres:5432/identity_access
DB_USERNAME=telemed
DB_PASSWORD=change-me

JWT_PRIVATE_KEY_PATH=/run/secrets/jwt-private-key
JWT_PUBLIC_KEY_PATH=/run/secrets/jwt-public-key

ACCESS_TOKEN_TTL=3600
REFRESH_TOKEN_TTL=604800

FEATURE_PASSWORD_RECOVERY=false
```

---

## 2. `.env.example`

The repository contains a `.env.example` file documenting the configuration required to run the service.

The file contains only placeholders and safe example values.

Example:

```env
DB_URL=
DB_USERNAME=
DB_PASSWORD=

JWT_PRIVATE_KEY_PATH=
JWT_PUBLIC_KEY_PATH=

ACCESS_TOKEN_TTL=3600
REFRESH_TOKEN_TTL=604800

FEATURE_PASSWORD_RECOVERY=false
```

The real `.env` file must never be committed.

The repository must include:

```gitignore
.env
.env.*
!.env.example
```

This allows developers to understand which variables are required without exposing credentials.

---

## 3. Required configuration validation

TeleMed-IA validates critical configuration during application startup.

The service should not start when required configuration is missing or invalid.

For example:

```java
@ConfigurationProperties(prefix = "telemed.security")
@Validated
public record SecurityProperties(

        @NotBlank
        String jwtPublicKeyPath,

        @NotBlank
        String jwtPrivateKeyPath,

        @Min(60)
        long accessTokenTtl,

        @Min(300)
        long refreshTokenTtl
) {
}
```

If a required variable is missing, Spring Boot stops during startup.

This follows the principle:

```text
Invalid configuration
        ↓
Fail immediately
        ↓
Do not accept traffic
```

This prevents failures from appearing later during authentication, registration, token refresh, or password recovery operations.

---

## 4. Secrets management

Sensitive information must never be stored directly in:

```text
source code
application.yml
application.properties
Docker images
Git repositories
logs
```

Examples of secrets in TeleMed-IA include:

- PostgreSQL credentials;
- JWT signing private keys;
- external service credentials;
- future email provider credentials;
- API keys.

The intended flow is:

```text
Secret Store
     ↓
Runtime injection
     ↓
Environment / mounted secret
     ↓
Identity & Access service
```

A secret manager can be used depending on the deployment environment, for example:

```text
HashiCorp Vault
AWS Secrets Manager
Azure Key Vault
GCP Secret Manager
Docker / Kubernetes Secrets
```

The application only receives the secret when the container starts.

---

## 5. JWT keys

TeleMed-IA uses asymmetric JWT signing according to the project security architecture.

The private key must remain protected.

Example runtime configuration:

```env
JWT_PRIVATE_KEY_PATH=/run/secrets/jwt-private-key.pem
JWT_PUBLIC_KEY_PATH=/run/secrets/jwt-public-key.pem
```

The application loads the key from the injected location.

The private key must never appear in:

```text
Git
README files
logs
Dockerfile
docker-compose.yml
CI output
```

The public key may be distributed to services that need to validate access tokens.

---

## 6. Secret rotation

Secrets must have an owner and a rotation process.

Example for TeleMed-IA:

| Secret | Owner | Storage | Rotation |
|---|---|---|---|
| Database password | Identity & Access team | Secret manager | Periodically / after exposure |
| JWT private key | Security / Identity service | Secret manager | Controlled key rotation |
| External API credentials | Owning microservice | Secret manager | According to provider policy |

If a secret is exposed, deleting it from Git is not enough.

The required response is:

```text
1. Revoke or rotate the secret.
2. Replace the exposed credential.
3. Remove the secret from active configuration.
4. Review repository history.
5. Verify logs and CI output.
6. Confirm that the new secret is injected securely.
```

---

## 7. Pre-commit secret scanning

TeleMed-IA should detect accidentally committed credentials before they reach the remote repository.

A secret scanner can be executed using a pre-commit hook.

For example:

```text
Developer creates commit
        ↓
Secret scanner runs
        ↓
Secret detected?
     /       \
   Yes        No
    ↓          ↓
Block      Commit
commit     allowed
```

Tools such as the following can be used:

```text
Gitleaks
TruffleHog
detect-secrets
```

Example with Gitleaks:

```bash
gitleaks protect --staged
```

The same verification should also run in CI so the repository has two protection layers:

```text
Local pre-commit scan
        +
CI secret scan
```

---

## 8. Feature flag

At least one new TeleMed-IA capability is protected using a feature flag.

For Identity & Access, a good candidate is:

```text
Password Recovery
```

Environment variable:

```env
FEATURE_PASSWORD_RECOVERY=false
```

Example configuration:

```java
@ConfigurationProperties(prefix = "features")
public record FeatureProperties(
        boolean passwordRecovery
) {
}
```

Before executing the capability:

```java
if (!featureProperties.passwordRecovery()) {
    throw new FeatureDisabledException("PASSWORD_RECOVERY");
}
```

This allows the code to be deployed without immediately exposing the functionality.

---

## 9. Deploy is not the same as release

The feature flag separates deployment from release.

For example:

```text
Deploy Identity & Access
        ↓
Password recovery code exists
        ↓
FEATURE_PASSWORD_RECOVERY=false
        ↓
Feature remains unavailable
```

When the team is ready:

```env
FEATURE_PASSWORD_RECOVERY=true
```

Then:

```text
Feature enabled
        ↓
Users can access password recovery
```

If a problem appears:

```env
FEATURE_PASSWORD_RECOVERY=false
```

The feature can be disabled without reverting the entire deployment.

---

## 10. TeleMed-IA example flow

The resulting configuration flow is:

```text
                    ┌─────────────────────┐
                    │    Secret Store     │
                    │ DB / JWT / API Keys │
                    └──────────┬──────────┘
                               │
                               ▼
                    Runtime secret injection
                               │
                               ▼
┌──────────────────────────────────────────────────────┐
│              Identity & Access Service               │
│                                                      │
│  Environment configuration                           │
│  Startup validation                                  │
│  JWT configuration                                   │
│  Feature flags                                       │
│                                                      │
└───────────────────────┬──────────────────────────────┘
                        │
                        ▼
                Service starts only
               with valid configuration
```

---

## 11. Repository protection

The repository should contain:

```text
.env.example
.gitignore
pre-commit secret scanning
startup configuration validation
feature flag configuration
CI secret scanning
```

It must not contain:

```text
.env
database passwords
JWT private keys
API tokens
production credentials
hard-coded secrets
```

---

## 12. Acceptance criteria

Session 1 is considered implemented in TeleMed-IA when the following conditions are satisfied:

- [ ] `.env.example` documents every required runtime variable.
- [ ] `.env` is ignored by Git.
- [ ] No production secret exists in the repository.
- [ ] Required configuration is validated during startup.
- [ ] The service fails immediately when critical configuration is missing.
- [ ] Secrets are injected at runtime.
- [ ] JWT private keys are not stored in Git.
- [ ] A local secret scanner runs before commits.
- [ ] Secret scanning is also executed in CI.
- [ ] At least one capability is protected by a feature flag.
- [ ] New feature flags are disabled by default.
- [ ] The selected feature can be disabled without reverting the application deployment.

---

## 13. MVP 2 preparation

These controls prepare TeleMed-IA for the next planning session.

Session 2 will define:

```text
Secrets ownership
        +
Secret rotation
        +
Feature flag ownership
        +
Canary rollout
        +
Rollback strategy
        ↓
Secure progressive delivery for MVP 2
```

The objective is not only to deploy TeleMed-IA successfully, but to make releases **controlled, reversible, and secure**.

---

## Key principle

> **The same TeleMed-IA application artifact should run in every environment. Configuration changes by environment, secrets are injected securely at runtime, and risky capabilities are released progressively through feature flags.**