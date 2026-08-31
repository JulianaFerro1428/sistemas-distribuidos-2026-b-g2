<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.

     Your weekly grade is read AUTOMATICALLY from this file:

       05-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 05

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->

* FULL_NAME: Maria Juliana Ferro Bonilla
* GITHUB_USER: JulianaFerro1428
* TEAM: Telemed ai
* SPRINT_GOAL: Implement the core appointment scheduling flow, including availability consultation, appointment booking, appointment management, medical agenda consultation, and appointment status updates.

<!-- CONFIG-END -->

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-007 | Check Availability | done | https://github.com/JulianaFerro1428/telemed-ai.git |
| HU-008 | Schedule Appointment | done | https://github.com/JulianaFerro1428/telemed-ai.git |
| HU-009 | Manage My Appointments | done | https://github.com/JulianaFerro1428/telemed-ai.git |
| HU-010 | Check Medical Schedule | done | https://github.com/JulianaFerro1428/telemed-ai.git |
| HU-011 | Update Appointment Status | done | https://github.com/JulianaFerro1428/telemed-ai.git |

## 2. My individual contribution

- I worked directly on the following user stories related to the appointment scheduling flow:
  - **HU-007 – Check Availability:** I contributed to the functionality that allows patients to consult available dates and time slots for healthcare professionals.
  - **HU-008 – Schedule Appointment:** I worked on the process that allows patients to select an available slot and schedule a medical appointment.
  - **HU-009 – Manage My Appointments:** I contributed to the functionality that allows patients to view and manage their scheduled appointments.

- I also contributed to the project documentation repository by updating the **06-data** folder, including the information related to the system data structure and persistence design.

- These contributions helped connect the appointment scheduling requirements with the project architecture and data design, keeping the responsibilities of each domain clearly defined.
## 3. Blockers and risks

- Appointment availability must remain synchronized to prevent duplicate bookings for the same professional and time slot.
- Appointment status transitions must be controlled to avoid invalid lifecycle changes.
- Integration between patient, professional, and appointment information may introduce coupling if domain boundaries are not respected.
- Additional integration testing is required to validate the complete appointment flow.

## 4. Plan for next week

- Validate the complete appointment flow end to end.
- Add or improve unit and integration tests.
- Verify the main success and error scenarios.
- Review API contracts between the appointment domain and the other services.
- Prepare the services to run consistently using Docker.
- Validate the application against a real database and environment-based configuration.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [ ] Tests added/updated (unit / integration)
- [x] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links

- https://github.com/JulianaFerro1428/telemed-ai.git