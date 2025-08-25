package click.klaassen.calsync.write;

import click.klaassen.calsync.commons.AppProperties;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;

import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

@Slf4j
public class PrimaryCalendarImportService extends CalendarImportService {

    private static final String PRIMARY_CALENDAR_ID = AppProperties.getTargetPrimaryCalendarId();
    public static final String DEFAULT_MEETING_TITLE = "Termin";

    public PrimaryCalendarImportService(Calendar service) {
        super(PRIMARY_CALENDAR_ID, service);
    }

    protected Event buildSimpleEvent(Event originalEvent) {
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
        simpleEvent.setReminders(new Event.Reminders().setUseDefault(false).setOverrides(null));
        simpleEvent.setDescription("");

        return simpleEvent;
    }
}
