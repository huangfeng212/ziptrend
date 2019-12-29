package ziptrend;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RawPointRepository extends JpaRepository<RawPoint, PointId> {}
