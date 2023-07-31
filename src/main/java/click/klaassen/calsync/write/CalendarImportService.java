package click.klaassen.calsync.write;

import click.klaassen.calsync.commons.Dates;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.Events;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Optional.*;

@AllArgsConstructor
@Slf4j
public class CalendarImportService {

    private static final String CALENDAR_ID = ofNullable(System.getenv("TARGET_CALENDAR_ID"))
            .orElse("unknown_calendar_id");
    public static final String START_TIME = "startTime";
    public static final String DEFAULT_MEETING_TITLE = "Termin";
    private final Calendar service;

    public static final String DECLINED_STATUS = "declined";
    private static final Predicate<? super Event> filterDeclined = event -> {
        if (event.getAttendees() == null || event.getAttendees().size() < 1) {
            return true;
        }
        return event.getAttendees().stream().filter(EventAttendee::isSelf).map(EventAttendee::getResponseStatus)
                .noneMatch(status -> status.equalsIgnoreCase(DECLINED_STATUS));
    };

    public void deleteEvents() throws IOException {
        List<Event> items = service.events().list(CALENDAR_ID).setTimeMin(Dates.START_OF_DAY)
                .setTimeMax(Dates.TODAY_PLUS_ONE_MONTH).setMaxResults(2500).setSingleEvents(true).setOrderBy(START_TIME)
                .execute().getItems();
        log.info("Load {} events from target calendar for delete", items.size());
        BatchRequest batch = service.batch();
        for (Event e : items) {
            try {
                service.events().delete(CALENDAR_ID, e.getId()).queue(batch, new DeleteBatchCallback());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (batch.size() == 50) {
                batch.execute();
                batch = service.batch();
            }
        }

        if (batch.size() > 0) {
            batch.execute();
        }
    }

    public void importEvents(Events events) throws IOException {
        BatchRequest batch = service.batch();
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            log.info("No upcoming events found.");
        } else {
            log.info("Upcoming events [{}]", items.size());
            for (Event e : items.stream().filter(filterDeclined).collect(Collectors.toList())) {
                log.debug("{} ({})", e.getSummary(), e.getStart());
                try {
                    Event newEvent = buildSimpleEvent(e);
                    service.events().insert(CALENDAR_ID, newEvent).queue(batch, new ImportBatchCallback());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (batch.size() == 50) {
                    batch.execute();
                    batch = service.batch();
                }
            }
        }
        if (batch.size() > 0) {
            batch.execute();
        }
    }

    private Event buildSimpleEvent(Event originalEvent) {
        Event simpleEvent = new Event();
        String meeting = DEFAULT_MEETING_TITLE;
        if (originalEvent.getAttendees() != null && originalEvent.getAttendees().size() > 0) {
            meeting = "Meeting [" + originalEvent.getAttendees().size() + "]";
            meeting += originalEvent.getAttendees().stream().filter(EventAttendee::isSelf)
                    .map(eventAttendee -> "(" + eventAttendee.getResponseStatus() + ")")
                    .collect(Collectors.joining(","));
        }
        simpleEvent.setSummary(meeting + ": " + originalEvent.getSummary());
        simpleEvent.setStart(originalEvent.getStart());
        simpleEvent.setEnd(originalEvent.getEnd());
        simpleEvent.setLocation(originalEvent.getLocation());
        return simpleEvent;
    }
}
