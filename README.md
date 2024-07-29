## Caddy Server

This is a simple Caddy Server based on Spring Boot.

### Develop

```bash
mvn clean package
```

### Run

```bash
# Build
mvn clean package

# Run
./start.sh
```

### Deploy by Docker

```bash
# Build Docker Image
docker build -t caddy-server:latest .

# Run Docker Container
docker run -d -p 8080:8080 caddy-server:latest
```

### License

[Apache License 2.0](./LICENSE)