package observatory

import Interaction._

import scala.collection.concurrent.TrieMap
import org.junit.Assert._
import org.junit.Test

trait InteractionTest extends MilestoneSuite {
  private val milestoneTest = namedMilestoneTest("interactive visualization", 3) _

  @Test def testTileLocation(): Unit = {
    case class TestCase(tile: Tile, expectedLocation: Location, delta: Double)

    val testCases = List(
      TestCase(Tile(0, 0, 0), Location(85.0, -180.0), 0.1d),
      TestCase(Tile(0, 0, 1), Location(85.0, -180.0), 0.1d),
      TestCase(Tile(0, 0, 19), Location(85.0, -180.0), 0.1d),
      TestCase(Tile(0, 1, 1), Location(0.0, -180.0), 0.1d),
      TestCase(Tile(1, 0, 1), Location(85.0, 0.0), 0.1d),
      TestCase(Tile(1, 1, 1), Location(0.0, 0.0), 0.1d)
      )

    for (TestCase(tile, expectedLocation, delta) <- testCases) {
      val Location(expectedLat, expectedLon) = expectedLocation
      val Location(actualLat, actualLon) = tileLocation(tile)
      assertEquals(expectedLat, actualLat, delta)
      assertEquals(expectedLon, actualLon, delta)
    }
  }



}
