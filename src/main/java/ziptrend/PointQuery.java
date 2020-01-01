package ziptrend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class PointQuery {

  @Autowired ZipPointRepository zipPointRepository;
  @Autowired PointUnzip pointUnzip;

  List<RawPoint> query(final long id, final Instant start, final Instant end) {
    if (PointUtil.isQuarterSharp(start) && PointUtil.isQuarterSharp(end)) {
      final List<ZipPoint> byIdAndTsIsBetween =
          zipPointRepository.findByIdAndTsIsBetween(id, start, end);
      final List<RawPoint> unzipped = pointUnzip.unzip(byIdAndTsIsBetween);
      while (unzipped.get(unzipped.size() - 1).getTs().isAfter(end)) {
        unzipped.remove(unzipped.size() - 1);
      }
      return unzipped;
    } else {
      throw new IllegalArgumentException();
    }
  }
}
