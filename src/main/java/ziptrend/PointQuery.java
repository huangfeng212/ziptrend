package ziptrend;

import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointQuery {

  @Autowired
  ZipPointRepository zipPointRepository;
  @Autowired
  PointUnzip pointUnzip;

  List<RawPoint> query(long id, Instant start, Instant end) {
    if (PointUtil.isQuarterSharp(start) && PointUtil.isQuarterSharp(end)) {
      final List<ZipPoint> byIdAndTsIsBetween = zipPointRepository
          .findByIdAndTsIsBetween(id, start, end);
     return pointUnzip.unzip(byIdAndTsIsBetween);
    }else {
      throw new IllegalArgumentException();
    }
  }
}
