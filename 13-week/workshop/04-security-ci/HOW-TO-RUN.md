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
2. Point `IDENTITY_DB_MIGRATIONS_DIR` to the official Liquibase migrations of
   `telemed-ia-identity-and-access-db` at the commit pinned in the API `ci.yml`
   (`ebf2aae11a107eaf89f702e3cd1143440500732d`). Without it the test stops with
   `IDENTITY_DB_MIGRATIONS_DIR is required for official-schema E2E tests.`

```bash
git -C ../telemed-ia-identity-and-access-db archive ebf2aae11a107eaf89f702e3cd1143440500732d | tar -x -C /tmp/identity-db
export IDENTITY_DB_MIGRATIONS_DIR=/tmp/identity-db
```

3. From the API repository root run:

```bash
mvn -B -pl identity-and-access-app -am test -Dtest=LoginAccountLockE2ETest -Dsurefire.failIfNoSpecifiedTests=false
```

## CP8 · Real run (2026-10-29)

Run on a local branch `test/hu-02-login-account-lock` created from `origin/develop` (`331779f`) of
`telemed-ia-identity-and-access-api`, with JDK 21.0.12, Maven 3.9.16 and Docker 29.7.2. The test was
not pushed to the API repository: it stays red until #152 is built.

Console output (excerpt, unchanged lines):

```text
[INFO] Running com.telemed.identityaccess.LoginAccountLockE2ETest
[ERROR] Tests run: 1, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 18.26 s <<< FAILURE! -- in com.telemed.identityaccess.LoginAccountLockE2ETest
[ERROR] com.telemed.identityaccess.LoginAccountLockE2ETest.loginShouldBeLockedAfterFiveFailedAttemptsForTheSameEmail -- Time elapsed: 1.753 s <<< FAILURE!
org.opentest4j.AssertionFailedError: 
expected: 429 TOO_MANY_REQUESTS
 but was: 200 OK
	at com.telemed.identityaccess.LoginAccountLockE2ETest.loginShouldBeLockedAfterFiveFailedAttemptsForTheSameEmail(LoginAccountLockE2ETest.java:92)
[ERROR] Tests run: 1, Failures: 1, Errors: 0, Skipped: 0
[INFO] identity-and-access-core ........................... SUCCESS [  0.798 s]
[INFO] identity-and-access-adapters ....................... SUCCESS [  0.652 s]
[INFO] identity-and-access-app ............................ FAILURE [ 20.225 s]
[INFO] BUILD FAILURE
[INFO] Total time:  22.108 s
[INFO] Finished at: 2026-10-29T17:48:56-05:00
```

Reading: the register call returned `201` and the five wrong passwords returned `401` (those
assertions passed). Line 92 is the sixth login with the right password: `auth-service` answered
`200 OK` with tokens instead of `429`. This is the expected red result and proves criterion 1 of
#152 is not met today.

## CP8 · Workflow validation

`security.yml` was checked with actionlint 1.7.7 (official Docker image `rhysd/actionlint:1.7.7`):

```text
verbose: Linting .github/workflows/security.yml
verbose: Found 0 parse errors in 0 ms for .github/workflows/security.yml
verbose: Found total 0 errors in 35 ms for .github/workflows/security.yml
```

Result: valid, 0 errors.

## Use the workflow

Copy `security.yml` to `.github/workflows/security.yml` in the same repository. It runs on every
pull request to `develop`, `qa` and `main` with three jobs: `secret-scan` (gitleaks),
`dependency-audit` (OWASP dependency-check, fails on CVSS ≥ 7; `NVD_API_KEY` is an optional
repository secret that makes the NVD download faster) and `authorization-tests` (the test above).
