Nityo - Cathay interview test
===

### 1. Request
---

- Simple APIs
- Call third party API and get JSON

### 2. My Structure Design
---

- Task:
  1. Make any movement ( call api, edit DB, handle data ) as a Task ( abstract class )
  2. Design Retry System ( Seprate to different class base or use annotation do distinguish )
  3. Tasks can be chain as a combined Task, and retry all retryable in it while needed.
- Cache:
  1. Add simple in-service cache in case of call out fail ( even Kafka )
  2. Cache System should be scheduled.
