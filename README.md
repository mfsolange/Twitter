# Uala code challenge

## Features

- Publish a tweet (max 280 characters)
- Follow another user
- Fetch timeline (paginated, offset-based)
- Validations and meaningful error responses
- Global exception handling
- Containerized PostgreSQL integration tests

## API Endpoints
### POST /follows
**Follow a user**

Headers:
- X-User-Id: {UUID}

Body:

```json
{
  "followedId": "UUID"
}
```

### POST /tweets
**Post a tweet**

Headers:
- X-User-Id: {UUID}

Body:

```json
{
  "content": "This is a tweet!"
}
```

### GET /timeline
**Fetch paginated timeline**

Headers:
- X-User-Id: {UUID}

Query Params:
- offset (default: 0)
- limit (default: 10)

Response:

```json
{
    "data": {
        "tweets": [
            {
                "userId": "UUID",
                "content": "Tweet content",
                "createdAt": "2025-01-01T00:00:00"
            }
        ],
        "pagination": {
            "limit": 10,
            "offset": 0,
            "hasMore": false
        }
    },
    "error": null
}
```

## Improvements
- Fan-out on write (timeline table with tweet references) with queues
- Response caching for timeline queries to reduce DB load
- Full pagination metadata (e.g., total count)

## Development with Makefile
This project includes a Makefile to simplify common development and Docker tasks.

Example Usage
```bash
# Build and run everything
make docker-up

# Run tests
make test

# Stop services
make docker-down

# Rebuild and restart only the app container
make docker-restart
```