package ziptrend;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PointUnzip {

  public List<RawPoint> unzip(List<ZipPoint> zipPoints) {
    List<RawPoint> rawPoints = new ArrayList<>();
    zipPoints.forEach(zipPoint -> {
      if (zipPoint.getMult() == 1 && zipPoint.getSpan() == 0) {
        rawPoints.add(new RawPoint(zipPoint.getId() + 1, zipPoint.getTs(), zipPoint.getVal()));
      } else if (zipPoint.getMult() > 1 && zipPoint.getSpan() > 0) {
        for (int pos = 0; pos < zipPoint.getMult(); pos++) {
          rawPoints.add(new RawPoint(zipPoint.getId() + 1,
              zipPoint.getTs().plus(pos * zipPoint.getSpan(),
                  ChronoUnit.SECONDS), zipPoint.getVal()));
        }
      } else {
        throw new IllegalStateException("BANG");
      }
    });
    return rawPoints;
  }
}
