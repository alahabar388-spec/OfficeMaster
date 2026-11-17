# NeuroHub architecture

1. Android client: chat, settings, conversation history and attachments.
2. API gateway: authentication, validation, rate limiting and request IDs.
3. Orchestrator: classifies task intent and required capabilities.
4. Model router: ranks configured providers by capability, availability, latency and user budget.
5. Provider adapters: normalize requests and responses across cloud APIs and local inference.
6. Quality controller: optionally asks a second model to verify high-impact outputs.
7. Memory and storage: user-approved memory, PostgreSQL and object storage.

## Automatic routing policy
- Simple chat: efficient low-cost model.
- Coding/reasoning: strongest available configured reasoning model.
- Image/audio tasks: provider with the required modality.
- Provider failure: retry/fallback within configured budget.
- Parallel comparison: opt-in or used only when the task benefits from independent answers.

## Deployment boundaries
Provider API keys are backend-only. The Android app talks to the NeuroHub API, never directly to secret-bearing provider endpoints. Local models are an optional capability subject to device RAM, storage and thermal limits.
