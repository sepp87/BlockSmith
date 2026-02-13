package btslib.method;

import blocksmith.domain.value.ParamInput.Choice;
import blocksmith.domain.value.ParamInput.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.chrono.IsoEra;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import btscore.utils.DateTimeUtils;
import blocksmith.infra.blockloader.annotations.Value;
import blocksmith.infra.blockloader.annotations.Block;

/**
 *
 * @author JoostMeulenkamp
 */
public class DateMethods {

    @Block(
            type = "Input.date",
            aliases = {"Date.new"},
            name = "Date",
            description = "Obtains an instance of LocalDate from a text string such as 2007-12-03.",
            category = "Core")
    public static LocalDate inputDate(@Value(input = Date.class) String value) {
        String pattern = DateTimeUtils.getDateFormat(value);
        if (pattern == null) {
            throw new DateTimeParseException("Process stopped, because the date format was unknown.", value, 0);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate date = LocalDate.parse(value, formatter);
        return date;
    }

    @Block(
            type = "Date.temporalUnit",
            name = "TemporalUnit",
            description = "A standard set of date periods units.",
            category = "Core")
    public static ChronoUnit temporalUnit(@Value(input = Choice.class) String value) {
        return ChronoUnit.valueOf(value);
    }

    @Block(
            name = "Date",
            description = "Obtains an instance of LocalDate from a text string such as 2007-12-03.",
            type = "Date.fromString",
            category = "Core")
    public static LocalDate fromString(String value) {
        String pattern = DateTimeUtils.getDateFormat(value);
        if (pattern == null) {
            throw new DateTimeParseException("Process stopped, because the date format was unknown.", value, 0);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate date = LocalDate.parse(value, formatter);
        return date;
    }

    @Block(
            name = "now",
            description = "Obtains the current date from the system clock in the default time-zone.",
            type = "Date.now",
            category = "Core")
    public static LocalDate now() {
        return LocalDate.now();
    }

    @Block(
            name = "DayOfWeek",
            description = "Gets the day-of-week field, which is an enum DayOfWeek.",
            type = "Date.getDayOfWeek",
            category = "Core")
    public static DayOfWeek getDayOfWeek(LocalDate date) {
        return date.getDayOfWeek();
    }

    @Block(
            name = "DayOfYear",
            description = "Gets the day-of-year field.",
            type = "Date.getDayOfYear",
            category = "Core")
    public static int getDayOfYear(LocalDate date) {
        return date.getDayOfYear();
    }

    @Block(
            name = "Era",
            description = "Gets the era applicable at this date.\nThe official ISO-8601 standard does not define eras, however IsoChronology does. It defines two eras, 'CE' from year one onwards and 'BCE' from year zero backwards. Since dates before the Julian-Gregorian cutover are not in line with history, the cutover between 'BCE' and 'CE' is also not aligned with the commonly used eras, often referred to using 'BC' and 'AD'.",
            type = "Date.getEra",
            category = "Core")
    public static IsoEra getEra(LocalDate date) {
        return date.getEra();
    }

    @Block(
            name = "Month",
            description = "Gets the month-of-year field using the Month enum.",
            type = "Date.getMonth",
            category = "Core")
    public static Month getMonth(LocalDate date) {
        return date.getMonth();
    }

    @Block(
            name = "MonthValue",
            description = "Gets the month-of-year field from 1 to 12.",
            type = "Date.getMonthValue",
            category = "Core")
    public static int getMonthValue(LocalDate date) {
        return date.getMonthValue();
    }

    @Block(
            name = "Year",
            description = "Gets the year field.",
            type = "Date.getYear",
            category = "Core")
    public static int getYear(LocalDate date) {
        return date.getYear();
    }

    @Block(
            name = "lengthOfMonth",
            description = "Returns the length of the month represented by this date.\nThis returns the length of the month in days. For example, a date in January would return 31.",
            type = "Date.lengthOfMonth",
            category = "Core")
    public static int lengthOfMonth(LocalDate date) {
        return date.lengthOfMonth();
    }

    @Block(
            name = "lengthOfYear",
            description = "Returns the length of the year represented by this date.\nThis returns the length of the year in days, either 365 or 366.",
            type = "Date.lengthOfYear",
            category = "Core")
    public static int lengthOfYear(LocalDate date) {
        return date.lengthOfYear();
    }

    @Block(
            name = "minus",
            description = "Returns a copy of this date with the specified amount subtracted.",
            type = "Date.minus",
            category = "Core")
    public static LocalDate minus(LocalDate date, long amountToSubstract, TemporalUnit unit) {
        return date.minus(amountToSubstract, unit);
    }

    @Block(
            name = "minusDays",
            description = "Returns a copy of this date with the specified amount subtracted.",
            type = "Date.minusDays",
            category = "Core")
    public static LocalDate minusDays(LocalDate date, long daysToSubstract) {
        return date.minusDays(daysToSubstract);
    }

    @Block(
            name = "minusMonths",
            description = "Returns a copy of this LocalDate with the specified number of months subtracted.",
            type = "Date.minusMonths",
            category = "Core")
    public static LocalDate minusMonths(LocalDate date, long monthsToSubstract) {
        return date.minusMonths(monthsToSubstract);
    }

    @Block(
            name = "minusWeeks",
            description = "Returns a copy of this LocalDate with the specified number of weeks subtracted.",
            type = "Date.minusWeeks",
            category = "Core")
    public static LocalDate minusWeeks(LocalDate date, long weeksToSubstract) {
        return date.minusWeeks(weeksToSubstract);
    }

    @Block(
            name = "minusYears",
            description = "Returns a copy of this LocalDate with the specified number of years subtracted.",
            type = "Date.minusYears",
            category = "Core")
    public static LocalDate minusYears(LocalDate date, long yearsToSubstract) {
        return date.minusYears(yearsToSubstract);
    }

    @Block(
            name = "isLeapYear",
            description = "Checks if the year is a leap year, according to the ISO proleptic calendar system rules.",
            type = "Date.isLeapYear",
            category = "Core")
    public static boolean isLeapYear(LocalDate date) {
        return date.isLeapYear();
    }

    @Block(
            name = "isBefore",
            description = "Checks if this date is before the specified date.",
            type = "Date.isBefore",
            category = "Core")
    public static boolean isBefore(LocalDate date, LocalDate other) {
        return date.isBefore(other);
    }

    @Block(
            name = "isAfter",
            description = "Checks if this date is after the specified date.",
            type = "Date.isAfter",
            category = "Core")
    public static boolean isAfter(LocalDate date, LocalDate other) {
        return date.isAfter(other);
    }

    @Block(
            name = "isEqual",
            description = "Checks if this date is equal to the specified date.",
            type = "Date.isEqual",
            category = "Core")
    public static boolean isEqual(LocalDate date, LocalDate other) {
        return date.isEqual(other);
    }

    @Block(
            name = "daysBetween",
            description = "Calculates the amount of time between two dates.",
            type = "Date.daysBetween",
            category = "Core")
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

}
