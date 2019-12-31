package ziptrend;

import static ziptrend.PointUtil.isQuarterSharp;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.springframework.stereotype.Service;

@Service
public class PointZip {
  List<ZipPoint> zip(final List<RawPoint> rawPoints) {
    final Stack<ZipPoint> stack = new Stack<>();
    rawPoints.forEach(
        rawPoint -> {
          if (stack.empty()) {// first one always put in
            stack.push(new ZipPoint(rawPoint.getId(), rawPoint.getTs(), rawPoint.getVal(), 1, 0L));
          } else {
            final ZipPoint peek = stack.peek();
            if (!isQuarterSharp(peek.getTs()) && isQuarterSharp(rawPoint
                .getTs())) {//first of the quarter minute should be saved for query by 15 mins
              stack.push(
                  new ZipPoint(rawPoint.getId(), rawPoint.getTs(), rawPoint.getVal(), 1, 0L));
            } else if (peek.getVal().equals(rawPoint.getVal())) {//may collapse into it
              if (peek.getSpan() == 0) { // peek is first of its group
                peek.setSpan(Duration.between(peek.getTs(), rawPoint.getTs()).getSeconds());
                peek.setMult(2);
              } else { // already collapsed at least once
                final long l = peek.getSpan() * (peek.getMult());
                final long between = Duration.between(peek.getTs(), rawPoint.getTs()).getSeconds();
                if (l == between) {
                  peek.setMult(peek.getMult() + 1);
                } else {// streak breaks
                  stack.push(
                      new ZipPoint(rawPoint.getId(), rawPoint.getTs(), rawPoint.getVal(), 1, 0L));
                }
              }
            } else {//value changed, new row needed
              stack.push(
                  new ZipPoint(rawPoint.getId(), rawPoint.getTs(), rawPoint.getVal(), 1, 0L));
            }
          }
        });
    return new ArrayList<>(stack);
  }

}
