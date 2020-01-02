package ziptrend.test;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnzippedPointEntityRepository extends JpaRepository<UnzippedPointEntity, PointId> {
  List<UnzippedPointEntity> findByIdOrderByTsAsc(long id);
}
