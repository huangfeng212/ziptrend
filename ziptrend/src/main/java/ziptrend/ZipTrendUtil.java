package ziptrend;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

public class ZipTrendUtil {
  public static boolean forceStampOn(final Instant instant) {
    final int minute = instant.atZone(ZoneOffset.UTC).getMinute();
    return minute == 0 || minute == 15 || minute == 30 || minute == 45;
  }

  public static void trimEnd(final List<UnzippedPoint> unzippedPoints, final Instant end) {
    if (forceStampOn(end)) {
      while (unzippedPoints.get(unzippedPoints.size() - 1).getTs().isAfter(end)) {
        unzippedPoints.remove(unzippedPoints.size() - 1); // trim off excessive tail caused by dups
      }
    } else {
      throw new IllegalArgumentException();
    }
  }
}
