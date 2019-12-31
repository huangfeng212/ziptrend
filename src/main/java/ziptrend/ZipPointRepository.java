package ziptrend;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZipPointRepository extends JpaRepository<ZipPoint, PointId> {

  List<ZipPoint> findByIdAndTsIsBetween(long id, Instant start, Instant end);
}
