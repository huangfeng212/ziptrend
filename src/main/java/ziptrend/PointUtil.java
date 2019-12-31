package ziptrend;

import java.time.Instant;
import java.time.ZoneOffset;

public class PointUtil {
  public static boolean isQuarterSharp(Instant instant) {
    final int minute = instant.atZone(ZoneOffset.UTC).getMinute();
    return minute == 0 || minute == 15 || minute == 30 || minute ==45;
  }
}
