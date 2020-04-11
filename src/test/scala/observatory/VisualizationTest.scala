package observatory

import org.junit.Assert._
import org.junit.Test

trait VisualizationTest extends MilestoneSuite {

  private val milestoneTest = namedMilestoneTest("raw data display", 2) _

  import Visualization._

  @Test def testGreatCircleDistance(): Unit = {

    case class TestCase(a: Location, b: Location, distance: Distance)

    val testCases = List(
      TestCase(Location(50.0, -40.0), Location(50.0, -40.0), 0),
      TestCase(Location(50.0, -40.0), Location(50.0, 40.0), math.Pi * EarthRadius),
      TestCase(Location(50.0, -40.0), Location(50.0, 140.0), math.Pi * EarthRadius),
      TestCase(Location(50.0, 40.0), Location(50.0, -140.0), math.Pi * EarthRadius),
      TestCase(Location(50.0, -40.0), Location(60.0, -30.0), 0)
    )

    for (TestCase(a, b, distance) <- testCases) {
      distance == greatCircleDistance(a, b)
    }
  }

//  @Test def testPredictTemperature(): Unit = {
//
//    val temperatureByLocation: Seq[(Location, Temperature)] =
//      Seq(
//        (Location(37.358, -78.438), 1.0),
//        (Location(37.368, -77.438), 4.0),
//        (Location(37.35, -78.433), 27.3)
//      )
//
//    case class TestCase(location: Location, expected: Temperature)
//
//    val testCases = List(
//      TestCase(Location(37.358, -78.438), 1.0), // Location already present
//      (Location(41.368, -76.438), 4.0),
//      (Location(37.368, -77.438), 4.0)
//    )
//
//    for ((location, expected) <- test
//
//  }
//
//  @Test def testInterpolateColor(): Unit = {}
//
//  @Test def testVisualize(): Unit = {}

}
