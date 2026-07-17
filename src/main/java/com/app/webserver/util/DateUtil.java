/**
 * Author: Johnathan McAllister (McAdmin)
 * Date: 2026-07-03
 * Course:
 * Professor:
 * <p>
 * Purpose:
 * -
 * <p>
 * Constraints:
 * -
 */

package com.app.webserver.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DateUtil {

    public static LocalDateTime parse(String input) {

        List<DateTimeFormatter> dateTimeFormats = List.of(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a"),
                DateTimeFormatter.ofPattern("M/d/yyyy h:mm a"),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );

        List<DateTimeFormatter> dateFormats = List.of(
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("M/d/yyyy"),
                DateTimeFormatter.ofPattern("MMM d yyyy"),
                DateTimeFormatter.ofPattern("MMMM d yyyy")
        );

        // Try DateTime formats first
        for (DateTimeFormatter formatter : dateTimeFormats) {
            try {
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }

        // Then try Date-only formats
        for (DateTimeFormatter formatter : dateFormats) {
            try {
                return LocalDate.parse(input, formatter).atStartOfDay();
            } catch (DateTimeParseException ignored) {
            }
        }

        throw new IllegalArgumentException(
                "Invalid date format. Examples:\n" +
                        "2026-06-13\n" +
                        "2026-06-13 14:30\n" +
                        "06/13/2026\n" +
                        "06/13/2026 2:30 PM"
        );
    }

    public static Instant parseInstant(String input) {

        ZoneId zone = ZoneId.systemDefault();

        List<DateTimeFormatter> dateTimeFormats = List.of(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a"),
                DateTimeFormatter.ofPattern("M/d/yyyy h:mm a"),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );

        List<DateTimeFormatter> dateFormats = List.of(
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("M/d/yyyy"),
                DateTimeFormatter.ofPattern("MMM d yyyy"),
                DateTimeFormatter.ofPattern("MMMM d yyyy")
        );

        // Try DateTime formats first
        for (DateTimeFormatter formatter : dateTimeFormats) {
            try {
                return LocalDateTime
                        .parse(input, formatter)
                        .atZone(ZoneId.of(String.valueOf(zone)))
                        .toInstant();
            } catch (DateTimeParseException ignored) {
            }
        }

        // Then try Date-only formats
        for (DateTimeFormatter formatter : dateFormats) {
            try {
                return LocalDate
                        .parse(input, formatter)
                        .atStartOfDay()
                        .atZone(ZoneId.of(String.valueOf(zone)))
                        .toInstant();
            } catch (DateTimeParseException ignored) {
            }
        }

        throw new IllegalArgumentException(
                "Invalid date format. Examples:\n" +
                        "2026-06-13\n" +
                        "2026-06-13 14:30\n" +
                        "06/13/2026\n" +
                        "06/13/2026 2:30 PM"
        );
    }

}
