package ziptrend;

import java.time.Instant;

public interface UnzippedPoint {
  Instant getTs();

  Object getVal();
}
