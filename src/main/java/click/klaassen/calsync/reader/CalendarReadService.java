package click.klaassen.calsync.reader;

import click.klaassen.calsync.commons.Dates;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Events;
import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class CalendarReadService {

    private final Calendar service;

    public Events readFutureEvents() throws IOException {
        return service.events().list("primary")
                .setTimeMin(Dates.START_OF_DAY)
                .setTimeMax(Dates.TODAY_PLUS_ONE_MONTH)
                .setMaxResults(2500)
                .setSingleEvents(true)
                .setOrderBy("startTime")
                .execute();
    }
}
