package ziptrend;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
  @Autowired
  PointQuery pointQuery;

  static Stream<Arguments> percentage1() {
    return Stream.of(
//    Arguments.of(1,200, 0),
     Arguments.of(2,200, 5)
//      Arguments.of(3,200, 10),
//        Arguments.of(4,200, 15),
//        Arguments.of(5,200, 20),
//        Arguments.of(6,200, 25),
//        Arguments.of(7,200, 30)
        );
  }
  static Stream<Arguments> percentage2() {
//    return Stream.of(Arguments.of(11,50, 5), Arguments.of(12,100, 5), Arguments.of(13,200, 5),
//        Arguments.of(14,250, 5),
//        Arguments.of(15,300, 5),
//        Arguments.of(16,350, 5),Arguments.of(17,400, 5));
    return Stream.of();
  }

  @BeforeEach public void beforeEach() {
    rawPointRepository.deleteAll();
    zipPointRepository.deleteAll();
  }

  @ParameterizedTest(name = "change {0}/1000, burst {1}/1000")
  @MethodSource(value = {"percentage1","percentage2"})
  void zipPointTest(int id, int pChange, int pBurst) {
    final List<RawPoint> generated = pointGen.generate(id, Instant.ofEpochSecond(1577801783L,123456000L), 24, pChange, pBurst);
    System.out.println("original size: " + generated.size());
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

    final List<RawPoint> query = pointQuery
        .query(id, Instant.ofEpochSecond(1577801753L),
            Instant.ofEpochSecond(1577805353L));
    System.out.println(query);
    System.out
        .println(String.format("compression ratio: %.2f",
            generated.size() * 1.0 / zipped.size()));
    System.out
        .println(String.format("space savings: %.2f",
            1.0 - (zipped.size() *5 * 1.0) / (generated.size()*3)));

  }
}
