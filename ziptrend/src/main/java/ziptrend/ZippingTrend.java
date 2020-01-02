package ziptrend;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class ZippingTrend {
  final Long id;
  final List<UnzippedPointImpl> unzippedPoints;

  public ZippingTrend(final Long id, final List<? extends UnzippedPoint> unzippedPoints) {
    this.id = id;
    this.unzippedPoints =
        unzippedPoints.stream()
            .map(
                unzippedPoint ->
                    new UnzippedPointImpl()
                        .setTs(unzippedPoint.getTs())
                        .setVal(unzippedPoint.getVal()))
            .collect(Collectors.toList());
  }

  public List<ZippedPoint> zip() {
    // Truncate to second
    unzippedPoints.forEach(
        unzippedPoint ->
            unzippedPoint.setTs(unzippedPoint.getTs().truncatedTo(ChronoUnit.SECONDS)));

    final Stack<ZippedPointImpl> stack = new Stack<>();
    unzippedPoints.forEach(
        unzippedPoint -> {
          if (stack.empty()) { // first one always put in
            stack.push(
                new ZippedPointImpl()
                    .setTs(unzippedPoint.getTs())
                    .setVal(unzippedPoint.getVal())
                    .setMult(1)
                    .setSpan(0L));
          } else {
            final ZippedPointImpl peek = stack.peek();
            if (!ZipTrendUtil.forceStampOn(peek.getTs())
                && ZipTrendUtil.forceStampOn(
                    unzippedPoint.getTs())) { // force stamp to ensure query interval
              stack.push(
                  new ZippedPointImpl()
                      .setTs(unzippedPoint.getTs())
                      .setVal(unzippedPoint.getVal())
                      .setMult(1)
                      .setSpan(0L));
            } else if (peek.getVal().equals(unzippedPoint.getVal())) { // possible collapsing
              if (peek.getSpan() == 0) { // peek is first of its group
                peek.setSpan(Duration.between(peek.getTs(), unzippedPoint.getTs()).getSeconds());
                peek.setMult(2);
              } else { // already collapsed at least once
                final long betweenOnPeek = peek.getSpan() * (peek.getMult());
                final long betweenIfPush =
                    Duration.between(peek.getTs(), unzippedPoint.getTs()).getSeconds();
                if (betweenOnPeek == betweenIfPush) {
                  peek.setMult(peek.getMult() + 1);
                } else { // streak breaks
                  stack.push(
                      new ZippedPointImpl()
                          .setTs(unzippedPoint.getTs())
                          .setVal(unzippedPoint.getVal())
                          .setMult(1)
                          .setSpan(0L));
                }
              }
            } else { // value changed, new row needed
              stack.push(
                  new ZippedPointImpl()
                      .setTs(unzippedPoint.getTs())
                      .setVal(unzippedPoint.getVal())
                      .setMult(1)
                      .setSpan(0L));
            }
          }
        });
    return new ArrayList<>(stack);
  }
}
