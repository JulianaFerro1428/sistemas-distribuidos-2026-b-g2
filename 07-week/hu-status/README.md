<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.

     Your weekly grade is read AUTOMATICALLY from this file:

       07-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 07

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->

- FULL_NAME: Maria Juliana Ferro Bonilla
- GITHUB_USER: JulianaFerro1428
- TEAM: Telemed ai
- SPRINT_GOAL: Improve the frontend integration and user experience of HU-06 — Pre-Consultation with Intelligent Agent, connecting the conversational flow with the backend and refining the patient interaction.

<!-- CONFIG-END -->

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-06 | Pre-Consultation with Intelligent Agent | doing | https://github.com/JulianaFerro1428/telemed-ai/commit/5334e63ac70ac525e71b3a40bbed6b956619a785 |
| HU-06 | Pre-Consultation with Intelligent Agent | doing | https://github.com/JulianaFerro1428/telemed-ai/commit/3472e2c110ca526c946fc4b8d854faca4a63543e |
| HU-06 | Pre-Consultation with Intelligent Agent | doing | https://github.com/JulianaFerro1428/telemed-ai/commit/cb500536c0f76939a5847ea34cb08f8855b55ca9 |

## 2. My individual contribution

- Continued the implementation of HU-06 — Pre-Consultation with Intelligent Agent.
- Improved the Angular/Ionic frontend for the intelligent pre-consultation flow.
- Connected the pre-consultation interface with the backend agent endpoints.
- Improved the chat interaction so that TeleMed IA starts the conversation with the initial pre-consultation question.
- Added visual handling for patient and agent messages inside the conversation interface.
- Improved the message input behavior, including message sending through the interface and keyboard interaction.
- Added loading feedback while the Intelligent Agent processes a patient message.
- Improved the visual representation of conversation states such as active, completed, and expired sessions.
- Integrated the frontend with the controlled pre-consultation flow managed by the backend.
- Continued validating the interaction between the Angular/Ionic frontend, Spring Boot backend, and the Intelligent Agent.
- Improved the overall usability and presentation of the Intelligent Agent experience for the patient.

## 3. Blockers and risks

- HU-06 is still in progress and requires additional end-to-end testing.
- The complete pre-consultation flow still needs validation under different patient interaction scenarios.
- Error handling between the frontend, backend, and external AI provider requires additional refinement.
- The final structured pre-consultation report still requires complete validation from the patient interaction to the healthcare professional view.
- Some MVP user stories remain pending.
- The current backend is still a modular monolith and the migration to independent microservices remains pending.

## 4. Plan for next week

- Continue testing and stabilizing HU-06 — Pre-Consultation with Intelligent Agent.
- Validate the complete frontend-to-backend pre-consultation flow.
- Improve error handling and user feedback when the AI provider or backend is unavailable.
- Validate session expiration and pre-consultation completion from the frontend.
- Complete the integration of the structured pre-consultation summary.
- Continue implementing pending MVP user stories.
- Begin planning the progressive migration of the modular monolith into independent microservices.
- Review service boundaries and identify the first domain to extract as an independent service.

## 5. Compliance self-check

- [ ] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [ ] Testable acceptance criteria
- [ ] Tests added/updated (unit / integration)
- [x] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links

- Intelligent Agent implementation and integration progress:  
  https://github.com/JulianaFerro1428/telemed-ai/commit/5334e63ac70ac525e71b3a40bbed6b956619a785

- Intelligent Agent frontend improvements:  
  https://github.com/JulianaFerro1428/telemed-ai/commit/3472e2c110ca526c946fc4b8d854faca4a63543e

- Additional frontend and agent interaction improvements:  
  https://github.com/JulianaFerro1428/telemed-ai/commit/cb500536c0f76939a5847ea34cb08f8855b55ca9

- Microservices service catalog definition and documentation:
  https://github.com/code-corhuila/telemed-ia-docs/commit/f82929b83b04c483a1c581bcad480bfaf161ed3f