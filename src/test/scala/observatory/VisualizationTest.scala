package observatory

import org.junit.Assert._
import org.junit.Test

trait VisualizationTest extends MilestoneSuite {

  private val milestoneTest = namedMilestoneTest("raw data display", 2) _

  import Visualization._

  @Test def testGreatCircleDistance(): Unit = {
    case class TestCase(a: Location, b: Location, expectedDistance: Distance, delta: Double)

    val testCases = List(
      TestCase(Location(38.000, -77.000), Location(38.000, -77.000), 0, 0d),                        // Locations equal
      TestCase(Location(38.000, -80.000), Location(-38.000, 100.000), math.Pi * EarthRadius, 0d),   // Antipodal
      TestCase(Location(38.000, 100.000), Location(-38.000, -80.000), math.Pi * EarthRadius, 0d),   // Antipodal
      TestCase(Location(38.000, 100.000), Location(39.000, 101.000), 141190, 10d),
      TestCase(Location(35.000, 70.000), Location(70.000, 140.000), 5625820, 10d)
    )

    for (TestCase(a, b, expectedDistance, delta) <- testCases) {
      assertEquals(expectedDistance, greatCircleDistance(a, b), delta)
    }
  }


  @Test def testPredictTemperature(): Unit = {

    val temperatures: Seq[(Location, Temperature)] = Seq(
      (Location(30.000, 71.000), 20.0),
      (Location(40.000, 72.000), 25.0),
      (Location(35.000, 80.000), 35.0)
    )

    case class TestCase(location: Location, expectedTemperature: Temperature, delta: Double)

    val testCases = List(
      TestCase(Location(30.000, 71.000), 20.0, 0d), // Point already present.
      TestCase(Location(40.001, 72.000), 25.0, 0d), // Point < 1km to nearest point.
      TestCase(Location(34.000, 75.000), 25.48, 0.01d)
    )

    for (TestCase(location, expectedTemperature, delta) <- testCases) {
      assertEquals(expectedTemperature, predictTemperature(temperatures, location), delta)
    }

  }


}
