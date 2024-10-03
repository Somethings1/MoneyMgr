package transaction.util;

import java.time.LocalDateTime;

public class PathUtil {

    public static String get(LocalDateTime date) {
        return "./" + date.format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public static String get(int year, int month) {
        return String.format("./%04d/%02d", year, month);
    }

    public static String get(int year) {
        return String.format("./%04d", year);
    }
}
