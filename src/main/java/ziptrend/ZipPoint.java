package ziptrend;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.time.Instant;
import java.util.Objects;

@Entity
@IdClass(PointId.class)
public class ZipPoint {
  @Id private Long id;
  @Id private Instant ts;
  private Integer val;
  private Integer mult = 1; // default just itself
  private Long span = 0L; // default  just itself

  public ZipPoint(
      final Long id, final Instant ts, final Integer val, final Integer mult, final Long span) {
    this.id = id;
    this.ts = ts;
    this.val = val;
    this.mult = mult;
    this.span = span;
  }

  public ZipPoint() {}

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

  public Integer getVal() {
    return val;
  }

  public void setVal(final Integer val) {
    this.val = val;
  }

  public Integer getMult() {
    return mult;
  }

  public void setMult(final Integer mult) {
    this.mult = mult;
  }

  public Long getSpan() {
    return span;
  }

  public void setSpan(final Long span) {
    this.span = span;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final ZipPoint zipPoint = (ZipPoint) o;
    return Objects.equals(id, zipPoint.id)
        && Objects.equals(ts, zipPoint.ts)
        && Objects.equals(val, zipPoint.val)
        && Objects.equals(mult, zipPoint.mult)
        && Objects.equals(span, zipPoint.span);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, ts, val, mult, span);
  }
}
