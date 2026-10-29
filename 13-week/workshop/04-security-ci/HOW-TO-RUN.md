# CP4 · How to run the security gates

Service: `auth-service` (`telemed-ia-identity-and-access-api`, Java 21 + Spring Boot, Maven).

## Story and criterion covered

`LoginAccountLockE2ETest` covers
[telemed-ia-docs#152](https://github.com/code-corhuila/telemed-ia-docs/issues/152)
(STRIDE S, risk 6), acceptance criterion 1: after 5 failed `POST /api/v1/auth/login` for the same
email, the next attempt returns `429` with `traceId` and no `accessToken`, even with the right
password and from another IP. Today `auth-service` has no per-account lock, so the test is expected
to **fail** (red) until #152 is implemented; then it must pass and stay in CI.

## Run the negative test locally

Requirements: JDK 21, Maven and Docker running (Testcontainers starts `postgres:16.4-alpine`).

1. Copy `LoginAccountLockE2ETest.java` to
   `identity-and-access-app/src/test/java/com/telemed/identityaccess/` in
   `telemed-ia-identity-and-access-api` (it reuses `OfficialDatabaseSchemaInitializer`).
2. From the repository root run:

```bash
mvn -B -pl identity-and-access-app -am test -Dtest=LoginAccountLockE2ETest -Dsurefire.failIfNoSpecifiedTests=false
```

## Use the workflow

Copy `security.yml` to `.github/workflows/security.yml` in the same repository. It runs on every
pull request to `develop`, `qa` and `main` with three jobs: `secret-scan` (gitleaks),
`dependency-audit` (OWASP dependency-check, fails on CVSS ≥ 7; `NVD_API_KEY` is an optional
repository secret that makes the NVD download faster) and `authorization-tests` (the test above).
