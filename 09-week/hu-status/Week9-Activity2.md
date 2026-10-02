 # Session 2 — Secure Config Planning and Progressive Delivery

## TeleMed-IA — Identity & Access

This document describes the planning activities for **Session 2** of the TeleMed-IA project.

The objective is to prepare MVP 2 for a safer release by defining:

- a secrets ownership and rotation plan;
- a feature-flag policy;
- a canary rollout strategy;
- a rollback plan;
- hardening stories with testable acceptance criteria.

The goal is to move from general security recommendations to concrete, measurable, and reviewable actions.

---

## 1. Secrets plan

Every sensitive value used by TeleMed-IA must have:

```text
Owner
Storage location
Runtime injection method
Access policy
Rotation policy
```

A secret must never exist without clear ownership.

### Secrets inventory

| Secret | Owner | Storage | Runtime injection | Access | Rotation |
|---|---|---|---|---|---|
| PostgreSQL password | Identity & Access team | Secret manager | Environment variable / mounted secret | Identity & Access runtime only | Every 90 days or immediately after exposure |
| JWT private key | Identity & Access / Security | Secret manager | Mounted secret at runtime | Signing component only | Controlled rotation every 90 days or after exposure |
| JWT public key | Identity & Access | Configuration / distributed public material | Mounted file or environment reference | Services that validate tokens | Rotated with private key |
| External email credential | Notification / Identity integration owner | Secret manager | Runtime injection | Password recovery component only | According to provider policy |
| Third-party API key | Owning microservice | Secret manager | Runtime injection | Service account only | According to provider policy |

---

## 2. Secret ownership

Every secret has a responsible owner.

The owner is responsible for:

```text
creating the secret
configuring access
reviewing permissions
rotating the secret
revoking compromised credentials
documenting changes
```

For Identity & Access, the service team is responsible for database and authentication secrets.

The JWT signing key requires additional security because it can be used to issue valid access tokens.

---

## 3. Least privilege

Secrets must only be visible to the component that actually needs them.

Example:

```text
JWT private key
      ↓
Identity & Access signing component
```

Other services should not receive the private key.

They only need the public key to validate tokens:

```text
Identity & Access
   Private Key
      ↓
Signs JWT
      ↓
Access Token
      ↓
Other services
      ↓
Public Key
      ↓
Validate token
```

This reduces the impact of a compromised service.

---

## 4. Secret rotation plan

Secrets must be rotated regularly and immediately if exposure is suspected.

Example process:

```text
Generate new secret
        ↓
Store new secret
        ↓
Update runtime configuration
        ↓
Deploy / reload securely
        ↓
Verify service
        ↓
Revoke old secret
```

For JWT keys, rotation must avoid invalidating all active traffic unexpectedly.

A controlled transition can temporarily support:

```text
Current key
+
Previous validation key
```

until tokens signed with the previous key expire.

---

## 5. Secret exposure response

If a secret is accidentally committed or exposed:

```text
1. Revoke or rotate it immediately.
2. Do not rely only on deleting the file.
3. Remove the secret from active environments.
4. Review repository history.
5. Check CI/CD logs.
6. Verify who had access.
7. Generate a replacement.
8. Re-deploy with the new secret.
```

A secret committed to Git must be treated as compromised.

---

# Feature Flag Policy

## 6. Purpose

Feature flags allow TeleMed-IA to separate:

```text
Deploy
   ≠
Release
```

The application can contain new functionality without exposing it immediately to all users.

Feature flags must be temporary and controlled.

---

## 7. Feature flag naming

Flags must use clear and descriptive names.

Recommended format:

```text
FEATURE_<CAPABILITY>
```

Examples:

```env
FEATURE_PASSWORD_RECOVERY=false
FEATURE_AI_PRECONSULTATION=false
FEATURE_SMART_TRIAGE=false
```

Names must describe the capability, not implementation details.

Avoid:

```env
FEATURE_NEW=false
FEATURE_TEST=false
FLAG_1=false
```

---

## 8. Feature flag owner

Every feature flag must have an owner.

Example:

