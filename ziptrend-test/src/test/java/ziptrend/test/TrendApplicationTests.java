package ziptrend.test;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ziptrend.UnzippedPoint;
import ziptrend.UnzippingTrend;
import ziptrend.ZipTrendUtil;
import ziptrend.ZippedPoint;
import ziptrend.ZippingTrend;

@SpringBootTest
class TrendApplicationTests {

  @Autowired UnzippedPointEntityRepository unzippedPointEntityRepository;
  @Autowired ZippedPointEntityRepository zippedPointEntityRepository;
  @Autowired PointGen pointGen;

  static Stream<Arguments> percentage() {
    return Stream.of(
        //    Arguments.of(1,200, 0),
        Arguments.of(2, 200, 5)
        //      Arguments.of(3,200, 10),
        //        Arguments.of(4,200, 15),
        //        Arguments.of(5,200, 20),
        //        Arguments.of(6,200, 25),
        //        Arguments.of(7,200, 30)
        );
  }

  @BeforeEach
  public void beforeEach() {
    unzippedPointEntityRepository.deleteAll();
    zippedPointEntityRepository.deleteAll();
  }

  @ParameterizedTest(name = "id {0}, change {1}/1000, burst {2}/1000")
  @MethodSource(value = {"percentage"})
  void zipPointTest(final long id, final int pChange, final int pBurst) {
    // store raw
    final List<UnzippedPointEntity> generated =
        pointGen.generate(id, Instant.parse("2019-12-31T08:00:12.345Z"), 24, pChange, pBurst);
    System.out.println("original rows: " + generated.size());
    unzippedPointEntityRepository.saveAll(generated);
    // retrieve raw and zip then save
    final List<UnzippedPointEntity> savedRaw =
        unzippedPointEntityRepository.findByIdOrderByTsAsc(id);
    final ZippingTrend zippingTrend = new ZippingTrend(id, savedRaw);
    final List<ZippedPoint> zipped = zippingTrend.zip();
    final List<ZippedPointEntity> zippedPointEntities =
        zipped.stream()
            .map(
                zippedPoint ->
                    new ZippedPointEntity(
                        id,
                        zippedPoint.getTs(),
                        (Integer) zippedPoint.getVal(),
                        zippedPoint.getMult(),
                        zippedPoint.getSpan()))
            .collect(Collectors.toList());
    System.out.println("zipped rows: " + zippedPointEntities.size());
    zippedPointEntityRepository.saveAll(zippedPointEntities);
    // retrieve zipped and unzip
    final List<ZippedPointEntity> savedZipped =
        zippedPointEntityRepository.findByIdOrderByTsAsc(id);
    final UnzippingTrend unzippingTrend = new UnzippingTrend(id, savedZipped);
    final List<UnzippedPoint> unzippedPoints = unzippingTrend.unzip();
    final List<UnzippedPointEntity> unzippedPointEntities =
        unzippedPoints.stream()
            .map(
                unzippedPoint ->
                    new UnzippedPointEntity(
                        id, unzippedPoint.getTs(), (Integer) unzippedPoint.getVal()))
            .collect(Collectors.toList());
    System.out.println("unzipped rows: " + unzippedPoints.size());

    Assertions.assertEquals(generated.size(), unzippedPoints.size());
    for (int i = 0; i < generated.size(); i++) {
      final UnzippedPointEntity rawPointEntity = generated.get(i);
      final UnzippedPointEntity unzippedPointEntity = unzippedPointEntities.get(i);
      Assertions.assertEquals(rawPointEntity.getVal(), unzippedPointEntity.getVal());
      Assertions.assertEquals(
          rawPointEntity.getTs().truncatedTo(ChronoUnit.SECONDS),
          unzippedPointEntity.getTs().truncatedTo(ChronoUnit.SECONDS));
    }
    final List<ZippedPointEntity> queryZipped =
        zippedPointEntityRepository.findByIdAndTsIsBetween(
            id, Instant.parse("2019-12-31T09:00:00Z"), Instant.parse("2019-12-31T10:00:00Z"));
    final UnzippingTrend unzippingTrend1 = new UnzippingTrend(id, queryZipped);
    final List<UnzippedPoint> queryUnzipped = unzippingTrend1.unzip();
    ZipTrendUtil.trimEnd(queryUnzipped, Instant.parse("2019-12-31T10:00:00Z"));
    Assertions.assertEquals(9, queryUnzipped.get(0).getTs().atZone(ZoneOffset.UTC).getHour());
    Assertions.assertEquals(0, queryUnzipped.get(0).getTs().atZone(ZoneOffset.UTC).getMinute());
    Assertions.assertEquals(
        9, queryUnzipped.get(queryUnzipped.size() - 1).getTs().atZone(ZoneOffset.UTC).getHour());
    Assertions.assertEquals(
        59, queryUnzipped.get(queryUnzipped.size() - 1).getTs().atZone(ZoneOffset.UTC).getMinute());
  }
}
