package fr.eaudeparis.syncremocra.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class JSONUtil {

  public static String getString(Map<String, Object> data, String key) {
    return (data.get(key) == null) ? null : data.get(key).toString();
  }

  public static Long getLong(Map<String, Object> data, String key) {
    return (data.get(key) == null || data.get(key).toString().length() == 0)
        ? null
        : Long.valueOf(data.get(key).toString());
  }

  public static Integer getInteger(Map<String, Object> data, String key) {
    return (data.get(key) == null || data.get(key).toString().length() == 0)
        ? null
        : Integer.valueOf(data.get(key).toString());
  }

  public static Boolean getBoolean(Map<String, Object> data, String key) {
    return (data.get(key) == null || data.get(key).toString().length() == 0)
        ? null
        : Boolean.valueOf(data.get(key).toString());
  }

  public static Double getDouble(Map<String, Object> data, String key) {
    return (data.get(key) == null || data.get(key).toString().length() == 0)
        ? null
        : Double.valueOf(data.get(key).toString());
  }

  public static LocalDateTime getLocalDateTime(
      Map<String, Object> data, String key, String format) {
    if (data.get(key) == null || data.get(key).toString().length() == 0) {
      return null;
    }
    String dateString = data.get(key).toString();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return LocalDateTime.parse(dateString, formatter);
  }
}
