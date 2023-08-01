package click.klaassen.calsync.commons;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppProperties {

    private static Properties getAppProperties() {
        var appProperties = new Properties();
        try {
            appProperties.load(new FileInputStream(".env"));
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return appProperties;
    };

    public static String getSourceCalendarId() {
        return getAppProperties().getProperty("SOURCE_CALENDAR_ID", "unknown_source_cal_id");
    }

    public static String getTargetCalendarId() {
        return getAppProperties().getProperty("TARGET_CALENDAR_ID", "unknown_target_cal_id");
    }

}
