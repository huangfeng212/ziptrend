package ziptrend;

import java.time.Instant;

public interface ZippedPoint {
  Instant getTs();

  Object getVal();

  Integer getMult();

  Long getSpan();
}
