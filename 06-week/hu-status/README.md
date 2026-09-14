<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.

     Your weekly grade is read AUTOMATICALLY from this file:

       06-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 06

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->

- FULL_NAME: Maria Juliana Ferro Bonilla
- GITHUB_USER: JulianaFerro1428
- TEAM: Telemed ai
- SPRINT_GOAL: Prepare and stabilize the current TeleMed IA MVP for the Week 06 project presentation, while continuing the implementation and integration of HU-06 — Pre-Consultation with Intelligent Agent.

<!-- CONFIG-END -->

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-06 | Pre-Consultation with Intelligent Agent | doing | https://github.com/JulianaFerro1428/telemed-ai/commit/5334e63ac70ac525e71b3a40bbed6b956619a785 |

## 2. My individual contribution

- Continued the implementation of HU-06 — Pre-Consultation with Intelligent Agent.
- Worked in depth on the intelligent agent conversational flow and its interaction with the patient.
- Improved the agent architecture using ports and adapters to keep the application logic decoupled from the specific AI provider.
- Integrated the real AI provider into the backend while keeping configuration and API credentials outside the source code.
- Improved the agent rules so that patient messages are used only to collect and organize information for the healthcare professional.
- Added restrictions so that the Intelligent Agent does not diagnose, prescribe medication, recommend doses, define treatments, or replace the healthcare professional.
- Worked on structured AI responses to separate patient-reported information from requests that are outside the agent's clinical scope.
- Implemented a controlled pre-consultation flow based on a limited number of relevant questions to reduce unnecessary interactions and token consumption.
- Added conversation session control and inactivity handling.
- Continued the integration between the Intelligent Agent backend and the Angular/Ionic frontend.
- Fixed Docker configuration files to improve project execution and deployment readiness.
- Updated and corrected the project documentation in the team documentation repository.
- Prepared the current system for the Week 06 project presentation and technical demonstration.

## 3. Blockers and risks

- HU-06 is still in progress and has not been fully completed.
- The Intelligent Agent requires additional end-to-end validation between the Angular/Ionic frontend, backend, AI provider, and persistence layer.
- The final structured pre-consultation summary still requires additional validation to ensure that only patient-reported information is included.
- Some user stories defined in the MVP roadmap are still pending.
- The current architecture is still a modular monolith. The progressive migration to independent microservices has not started yet.
- Cloud deployment is still pending and will be addressed in later development stages.
- AI provider availability and token limits remain an external dependency and must continue to be controlled by the application.

## 4. Plan for next week

- Continue and complete the pending functionality of HU-06 — Pre-Consultation with Intelligent Agent.
- Validate the complete Intelligent Agent pre-consultation flow from the frontend.
- Validate the controlled question flow and final session completion.
- Complete and validate the structured pre-consultation summary for the healthcare professional.
- Continue integrating the Angular/Ionic frontend with the backend endpoints.
- Work on the remaining MVP user stories according to the product roadmap.
- Start defining the progressive migration strategy from the modular monolith to independent microservices.
- Identify the first bounded context to extract as an independent service.
- Continue preparing the project architecture and Docker configuration for future AWS deployment.

## 5. Compliance self-check

- [ ] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [ ] Testable acceptance criteria
- [ ] Tests added/updated (unit / integration)
- [x] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links

- HU-06 — Intelligent Agent implementation progress:  
  https://github.com/JulianaFerro1428/telemed-ai/commit/5334e63ac70ac525e71b3a40bbed6b956619a785

- Docker configuration fixes and deployment preparation:  
  https://github.com/JulianaFerro1428/telemed-ai/commit/825a73ac4a96ddd4ac1c07c048df0230384bc810

- Documentation corrections and updates:  
  https://github.com/code-corhuila/telemed-ia-docs/commit/a801012c992893674d2ec12f5b39a7615587e32a