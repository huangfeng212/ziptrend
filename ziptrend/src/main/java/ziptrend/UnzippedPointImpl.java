package ziptrend;

import java.time.Instant;

class UnzippedPointImpl implements UnzippedPoint {
  Instant ts;
  Object val;

  @Override
  public Instant getTs() {
    return ts;
  }

  public UnzippedPointImpl setTs(final Instant ts) {
    this.ts = ts;
    return this;
  }

  @Override
  public Object getVal() {
    return val;
  }

  public UnzippedPointImpl setVal(final Object val) {
    this.val = val;
    return this;
  }
}
