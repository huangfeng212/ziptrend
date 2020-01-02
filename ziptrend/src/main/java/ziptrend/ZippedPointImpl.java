package ziptrend;

import java.time.Instant;

class ZippedPointImpl implements ZippedPoint {
  Instant ts;
  Object val;
  Integer mult;
  Long span;

  @Override
  public Instant getTs() {
    return ts;
  }

  public ZippedPointImpl setTs(final Instant ts) {
    this.ts = ts;
    return this;
  }

  @Override
  public Object getVal() {
    return val;
  }

  public ZippedPointImpl setVal(final Object val) {
    this.val = val;
    return this;
  }

  @Override
  public Integer getMult() {
    return mult;
  }

  public ZippedPointImpl setMult(final Integer mult) {
    this.mult = mult;
    return this;
  }

  @Override
  public Long getSpan() {
    return span;
  }

  public ZippedPointImpl setSpan(final Long span) {
    this.span = span;
    return this;
  }
}
