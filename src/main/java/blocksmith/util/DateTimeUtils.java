package blocksmith.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Joost
 */
public class DateTimeUtils {

    private DateTimeUtils() {

    }

    public static final Map<String, String> DATE_REGEX;

    static {
        // http://balusc.omnifaces.org/2007/09/dateutil.html

        DATE_REGEX = new HashMap<String, String>();
        DATE_REGEX.put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}$", "dd.MM.yyyy"); //01.04.2016
        DATE_REGEX.put("^\\d{4}\\.\\d{1,2}\\.\\d{1,2}$", "YYYY.MM.dd"); //01.04.2016
        DATE_REGEX.put("^[a-zA-Z]{2}\\s\\d{1,2}.\\d{1,2}.\\d{4}\\s\\d{1,2}:\\d{2}\\s[+|-]\\d{4}$", "EE dd.MM.yyyy HH:mm Z"); //Mi 06.07.2016 09:47 +0200
        DATE_REGEX.put("^\\d{8}$", "yyyyMMdd");
        DATE_REGEX.put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
        DATE_REGEX.put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
        DATE_REGEX.put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
        DATE_REGEX.put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
        DATE_REGEX.put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
        DATE_REGEX.put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
        DATE_REGEX.put("^\\d{12}$", "yyyyMMddHHmm");
        DATE_REGEX.put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
        DATE_REGEX.put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
        DATE_REGEX.put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
        DATE_REGEX.put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
        DATE_REGEX.put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
        DATE_REGEX.put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
        DATE_REGEX.put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
        DATE_REGEX.put("^\\d{14}$", "yyyyMMddHHmmss");
        DATE_REGEX.put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
        DATE_REGEX.put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
        DATE_REGEX.put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
        DATE_REGEX.put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
        DATE_REGEX.put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
        DATE_REGEX.put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
        DATE_REGEX.put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
    }

    /**
     * Determine SimpleDateFormat pattern matching with the given date string.
     * Returns null if format is unknown. You can simply extend DateUtil with
     * more formats if needed.
     *
     * @param dateString The date string to determine the SimpleDateFormat
     * pattern for.
     * @return The matching SimpleDateFormat pattern, or null if format is
     * unknown.
     * @see SimpleDateFormat
     */
    public static Optional<String> getDateFormat(String dateString) {
        for (var entry : DATE_REGEX.entrySet()) {
            var regex = entry.getKey();
            if (dateString.toLowerCase().matches(regex)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty(); // Unknown format.
    } 

    public static Optional<LocalDate> getLocalDateFrom(String iso8601) {
        try {
            var date = LocalDate.parse(iso8601);
            return Optional.of(date);
        } catch (DateTimeParseException e) {
            // Not an ISO 8601 formatted date string
        }
        return Optional.empty();
    }

}
