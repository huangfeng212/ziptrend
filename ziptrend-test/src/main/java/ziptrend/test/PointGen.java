package ziptrend.test;

import com.google.common.base.Preconditions;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class PointGen {

  public static final int P_TRUE = 1000;
  //  public static final int P_CHANGE = 200;
  //  public static final int P_BURST = 5;
  public static final int P_MISSING = 1; // No need to support really, it's just zipping tool
  public static final int D_CHANGE = 2;
  public static final int D_INIT = 0;

  public List<UnzippedPointEntity> generate(
      final long pointId,
      final Instant start,
      final int hours,
      final int pChange,
      final int pBurst) {
    Preconditions.checkArgument(hours > 0);
    final Random random = new Random();
    final int[] int0 = new int[] {D_INIT};
    final List<UnzippedPointEntity> pointEntities = new ArrayList<>();
    for (int hour = 0; hour < hours; hour++) {
      for (int minute = 0; minute < 60; minute++) {
        // minute data
        final boolean shouldChange = random.nextInt(P_TRUE) < pChange;
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
            new UnzippedPointEntity(
                pointId, start.plus(hour * 3600 + minute * 60, ChronoUnit.SECONDS), int0[0]));
        // second data (burst)
        for (int second = 1; second < 60; second++) {
          final boolean shouldBurst = random.nextInt(P_TRUE) < pBurst;
          if (shouldBurst) {
            final boolean shouldAdd = random.nextBoolean();
            final int change = random.nextInt(D_CHANGE);
            if (shouldAdd) {
              int0[0] += change;
            } else {
              int0[0] -= change;
            }
            pointEntities.add(
                new UnzippedPointEntity(
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
