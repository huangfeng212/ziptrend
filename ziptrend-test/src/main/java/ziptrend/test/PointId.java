package ziptrend.test;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class PointId implements Serializable {
  private static final long serialVersionUID = 3192266477622883435L;
  private Long id;
  private Instant ts;

  public PointId(final Long id, final Instant ts) {
    this.id = id;
    this.ts = ts;
  }

  public PointId() {}

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Instant getTs() {
    return ts;
  }

  public void setTs(final Instant ts) {
    this.ts = ts;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final PointId pointId = (PointId) o;
    return Objects.equals(id, pointId.id) && Objects.equals(ts, pointId.ts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, ts);
  }
}
