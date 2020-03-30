package observatory

import java.time.LocalDate
import org.junit.Assert._
import org.junit._

trait ExtractionTest extends MilestoneSuite {

  private val milestoneTest = namedMilestoneTest("data extraction", 1) _

  import Extraction._

  @Test def testParseLocationRecord(): Unit = {

    case class TestCase(line: String, locationRecord: LocationRecord)

    val testCases = List(
      TestCase(
        "010013,,,",
        LocationRecord(Station(Some("010013"), None), None)
      ),
      TestCase(
        "724017,03707,+37.358,-078.438",
        LocationRecord(Station(Some("724017"), Some("03707")), Some(Location(37.358d, -78.438d)))
      ),
      TestCase(
        "724017,,+37.350,-078.433",
        LocationRecord(Station(Some("724017"), None), Some(Location(37.350d, -78.433d)))
      )
    )

    for (TestCase(line, locationRecord) <- testCases) {
      assertEquals(locationRecord, parseLocationRecord(line))
    }

  }

  @Test def testParseTemperatureRecord(): Unit = {

    val year = 2015

    case class TestCase(line: String, temperatureRecord: TemperatureRecord)

    val testCases = List(
      TestCase(
        "010013,,11,25,39.2",
        TemperatureRecord(Station(Some("010013"), None), LocalDate.of(year, 11, 25), Some(fahrenheitToCelsius(39.2d)))
      ),
      TestCase(
        "724017,,08,11,81.14",
        TemperatureRecord(Station(Some("724017"), None), LocalDate.of(year, 8, 11), Some(fahrenheitToCelsius(81.14d)))
      ),
      TestCase(
        "724017,03707,12,06,32",
        TemperatureRecord(Station(Some("724017"), Some("03707")), LocalDate.of(year, 12, 6), Some(fahrenheitToCelsius(32d)))
      ),
      TestCase(
        "724017,03707,01,29,35.6",
        TemperatureRecord(Station(Some("724017"), Some("03707")), LocalDate.of(year, 1, 29), Some(fahrenheitToCelsius(35.6d)))
      ),
      TestCase(
        "724017,03707,01,29,9999.9",
        TemperatureRecord(Station(Some("724017"), Some("03707")), LocalDate.of(year, 1, 29), None)
      )
    )

    for (TestCase(line, temperatureRecord) <- testCases) {
      assertEquals(temperatureRecord, parseTemperatureRecord(line, year))
    }

  }

  @Test def testLocateTemperatures(): Unit = {
    val expected = List(
      (LocalDate.of(2015, 12, 6), Location(37.358, -78.438), 0.0),
      (LocalDate.of(2015, 1, 29), Location(37.358, -78.438), 2.000000000000001),
      (LocalDate.of(2015, 8, 11), Location(37.35, -78.433), 27.3)
    )
    assertEquals(
      expected,
      locateTemperatures(2015, "/test/stations.csv", "/test/2015.csv").toList.sortBy(_._3)
    )
  }

  @Test def testLocationYearlyAverageRecords(): Unit = {
    val records = List(
      (LocalDate.of(2015, 8, 11), Location(37.35, -78.433), 27.3),
      (LocalDate.of(2015, 12, 6), Location(37.358, -78.438), 0.0),
      (LocalDate.of(2015, 1, 29), Location(37.358, -78.438), 2.000000000000001)
    )
    val expected = List(
      (Location(37.358, -78.438), 1.0000000000000004),
      (Location(37.35, -78.433), 27.3)
    )
    assertEquals(expected, locationYearlyAverageRecords(records).toList.sortBy(_._2))
  }

  @Rule def individualTestTimeout = new org.junit.rules.Timeout(100 * 1000)
}
