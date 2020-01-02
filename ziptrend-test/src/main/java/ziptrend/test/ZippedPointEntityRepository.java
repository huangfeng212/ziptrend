package ziptrend.test;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZippedPointEntityRepository extends JpaRepository<ZippedPointEntity, PointId> {
  List<ZippedPointEntity> findByIdOrderByTsAsc(long id);

  List<ZippedPointEntity> findByIdAndTsIsBetween(long id, Instant start, Instant end);
}
