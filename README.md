    # Poop MCP Server

Poop MCP Server provides an MCP (Model Control Protocol) server for AI tools. This server enables AI models to interact with external services through a standardized protocol.

## Features

- **3D Scene Command Service**: Process movement commands in a 3D environment
- **Cryptocurrency Price Service**: Fetch real-time cryptocurrency prices using the CoinGecko API
- **Spring AI Integration**: Built with Spring AI's tool framework for seamless AI model integration
- **Docker Support**: Easy deployment with Docker

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker (optional, for containerized deployment)
- CoinGecko API key (for cryptocurrency price service)

## Installation and Setup

### Clone the Repository

```bash
git clone https://github.com/AlienMCP/poop-mcp-server.git
cd poop-mcp-server
```

### Environment Variables

Create a `.env` file in the project root with the following variables:

```
COINGECKO_API_KEY=your_api_key_here
```

### Build the Application

```bash
mvn clean package -DskipTests
```

## Usage

### Running Locally

```bash
java -jar target/poop-mcp-server-0.0.1-SNAPSHOT.jar
```

The server will start on port 8888 by default.

### Profiles

The application supports three profiles:

- `dev` (default): Development environment
- `test`: Testing environment
- `prod`: Production environment

To run with a specific profile:

```bash
java -jar target/poop-mcp-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Configuration

### Application Properties

The main configuration is in `application.yaml`:

```yaml
spring:
  ai:
    mcp:
      server:
        name: poop-mcp-server
        version: 0.0.1
  profiles:
    active: '@profile.name@'
server:
  port: 8888
```

Environment-specific configurations are in:
- `application-dev.yaml`
- `application-test.yaml`
- `application-prod.yaml`

## Docker Deployment

### Build Docker Image

```bash
mvn clean package -DskipTests
docker build -t poop-mcp-server:latest .
```

### Run Docker Container

```bash
docker run -p 8888:8888 -e COINGECKO_API_KEY=your_api_key_here poop-mcp-server:latest
```

## API Documentation

### 3D Scene Command Service

The 3D Scene Command Service provides functionality to process movement commands in a 3D environment.

#### Process Move Command

```
processMoveCommand(command, target, distance)
```

- `command`: The command type (only "move" is supported)
- `target`: The direction ("front", "back", "left", "right")
- `distance`: The distance to move (positive number)

Returns a JSON string with the command details or an error message.

### Cryptocurrency Price Service

The Cryptocurrency Price Service provides functionality to fetch real-time cryptocurrency prices.

#### Fetch Crypto Price

```
fetchCryptoPrice(coin)
```

- `coin`: The official full name of the cryptocurrency (e.g., "bitcoin", "ethereum")

Returns a JSON string with the cryptocurrency price details or an error message.

## Spring AI Integration

This server is designed to work with Spring AI's tool framework. Services are annotated with `@ToolServer` and methods with `@Tool` to make them available as tools for AI models.

