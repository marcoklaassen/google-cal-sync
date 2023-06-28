package click.klaassen.calsync;

import click.klaassen.calsync.auth.AuthenticatorService;
import click.klaassen.calsync.reader.CalendarReadService;
import click.klaassen.calsync.write.CalendarImportService;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class App {

    /*
     * Reader Args
     */
    /**
     * Application name.
     */
    private static final String READER_APP_NAME = "Calendar Sync Reader";

    /**
     * Directory to store authorization tokens for this application.
     */
    private static final String READER_TOKENS_DIRECTORY_PATH = "reader_tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> READER_SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final String READER_CREDENTIALS_FILE_PATH = "/reader-credentials.json";


    /*
     * Writer Args
     */
    /**
     * Application name.
     */
    private static final String IMPORT_APPLICATION_NAME = "Calendar Importer";

    /**
     * Directory to store authorization tokens for this application.
     */
    private static final String IMPORT_TOKENS_DIRECTORY_PATH = "import_tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> IMPORT_SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String IMPORT_CREDENTIALS_FILE_PATH = "/import-credentials.json";


    public static void main(String... args) throws GeneralSecurityException, IOException {
        Calendar sourceCalendar = new AuthenticatorService(
                READER_APP_NAME,
                READER_TOKENS_DIRECTORY_PATH,
                READER_SCOPES,
                READER_CREDENTIALS_FILE_PATH).getCalender();

        Calendar targetCalendar = new AuthenticatorService(
                IMPORT_APPLICATION_NAME,
                IMPORT_TOKENS_DIRECTORY_PATH,
                IMPORT_SCOPES,
                IMPORT_CREDENTIALS_FILE_PATH).getCalender();

        CalendarImportService importService = new CalendarImportService(targetCalendar);

        // Get Events from Source Calendar
        Events events = new CalendarReadService(sourceCalendar).readFutureEvents();

        // Delete existing events from Calendar
        importService.deleteEvents();

        // Add Events to Calendar
        importService.importEvents(events);

    }
}