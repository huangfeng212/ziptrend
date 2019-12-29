package ziptrend;

import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PointAlign {
  public List<RawPoint> align(final List<RawPoint> pointEntities) {
    // erase sub-seconds

    return pointEntities.stream()
        .map(
            pointEntity ->
                new RawPoint(
                    pointEntity.getId(),
                    pointEntity.getTs().truncatedTo(ChronoUnit.SECONDS),
                    pointEntity.getVal()))
        .collect(Collectors.toList());
  }
}
