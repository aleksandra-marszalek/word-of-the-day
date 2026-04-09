# Word of the Day API

A daily word challenge API built with Micronaut, AWS Lambda, DynamoDB, and Redis.

Every day at 9am UTC a new word is fetched from API Ninja's [random word api](https://api-ninjas.com/api/randomword), enriched with
a full definition from the [Dictionary API](https://dictionaryapi.dev/), and stored in DynamoDB. Users can fetch
the word of the day and submit guesses, ask for the hints or reveal the word completely, if they're stuck.

![Word of the Day Screenshot](screenshot.png)


## Tech Stack

- **Micronaut** — framework with compile-time DI
- **AWS Lambda** — serverless compute
- **AWS DynamoDB** — persistent storage (with Partition and Sort Keys supporting efficient search)
- **Redis (Upstash)** — caching layer (reduces retrieval to ~50 ms)
- **GraalVM Native Image** — super fast cold starts (<500 ms)

## Live Demo

You can now play the game [here](https://word-of-the-day-frontend.s3.eu-west-1.amazonaws.com/index.html). Enjoy!!

Base URL for the BE Api: `https://8lv1i6gga2.execute-api.eu-west-1.amazonaws.com/Prod`

### Example requests
```bash
# Get today's word
curl https://8lv1i6gga2.execute-api.eu-west-1.amazonaws.com/Prod/api/v1/word/today

# Submit a guess
curl -X POST https://8lv1i6gga2.execute-api.eu-west-1.amazonaws.com/Prod/api/v1/word/guess \
  -H "Content-Type: application/json" \
  -d '{"guess": "your-word-here"}'
```

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/word/today` | Get today's word definition |
| POST | `/api/v1/word/guess` | Submit a guess |
| GET | `/api/v1/word/hint` | Get a hint (reveals one letter position) |
| GET | `/api/v1/word/reveal` | Reveal the full word answer |

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

`GET /api/v1/word/hint?revealed=0&revealed=3`
```json
{ "position": 5, "letter": "e" }
```
Pass already-revealed positions as query params to avoid duplicates.

`GET /api/v1/word/reveal`
```json
{ "word": "ephemeral" }
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

- [ ] Add API Gateway with custom domain
  -  Consider adding rate limiting on guess endpoint to prevent brute force
- [ ] Add history endpoint `GET /api/v1/word/history` // allow users to guess words from previous days
- [ ] Consider splitting `WordOfDayService` into command/query
  services (CQS pattern)
- [ ] Switch Redis serialization from Java to JSON for better debuggability
- [ ] Add Mockito as JVM agent to remove warning in test output