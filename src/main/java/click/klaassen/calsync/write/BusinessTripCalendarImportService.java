package click.klaassen.calsync.write;

import click.klaassen.calsync.commons.AppProperties;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class BusinessTripCalendarImportService extends CalendarImportService {

    private static final String BUSINESS_TRIP_CALENDAR_ID = AppProperties.getTargetBusinessCalendarId();
    private static final String DEFAULT_BUSINESS_TRIP_TITLE = "Dienstreise";
    private static final Set<String> BUSINESS_TRAVEL_IDENTIFIERS = AppProperties.getBusinessTravelIdentifiers();

    public BusinessTripCalendarImportService(Calendar service) {
        super(BUSINESS_TRIP_CALENDAR_ID, service);
    }

    protected Event buildSimpleEvent(Event originalEvent) {
        if (BUSINESS_TRAVEL_IDENTIFIERS.stream().map(String::toLowerCase).anyMatch(
                identifier -> originalEvent.getSummary().toLowerCase().matches(".*\\b" + identifier + "\\b.*"))) {
            // business trip detected
            Event simpleEvent = new Event();
            String meeting = DEFAULT_BUSINESS_TRIP_TITLE;
            simpleEvent.setSummary(meeting + ": " + originalEvent.getSummary());
            simpleEvent.setStart(originalEvent.getStart());
            simpleEvent.setEnd(originalEvent.getEnd());
            simpleEvent.setLocation(originalEvent.getLocation());
            simpleEvent.setReminders(new Event.Reminders().setUseDefault(false).setOverrides(null));
            simpleEvent.setDescription("");

            return simpleEvent;
        }
        return null;
    }
}
