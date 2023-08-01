# Google Calendar Sync

This application reads some events from a google calendar, 
copies the events and writes them into a second google calendar with some customizations.

## Build

### Configuration

You have to define two Environment variables: 

* `SOURCE_CALENDAR_ID` The google calendar ID of the source calendar where we want to read from
* `TARGET_CALENDAR_ID` The google calendar ID of the target calendar where we want to write in

This environment variables have to be configured in the `.env` file in the root directory of this project. 
There is a `.env.example` in this repository to demonstrate the usage of the .env file. 
The application uses this `.env` file to get the properties for the calendar ids. 
The Containerfile copies this config file to the container.

### Build Process

```
mvn formatter:format
mvn clean package
docker build -f src/main/docker/Containerfile . -t google-cal-sync:0.2
```

## Run as JAR

```
java -jar target/google-cal-sync-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Run as Container

```
docker run --name google-cal-sync google-cal-sync:0.2
```

## Debug
```
docker exec -it google-cal-sync /bin/bash
tail -f /var/log/cron.log 
```