# Build
```
mvn clean package
docker build -f src/main/docker/Containerfile . -t google-cal-sync:0.1
```

# Run
You have to define two Environment variables: 

* `SOURCE_CALENDAR_ID` The google calendar ID of the source calendar where we want to read from
* `TARGET_CALENDAR_ID` The google calendar ID of the target calendar where we want to write in

```
SOURCE_CALENDAR_ID=<source-cal-id> \
TARGET_CALENDAR_ID=<target-cal-id> \
java -jar target/google-cal-sync-1.0-SNAPSHOT-jar-with-dependencies.jar
```

# Deployment

```
docker run google-cal-sync:0.1
```