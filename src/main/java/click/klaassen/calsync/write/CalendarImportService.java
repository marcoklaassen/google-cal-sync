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
import java.util.function.Predicate;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public abstract class CalendarImportService {

    private final String CALENDAR_ID;
    private final Calendar service;

    public static final String START_TIME = "startTime";

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
                    if (newEvent != null) {
                        service.events().insert(CALENDAR_ID, newEvent).queue(batch, new ImportBatchCallback());
                    }
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

    /**
     * Builds a new event out of the original one.
     *
     * @param originalEvent
     *
     * @return null if the original event should not trigger an event in the target calendar or a new event
     */
    protected abstract Event buildSimpleEvent(Event originalEvent);
}