| Feature flag | Owner |
|---|---|
| `FEATURE_PASSWORD_RECOVERY` | Identity & Access team |
| `FEATURE_AI_PRECONSULTATION` | Preconsultation service team |
| `FEATURE_SMART_TRIAGE` | Triage / AI service team |

The owner is responsible for:

```text
enablement
monitoring
rollback
removal
documentation
```

---

## 9. Default behavior

All new risky features must be disabled by default.

Example:

```env
FEATURE_PASSWORD_RECOVERY=false
```

This means that code may already exist in production while the capability remains unavailable.

---

## 10. Removal policy

Feature flags are temporary.

Each flag must include:

```text
Owner
Creation date
Release target
Removal date
```

Example:

| Flag | Owner | Default | Removal target |
|---|---|---|---|
| `FEATURE_PASSWORD_RECOVERY` | Identity team | `false` | After stable MVP 2 release |
| `FEATURE_AI_PRECONSULTATION` | AI team | `false` | After progressive rollout |
| `FEATURE_SMART_TRIAGE` | Triage team | `false` | After validation in production |

Once a capability is stable and fully released, the flag must be removed from:

```text
code
configuration
.env.example
deployment files
documentation
```

This prevents feature-flag debt.

---

# Canary and Rollback Plan

## 11. Selected MVP 2 feature

For this plan, the selected feature is:

```text
AI Preconsultation
```

This capability collects information before the medical appointment and prepares a structured summary for the healthcare professional.

Because it is a core MVP 2 capability, it should not be activated for every user immediately.

---

## 12. Feature flag

The feature is protected with:

```env
FEATURE_AI_PRECONSULTATION=false
```

Initial production deployment:

```text
Code deployed
       ↓
Feature flag OFF
       ↓
No user exposure
```

This is known as a dark deployment.

---

## 13. Canary rollout

The rollout will occur gradually.

### Stage 1 — Internal validation

```text
Internal users only
```

Objectives:

- verify the complete flow;
- verify logs;
- verify integration between services;
- confirm database behavior;
- validate AI response structure;
- verify latency.

---

### Stage 2 — 5% canary

```text
5% of eligible users
```

Monitor:

```text
HTTP error rate
request latency
AI processing failures
database errors
timeout rate
user flow completion
```

The feature only moves to the next stage if the metrics remain healthy.

---

### Stage 3 — 25%

```text
25% of eligible users
```

Continue monitoring the same indicators.

Special attention should be given to:

```text
increased load
unexpected AI errors
integration failures
authentication errors
```

---

### Stage 4 — 50%

```text
50% of eligible users
```

The service should demonstrate stable behavior under a more realistic load.

---

### Stage 5 — 100%

```text
100%
```

The feature is considered fully released only after stability has been confirmed.

---

## 14. Canary decision flow

```text
Deploy code
    ↓
Flag OFF
    ↓
Internal users
    ↓
Metrics healthy?
   /       \
 No         Yes
 ↓           ↓
Flag OFF     5%
              ↓
        Metrics healthy?
          /       \
        No         Yes
        ↓           ↓
    Rollback        25%
                      ↓
                    50%
                      ↓
                    100%
```

---

# Rollback Plan

## 15. Primary rollback

The first rollback mechanism is the feature flag.

If the feature shows unexpected behavior:

```env
FEATURE_AI_PRECONSULTATION=false
```

The capability becomes unavailable without requiring a full redeploy.

Target rollback time:

```text
< 1 minute
```

---

## 16. Rollback triggers

Rollback should happen if any of the following conditions appear:

```text
significant increase in 5xx errors
high timeout rate
AI processing failures
unexpected database errors
authentication failures
critical user-flow failures
security incident
```

Exact thresholds should be defined in monitoring dashboards before release.

---

## 17. Secondary rollback

If disabling the feature flag is not enough:

```text
1. Disable the feature flag.
2. Stop further rollout.
3. Restore the previous stable application version.
4. Verify database compatibility.
5. Verify service health.
6. Investigate the failed release.
```

The team must not improvise rollback procedures during the incident.

