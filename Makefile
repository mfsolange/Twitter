# Variables
PROFILE=prod
IMAGE_NAME=uala-api

# Build the application JAR
build:
	mvn clean package

# Run tests
test:
	mvn test

# Clean build artifacts
clean:
	mvn clean

# Build Docker image
docker-build: build
	docker build -t $(IMAGE_NAME) .

# Run app and db using docker-compose
docker-up: docker-build
	docker-compose up --build

# Stop docker-compose services
docker-down:
	docker-compose down

# Rebuild and restart only the app service
docker-restart:
	docker-compose up --build --no-deps app
