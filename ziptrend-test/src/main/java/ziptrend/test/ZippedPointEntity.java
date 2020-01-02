package ziptrend.test;

import java.time.Instant;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import ziptrend.ZippedPoint;

@Entity
@IdClass(PointId.class)
public class ZippedPointEntity implements ZippedPoint {
  @Id private Long id;
  @Id private Instant ts;
  private Integer val;
  private Integer mult = 1; // default just itself
  private Long span = 0L; // default  just itself

  public ZippedPointEntity(
      final Long id, final Instant ts, final Integer val, final Integer mult, final Long span) {
    this.id = id;
    this.ts = ts;
    this.val = val;
    this.mult = mult;
    this.span = span;
  }

  public ZippedPointEntity() {}

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  @Override
  public Instant getTs() {
    return ts;
  }

  public void setTs(final Instant ts) {
    this.ts = ts;
  }

  @Override
  public Integer getVal() {
    return val;
  }

  public void setVal(final Integer val) {
    this.val = val;
  }

  @Override
  public Integer getMult() {
    return mult;
  }

  public void setMult(final Integer mult) {
    this.mult = mult;
  }

  @Override
  public Long getSpan() {
    return span;
  }

  public void setSpan(final Long span) {
    this.span = span;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final ZippedPointEntity zippedPointEntity = (ZippedPointEntity) o;
    return Objects.equals(id, zippedPointEntity.id)
        && Objects.equals(ts, zippedPointEntity.ts)
        && Objects.equals(val, zippedPointEntity.val)
        && Objects.equals(mult, zippedPointEntity.mult)
        && Objects.equals(span, zippedPointEntity.span);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, ts, val, mult, span);
  }
}
