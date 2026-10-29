# CP4 · How to run the security gates

Service: `auth-service` (`telemed-ia-identity-and-access-api`, Java 21 + Spring Boot, Maven).

## Story and criterion covered

`LoginAccountLockE2ETest` covers
[telemed-ia-docs#152](https://github.com/code-corhuila/telemed-ia-docs/issues/152)
(STRIDE S, risk 6), acceptance criterion 1: after 5 failed `POST /api/v1/auth/login` for the same
email, the next attempt returns `429` with `traceId` and no `accessToken`, even with the right
password and from another IP. The test was red first (CP8, below) and is green since the per-account
lock of #152 was built in `auth-service` (CP9, below); it must stay in CI.

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
pushed to the API repository at that point, because #152 was not built yet.

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
#152 was not met.

## CP9 · Implementation in the real service (2026-10-29)

#152 was built with red/green TDD commits in two pull requests (merge the database one first):

- [identity-and-access-db#59](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/59):
  new migration `007_create_login_attempts.sql` (one row per account, keyed by the SHA-256 of the
  normalized email) and `004_login_attempts_grants.sql` (`SELECT` for `identity_reader`,
  `INSERT, UPDATE` for `identity_writer`, no `DELETE`).
- [identity-and-access-api#134](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/134):
  `LoginAttempts` (5 failures in 15 minutes lock for 1, 5, then 15 minutes), the check in
  `LoginService` before BCrypt, the JPA adapter and `429` with `Retry-After` and `traceId` in
  `GlobalExceptionHandler`. This test is in the PR, with one more line that checks `Retry-After: 60`.

Run with `IDENTITY_DB_MIGRATIONS_DIR` pointing to identity-db `0241b20` (the commit with the new
table) and `mvn -B verify` (excerpt, unchanged lines):

```text
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 4.543 s -- in com.telemed.identityaccess.LoginAccountLockE2ETest
[INFO] Tests run: 48, Failures: 0, Errors: 0, Skipped: 0
[INFO] identity-and-access-core ........................... SUCCESS [  3.885 s]
[INFO] identity-and-access-adapters ....................... SUCCESS [  9.883 s]
[INFO] identity-and-access-app ............................ SUCCESS [01:13 min]
[INFO] BUILD SUCCESS
[INFO] Finished at: 2026-10-29T18:37:21-05:00
```

Reading: the same request that answered `200 OK` in CP8 now answers `429`. GitHub Actions did not
start on either PR ("account is locked due to a billing issue", same on every organization PR
since 2026-10-25), so these are local runs.

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
