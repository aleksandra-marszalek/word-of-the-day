# Word of the Day API

A daily word challenge API built with Micronaut, AWS Lambda, DynamoDB, and Redis.

Every day at 9am UTC a new word is fetched from a random word API, enriched with
a full definition from the Dictionary API, and stored in DynamoDB. Users can fetch
the word of the day and submit guesses.

## Tech Stack

- **Micronaut** — framework with compile-time DI
- **AWS Lambda** — serverless compute
- **AWS DynamoDB** — persistent storage
- **Redis (Upstash)** — caching layer
- **GraalVM Native Image** — fast cold starts

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/word/today` | Get today's word definition |
| POST | `/api/v1/word/guess` | Submit a guess |

### Example responses

`GET /api/v1/word/today`
```json
{
  "date": "2026-04-06",
  "definition": "lasting for a very short time",
  "partOfSpeech": "adjective",
  "phonetic": "/ɪˈfem.ər.əl/",
  "audioUrl": "https://...",
  "length": 9
}
```

`POST /api/v1/word/guess`
```json
{ "guess": "ephemeral" }
```
Response:
```json
{ "correct": true }
```

## Running Locally

### Prerequisites
- Java 21 (Amazon Corretto)
- Maven
- Docker Desktop
- AWS CLI configured
- SAM CLI

### Start local Redis
```bash
docker run -d -p 6379:6379 redis:latest
```

### Build and run
```bash
./mvnw package -DskipTests
sam local start-api --template sam.jvm.yml --warm-containers EAGER
```

### Run tests
```bash
./mvnw test
```

## DynamoDB Schema

| Attribute | Type | Description |
|-----------|------|-------------|
| PK | String (partition key) | Always `"WORD"` |
| SK | String (sort key) | Date `YYYY-MM-DD` |
| word | String | The word |
| definition | String | Primary definition |
| partOfSpeech | String | e.g. noun, adjective |
| phonetic | String | Phonetic spelling |
| audioUrl | String | Pronunciation audio |
| fetchedAt | String | ISO timestamp |

## TODO — Future Improvements

- [ ] Add `POST /word/admin` endpoint to manually trigger or override word of the day (can do it in the DB directly for now)
- [ ] Introduce safe handling to edge cases:
  - add `date` field to `GuessRequestDTO` to fix race condition at 9am when new word is fetched (TOCTOU issue)
  - Add idempotency guard on scheduler (DynamoDB conditional write `attribute_not_exists(date)`) 
- [ ] Add frontend (SvelteKit) 
- [ ] Add API Gateway with custom domain
  -  Consider adding rate limiting on guess endpoint to prevent brute force
- [ ] Add history endpoint `GET /api/v1/word/history` // allow users to guess words from previous days
- [ ] Consider splitting `WordOfDayService` into command/query
  services (CQS pattern)
- [ ] Switch Redis serialization from Java to JSON for better debuggability
- [ ] Add additional hints for the user on guessing (how many letters correct, etc.)
- [ ] Add Mockito as JVM agent to remove warning in test output
