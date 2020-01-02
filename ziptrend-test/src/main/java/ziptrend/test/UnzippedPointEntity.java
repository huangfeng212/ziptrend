package ziptrend.test;

import java.time.Instant;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import ziptrend.UnzippedPoint;

@Entity
@IdClass(PointId.class)
public class UnzippedPointEntity implements UnzippedPoint {
  @Id private Long id;
  @Id private Instant ts;
  private Integer val;

  public UnzippedPointEntity(final Long id, final Instant ts, final Integer val) {
    this.id = id;
    this.ts = ts;
    this.val = val;
  }

  public UnzippedPointEntity() {}

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
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final UnzippedPointEntity that = (UnzippedPointEntity) o;
    return Objects.equals(id, that.id)
        && Objects.equals(ts, that.ts)
        && Objects.equals(val, that.val);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, ts, val);
  }

  @Override
  public String toString() {
    return id + "," + ts + "," + val;
  }
}
