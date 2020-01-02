package ziptrend;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UnzippingTrend {
  final Long id;
  final List<ZippedPointImpl> zippedPoints;

  public UnzippingTrend(final Long id, final List<? extends ZippedPoint> zippedPoints) {
    this.id = id;
    this.zippedPoints =
        zippedPoints.stream()
            .map(
                zippedPoint ->
                    new ZippedPointImpl()
                        .setTs(zippedPoint.getTs())
                        .setVal(zippedPoint.getVal())
                        .setMult(zippedPoint.getMult())
                        .setSpan(zippedPoint.getSpan()))
            .collect(Collectors.toList());
  }

  public List<UnzippedPoint> unzip() {
    final List<UnzippedPoint> unzippedPoints = new ArrayList<>();
    zippedPoints.forEach(
        zipPoint -> {
          if (zipPoint.getMult() == 1 && zipPoint.getSpan() == 0) {
            unzippedPoints.add(
                new UnzippedPointImpl().setTs(zipPoint.getTs()).setVal(zipPoint.getVal()));
          } else if (zipPoint.getMult() > 1 && zipPoint.getSpan() > 0) {
            for (int pos = 0; pos < zipPoint.getMult(); pos++) {
              unzippedPoints.add(
                  new UnzippedPointImpl()
                      .setTs(zipPoint.getTs().plus(pos * zipPoint.getSpan(), ChronoUnit.SECONDS))
                      .setVal(zipPoint.getVal()));
            }
          } else {
            throw new IllegalStateException("BANG");
          }
        });
    return unzippedPoints;
  }
}
