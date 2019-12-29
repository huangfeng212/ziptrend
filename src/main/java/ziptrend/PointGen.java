package ziptrend;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class PointGen {

  public static final int P_TRUE = 100;
  public static final int P_CHANGE = 20;
  public static final int P_BURST = 1;
  public static final int P_MISSING = 1;
  public static final int D_CHANGE = 2;
  public static final int D_INIT = 0;

  public List<RawPoint> generate(final long pointId, final Instant start, final int hours) {
    Preconditions.checkArgument(hours > 0);
    final Random random = new Random();
    final int[] int0 = new int[] {D_INIT};
    final List<RawPoint> pointEntities = new ArrayList<>();
    for (int hour = 0; hour < hours; hour++) {
      for (int minute = 0; minute < 60; minute++) {
        // minute data
        final boolean shouldChange = random.nextInt(P_TRUE) < P_CHANGE;
        if (shouldChange) {
          final boolean shouldAdd = random.nextBoolean();
          final int change = random.nextInt(D_CHANGE);
          if (shouldAdd) {
            int0[0] += change;
          } else {
            int0[0] -= change;
          }
        }
        pointEntities.add(
            new RawPoint(
                pointId, start.plus(hour * 3600 + minute * 60, ChronoUnit.SECONDS), int0[0]));
        // second data (burst)
        for (int second = 1; second < 60; second++) {
          final boolean shouldBurst = random.nextInt(P_TRUE) < P_BURST;
          if (shouldBurst) {
            final boolean shouldAdd = random.nextBoolean();
            final int change = random.nextInt(D_CHANGE);
            if (shouldAdd) {
              int0[0] += change;
            } else {
              int0[0] -= change;
            }
            pointEntities.add(
                new RawPoint(
                    pointId,
                    start.plus(hour * 3600 + minute * 60 + second, ChronoUnit.SECONDS),
                    int0[0]));
          }
        }
      }
    }
    return pointEntities;
  }
}
