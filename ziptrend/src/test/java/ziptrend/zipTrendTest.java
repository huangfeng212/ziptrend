package ziptrend;

import com.google.common.base.Preconditions;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class zipTrendTest {
  public static final int P_TRUE = 1000;
  //  public static final int P_CHANGE = 200;
  //  public static final int P_BURST = 5;
  public static final int P_MISSING = 1; // No need to support really, it's just zipping tool
  public static final int D_CHANGE = 2;
  public static final int D_INIT = 0;

  static Stream<Arguments> percentage() {
    return Stream.of(
        //    Arguments.of(1,200, 0),
        Arguments.of(2, 200, 5)
        //      Arguments.of(3,200, 10),
        //        Arguments.of(4,200, 15),
        //        Arguments.of(5,200, 20),
        //        Arguments.of(6,200, 25),
        //        Arguments.of(7,200, 30)
        );
  }

  @ParameterizedTest(name = "change {0}/1000, burst {1}/1000")
  @MethodSource(value = {"percentage"})
  void zipPointTest(final int pChange, final int pBurst) {
    final List<UnzippedPoint> generated =
        generate(Instant.parse("2019-12-31T08:00:12.345Z"), 24, pChange, pBurst);
    System.out.println("original points: " + generated.size());

    final ZippingTrend zippingTrend = new ZippingTrend(1L, generated);
    final List<ZippedPoint> zipped = zippingTrend.zip();
    System.out.println("zipped points: " + zipped.size());

    final UnzippingTrend unzippingTrend = new UnzippingTrend(1L, zipped);
    final List<UnzippedPoint> unzipped = unzippingTrend.unzip();
    System.out.println("unzipped points: " + unzipped.size());

    Assertions.assertEquals(generated.size(), unzipped.size());
    for (int i = 0; i < generated.size(); i++) {
      final UnzippedPoint originalPoint = generated.get(i);
      final UnzippedPoint unzippedPoint = unzipped.get(i);
      Assertions.assertEquals(originalPoint.getVal(), unzippedPoint.getVal());
      Assertions.assertEquals(
          originalPoint.getTs().truncatedTo(ChronoUnit.SECONDS),
          unzippedPoint.getTs().truncatedTo(ChronoUnit.SECONDS));
    }

    System.out.println(
        String.format("compression ratio: %.2f", generated.size() * 3 * 1.0 / (zipped.size() * 5)));
    System.out.println(
        String.format(
            "space savings: %.2f", 1.0 - (zipped.size() * 5 * 1.0) / (generated.size() * 3)));
  }

  private List<UnzippedPoint> generate(
      final Instant start, final int hours, final int pChange, final int pBurst) {
    Preconditions.checkArgument(hours > 0);
    final Random random = new Random();
    final int[] int0 = new int[] {D_INIT};
    final List<UnzippedPoint> unzippedPoints = new ArrayList<>();
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
        unzippedPoints.add(
            new UnzippedPointImpl()
                .setTs(start.plus(hour * 3600 + minute * 60, ChronoUnit.SECONDS))
                .setVal(int0[0]));
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
            unzippedPoints.add(
                new UnzippedPointImpl()
                    .setTs(start.plus(hour * 3600 + minute * 60 + second, ChronoUnit.SECONDS))
                    .setVal(int0[0]));
          }
        }
      }
    }
    return unzippedPoints;
  }
}
