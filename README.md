# Google Calendar Sync

This application reads some events from a google calendar, 
copies the events and writes them into a second google calendar with some customizations.
Additionally it identifies business travel events and add events in a dedicated business travel calendar. 

## Build

### Configuration

You have to define two Environment variables: 

* `SOURCE_CALENDAR_ID` The google calendar ID of the source calendar where we want to read from
* `TARGET_PRIMARY_CALENDAR_ID` The google calendar ID of the target calendar where we want to write all events in
* `TARGET_BUSINESS_TRIP_CALENDAR_ID` The google calendar ID of the target calendar where we want to write just business trips in
* `BUSINESS_TRIP_IDENTIFIERS` Comma separated list of words which identify an event as a business travel (e.g. Flight,Train,Hotel,Travel). All events with this words in the summary will be written in the business trip calendar additionally. 

This environment variables have to be configured in the `.env` file in the root directory of this project. 
There is a `.env.example` in this repository to demonstrate the usage of the .env file. 
The application uses this `.env` file to get the properties for the calendar ids. 
The Containerfile copies this config file to the container.

### Build Process

```
mvn formatter:format
mvn clean package
podman build -f src/main/docker/Containerfile . -t google-cal-sync:0.3
```

## Run as JAR

```
java -jar target/google-cal-sync-1.1-SNAPSHOT-jar-with-dependencies.jar
```

## Run as Container

```
podman run --replace --name google-cal-sync google-cal-sync:0.3
```

## Debug
```
podman exec -it google-cal-sync /bin/bash
tail -f /var/log/cron.log 
```