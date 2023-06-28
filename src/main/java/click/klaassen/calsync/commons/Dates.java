package click.klaassen.calsync.commons;

import com.google.api.client.util.DateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Dates {
    private static final LocalDateTime startOfDayBase = LocalDate.now().atStartOfDay();
    public static final DateTime START_OF_DAY = new DateTime(startOfDayBase.toInstant(ZoneOffset.UTC).toEpochMilli());
    public static final DateTime TODAY_PLUS_ONE_MONTH = new DateTime(startOfDayBase.plusMonths(1).toInstant(ZoneOffset.UTC).toEpochMilli());
}
