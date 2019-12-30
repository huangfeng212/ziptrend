package ziptrend;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TrendApplicationTests {

  @Autowired
  RawPointRepository rawPointRepository;
  @Autowired
  ZipPointRepository zipPointRepository;
  @Autowired
  PointGen pointGen;
  @Autowired
  PointAlign pointAlign;
  @Autowired
  PointZip pointZip;
  @Autowired
  PointUnzip pointUnzip;

  static Stream<Arguments> percentage1() {
    return Stream.of(Arguments.of(200, 5), Arguments.of(200, 10), Arguments.of(200, 15),
        Arguments.of(200, 20));
  }

  static Stream<Arguments> percentage2() {
    return Stream.of(Arguments.of(200, 10), Arguments.of(300, 10), Arguments.of(400, 10),
        Arguments.of(500, 10));
  }

  @ParameterizedTest(name = "change {0}/1000, burst {1}/1000")
  @MethodSource(value = {"percentage1", "percentage2"})
  void zipPointTest(int pChange, int pBurst) {
    final List<RawPoint> generated = pointGen.generate(1, Instant.now(), 24, pChange, pBurst);
    System.out.println("original size: " + generated.size());
    rawPointRepository.deleteAll();
    rawPointRepository.saveAll(generated);

    final List<RawPoint> saved = rawPointRepository.findAll();
    final List<RawPoint> aligned = pointAlign.align(saved);
    final List<ZipPoint> zipped = pointZip.zip(aligned);
    System.out.println("zipped size: " + zipped.size());
    zipPointRepository.saveAll(zipped);

    final List<RawPoint> unzip = pointUnzip.unzip(zipped);
    System.out.println("unzipped size: " + unzip.size());
    rawPointRepository.saveAll(unzip);

    Assertions.assertEquals(generated.size(), unzip.size());
    for (int i = 0; i < generated.size(); i++) {
      final RawPoint rawPoint = generated.get(i);
      final RawPoint unzipped = unzip.get(i);
      Assertions.assertEquals(rawPoint.getVal(), unzipped.getVal());
      Assertions.assertEquals(rawPoint.getTs().truncatedTo(ChronoUnit.SECONDS),
          unzipped.getTs().truncatedTo(ChronoUnit.SECONDS));
    }
    System.out
        .println(String.format("compression ratio: %.2f%%",
            (generated.size() - zipped.size()) * 100.0 / zipped.size()));
    System.out
        .println(String.format("space savings: %.2f%%",
            1 - (zipped.size()) * 100.0 / (generated.size() - zipped.size())));
  }
}
