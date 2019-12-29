package ziptrend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

@SpringBootTest
class TrendApplicationTests {
  @Autowired RawPointRepository rawPointRepository;
  @Autowired ZipPointRepository zipPointRepository;
  @Autowired PointGen pointGen;
  @Autowired PointAlign pointAlign;
  @Autowired PointZip pointZip;

  @Test
  void zipPointTest() {
    final List<RawPoint> generated = pointGen.generate(1, Instant.now(), 24);
    rawPointRepository.deleteAll();
    rawPointRepository.saveAll(generated);

    final List<RawPoint> saved = rawPointRepository.findAll();
    System.out.println(saved.size());
    final List<RawPoint> aligned = pointAlign.align(saved);
    final List<ZipPoint> zipped = pointZip.zip(aligned);
    System.out.println(zipped.size());
    zipPointRepository.saveAll(zipped);
  }
}
