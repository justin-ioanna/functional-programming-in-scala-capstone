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

  val temperatures: Seq[(Location, Temperature)] = Seq(
    (Location(30.000, 71.000), 20.0),
    (Location(40.000, 72.000), 25.0),
    (Location(35.000, 80.000), 35.0)
  )

  @Test def testPredictTemperature(): Unit = {

    case class TestCase(location: Location, expectedTemperature: Temperature, delta: Double)

    val testCases = List(
      TestCase(Location(30.000, 71.000), 20.0, 0d), // Point already present.
      TestCase(Location(40.001, 72.000), 25.0, 0d), // Point < 1km to nearest point.
      TestCase(Location(34.000, 75.000), 29.0, 0.5d)
    )

    for (TestCase(location, expectedTemperature, delta) <- testCases) {
      assertEquals(expectedTemperature, predictTemperature(temperatures, location), delta)
    }

  }

  val testColorScale: List[(Temperature, Color)] = List(
    (10, Color(255, 255, 255)),
    (0, Color(255, 0, 0)),
    (-10, Color(0, 0, 0)),
  )

  @Test def testInterpolateColor(): Unit = {

    case class TestCase(temperature: Temperature, expectedColor: Color, delta: Double)

    val testCases = List(
      TestCase(15, Color(255, 255, 255), 0d),
      TestCase(10, Color(255, 255, 255), 0d),
      TestCase(5, Color(255, 128, 128), 0d),
      TestCase(2, Color(255, 51, 51), 0d),
      TestCase(0, Color(255, 0, 0), 0d),
      TestCase(-2, Color(204, 0, 0), 0d),
      TestCase(-5, Color(128, 0, 0), 0d),
      TestCase(-10, Color(0, 0, 0), 0d),
      TestCase(-15, Color(0, 0, 0), 0d)
    )

    for (TestCase(temperature, expectedColor, delta) <- testCases) {
      val Color(expectedRed, expectedGreen, expectedBlue) = expectedColor
      val Color(actualRed, actualGreen, actualBlue) = interpolateColor(testColorScale, temperature)
      assertEquals(expectedRed, actualRed, delta)
      assertEquals(expectedGreen, actualGreen, delta)
      assertEquals(expectedBlue, actualBlue, delta)
    }

  }

  @Test def testVisualise(): Unit = {
    val image = visualize(temperatures, testColorScale)
    image.forall(
      (_, _, pixel) =>
        pixel.red >= 0 && pixel.red <= 255 &&
          pixel.green >= 0 && pixel.green <= 255 &&
            pixel.blue >= 0 && pixel.blue <= 255
    )

  }

}
