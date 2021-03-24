package Data;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Static class that is used as a toolbox for time conversions and time comparisons.
 * @author Skyler McCracken
 */
public class TimeConverter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Converts the incoming time in UTC to a to a local time
     * @param utc
     * LocalDateTime in UTC timezone.
     * @return
     * Returns a LocalDateTime in System Default.
     */
    public static LocalDateTime utcTOlocal(LocalDateTime utc){
        ZoneId zid = ZoneId.systemDefault();

        ZonedDateTime zdtStart = utc.atZone(zid);

        return zdtStart.toLocalDateTime();
    }

    /**
     * Converts the incoming time in Local to the UTC timezone
     * @param local
     * LocalDateTime in the System Default
     * @return
     * Returns a LocalDateTime in the UTC translation.
     */
    public static LocalDateTime localToUTC(LocalDateTime local){
        //Convert to a ZonedDate Time in UTC

        ZoneId zid = ZoneId.systemDefault();
        ZonedDateTime zdtStart = local.atZone(zid);
        ZonedDateTime utcStart = zdtStart.withZoneSameInstant(ZoneId.of("UTC"));

        return utcStart.toLocalDateTime();
    }

    /**
     * Method to test whether the incoming localdatetime is within 15 minutes of now.
     * @param startTime
     * LocalDateTime in UTC timezone.
     * @return
     * Boolean returned based on the LocalDateTime parameter and now.
     */
    public static boolean withinFifteen(LocalDateTime startTime){
        boolean isWithin = false;
        LocalDateTime now = LocalDateTime.now();
        if (startTime.isAfter(now) && startTime.isBefore(now.plusMinutes(15))) isWithin = true;
        return  isWithin;
    }

    /**
     * Converts a LocalDateTime into a string
     * @param inputTime
     * LocalDateTime inputTime
     * @return
     * returns the string based on the static formatter.
     */
    public static String promptString(LocalDateTime inputTime){
       return inputTime.format(dateFormatter);
    }

    public static LocalDateTime estTOlocal(LocalDateTime EST) {
        ZoneId zid = ZoneId.of("America/New_York");
        ZonedDateTime zdtStart = EST.atZone(zid);
        ZonedDateTime localStart = zdtStart.withZoneSameInstant(ZoneId.systemDefault());
        return localStart.toLocalDateTime();
    }
}
