# Build
```
mvn clean package
docker build -f src/main/docker/Containerfile . -t google-cal-sync:0.1
```

# Run

```
java -jar target/google-cal-sync-1.0-SNAPSHOT-jar-with-dependencies.jar
```

# Deployment

```
docker run google-cal-sync:0.1
```

# ToDo

* CronJob Execution in Container
* Logging for production use