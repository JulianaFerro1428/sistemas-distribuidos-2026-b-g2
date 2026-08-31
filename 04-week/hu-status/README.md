<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.

```
 Your weekly grade is read AUTOMATICALLY from this file:

   04-week/hu-status/README.md  (inside YOUR fork). English. -->
```

# Weekly Status - Week 04

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->

* FULL_NAME: Maria Juliana Ferro Bonilla
* GITHUB_USER: JulianaFerro1428
* TEAM: Telemed ai
* SPRINT_GOAL: Define the current MVP domains and document the main architectural decisions for the Telemedicine Platform with Intelligent Agent.

<!-- CONFIG-END -->


## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-XXX-001 |  |  |  |

## 2. My individual contribution

* I contributed to defining and documenting the current business domains of the Telemed AI MVP, establishing clear responsibilities and boundaries between them.

* The domains defined for the current system were:

  * **Identity & Access:** authentication, authorization and user access control.
  * **Patient Management:** management of patient information and profiles.
  * **Professional Management:** management of healthcare professional information and availability.
  * **Intelligent Agent:** pre-consultation support, collection and organization of patient information, without providing diagnoses or prescriptions.
  * **Appointment Scheduling:** management of appointment availability, scheduling and appointment lifecycle.
  * **Medical Consultation:** management of the medical consultation process and its information.
  * **Notifications:** communication of relevant system events to users.
  * **Document Generation:** generation of summaries and documents derived from the platform processes.

* I also contributed to the architectural documentation through **Architecture Decision Records (ADRs)**. These ADRs define important technical decisions and make clear why each architectural approach was selected.

* These decisions helped establish clearer domain boundaries, reduce coupling between components and create an architectural foundation for the next implementation stages.

## 3. Blockers and risks

* Some architectural decisions still require validation as the implementation progresses.
* ADR-011, related to the message broker, is still pending a final decision.
* A key risk is allowing responsibilities to overlap between domains, which could increase coupling and make future evolution of the system more difficult.
* The architecture documentation must remain synchronized with future implementation changes.

## 4. Plan for next week

* Define the MVP 1 API contract before implementation using a contract-first approach.
* Specify the main endpoints, requests, responses and error cases.
* Break the MVP work into small and estimable tasks with clear acceptance criteria.
* Prioritize the sprint scope using MoSCoW (Must, Should, Could and Won't).
* Define a clear Sprint Goal and Definition of Done.
* Begin connecting the defined architecture with the first executable end-to-end service flow.

## 5. Compliance self-check

* [x] Conventional Commits - `type(scope): summary`
* [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
* [x] Testable acceptance criteria
* [ ] Tests added/updated (unit / integration)
* [x] DDD / hexagonal boundaries respected (domain has no I/O)
* [x] No secrets; config via environment variables

## 6. Evidence links

* Domain definition and documentation:
  https://github.com/code-corhuila/telemed-ia-docs/tree/main/02-domain

* Architecture and ADR documentation:
  https://github.com/code-corhuila/telemed-ia-docs/tree/main/05-architecture





<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       04-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 04

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Maria Juliana Ferro Bonilla
- GITHUB_USER: JulianaFerro1428
- TEAM: Telemed ai
- SPRINT_GOAL: Define the current MVP domains and document the main architectural decisions for the Telemedicine Platform with Intelligent Agent.
<!-- CONFIG-END -->

## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-XXX-001 |  |  |  |

## 2. My individual contribution
-

## 3. Blockers and risks
-

## 4. Plan for next week
-

## 5. Compliance self-check
- [ ] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [ ] Testable acceptance criteria
- [ ] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (domain has no I/O)
- [ ] No secrets; config via environment variables

## 6. Evidence links
-
