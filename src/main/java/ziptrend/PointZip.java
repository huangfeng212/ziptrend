package ziptrend;

import java.time.Duration;
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
          if (stack.empty()) {
            stack.push(new ZipPoint(rawPoint.getId(), rawPoint.getTs(), rawPoint.getVal(), 1, 0L));
          } else {
            final ZipPoint peek = stack.peek();
            if (peek.getVal().equals(rawPoint.getVal())) {
              if (peek.getSpan() == 0) { // peek is first of its group
                peek.setSpan(Duration.between(peek.getTs(), rawPoint.getTs()).getSeconds());
                peek.setMult(2);
              } else { // already duplicated at least once
                final long l = peek.getSpan() * (peek.getMult());
                final long between = Duration.between(peek.getTs(), rawPoint.getTs()).getSeconds();
                if (l == between) {
                  peek.setMult(peek.getMult() + 1);
                } else {
                  stack.push(
                      new ZipPoint(rawPoint.getId(), rawPoint.getTs(), rawPoint.getVal(), 1, 0L));
                }
              }
            } else {
              stack.push(
                  new ZipPoint(rawPoint.getId(), rawPoint.getTs(), rawPoint.getVal(), 1, 0L));
            }
          }
        });
    return new ArrayList<>(stack);
  }
}