---

# MVP 2 Hardening Stories

## 18. Story 1 — Startup validation

### User story

```text
As a platform team,
I want required configuration to be validated at startup,
so that services never run with invalid configuration.
```

### Acceptance criteria

- [ ] Required variables are documented.
- [ ] Missing required variables prevent startup.
- [ ] Invalid values prevent startup.
- [ ] The error message clearly identifies the invalid configuration.
- [ ] No secret value is printed in logs.

---

## 19. Story 2 — Secret storage

### User story

```text
As a security-conscious team,
I want secrets to be injected from a secure store,
so that credentials are never committed to source control.
```

### Acceptance criteria

- [ ] Production secrets do not exist in Git.
- [ ] `.env` is ignored.
- [ ] `.env.example` contains placeholders only.
- [ ] Secrets are injected at runtime.
- [ ] CI scans the repository for secrets.
- [ ] Local pre-commit scanning is available.
- [ ] Secret values never appear in application logs.

---

## 20. Story 3 — Feature flag

### User story

```text
As a product team,
I want AI Preconsultation to be protected by a feature flag,
so that deployment can happen independently from release.
```

### Acceptance criteria

- [ ] `FEATURE_AI_PRECONSULTATION` exists.
- [ ] The default value is `false`.
- [ ] When disabled, users cannot access the new capability.
- [ ] When enabled, eligible users can access it.
- [ ] The flag owner is documented.
- [ ] The removal target is documented.

---

## 21. Story 4 — Canary rollout

### User story

```text
As an operations team,
I want AI Preconsultation to be released progressively,
so that failures affect only a small part of the system.
```

### Acceptance criteria

- [ ] The rollout begins with internal users.
- [ ] The first external canary is limited to approximately 5%.
- [ ] Metrics are reviewed before increasing exposure.
- [ ] The rollout supports multiple stages.
- [ ] Each stage has a go / no-go decision.
- [ ] The feature can be disabled at any stage.

---

## 22. Story 5 — Rollback

### User story

```text
As an operations team,
I want a documented rollback procedure,
so that an unhealthy feature can be disabled immediately.
```

### Acceptance criteria

- [ ] Rollback steps are documented before release.
- [ ] The feature can be disabled using the feature flag.
- [ ] The target rollback time is under 1 minute.
- [ ] The team knows who is authorized to trigger rollback.
- [ ] Monitoring identifies the conditions that require rollback.
- [ ] A previous stable application version is available if required.

---

# MVP 2 Release Flow

## 23. Expected release process

```text
Development
     ↓
Automated tests
     ↓
Secret scanning
     ↓
Configuration validation
     ↓
Deploy with feature OFF
     ↓
Internal validation
     ↓
5% canary
     ↓
Observe metrics
     ↓
25%
     ↓
50%
     ↓
100%
     ↓
Remove temporary feature flag
```

At any stage:

```text
Problem detected
       ↓
Flag OFF
       ↓
Investigate
```

---

# 24. Definition of Done

Session 2 is considered complete when:

- [ ] Secrets have documented owners.
- [ ] Secret storage is documented.
- [ ] Rotation policies are defined.
- [ ] Access follows least privilege.
- [ ] Feature-flag naming rules are defined.
- [ ] Every flag has an owner.
- [ ] Every temporary flag has a removal target.
- [ ] An MVP 2 feature has a documented canary strategy.
- [ ] Rollback steps are defined before release.
- [ ] Rollback can be performed without an emergency redeploy.
- [ ] Hardening work is divided into testable stories.
- [ ] Every story has measurable acceptance criteria.

---

## Next step

The next session focuses on **persistence**.

This means validating that the secure release plan works correctly with:

```text
PostgreSQL
Liquibase migrations
persistence adapters
transactions
integration tests
Testcontainers
```

After that, the project can move toward the **MVP 2 release**.

---

## Key principle

> **TeleMed-IA should never depend on an all-or-nothing release. Secrets must have clear ownership, risky capabilities must be guarded by temporary feature flags, and every progressive rollout must have a rollback defined before production exposure begins.**