package click.klaassen.calsync.reader;

import click.klaassen.calsync.commons.Dates;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Events;
import lombok.AllArgsConstructor;

import java.io.IOException;

import static java.util.Optional.ofNullable;

@AllArgsConstructor
public class CalendarReadService {

    public static final String CALENDAR_ID = ofNullable(System.getenv("SOURCE_CALENDAR_ID")).orElse("unknown_calendar_id");
    public static final String START_TIME = "startTime";
    private final Calendar service;

    public Events readFutureEvents() throws IOException {
        return service.events().list(CALENDAR_ID)
                .setTimeMin(Dates.START_OF_DAY)
                .setTimeMax(Dates.TODAY_PLUS_ONE_MONTH)
                .setMaxResults(2500)
                .setSingleEvents(true)
                .setOrderBy(START_TIME)
                .execute();
    }
}
