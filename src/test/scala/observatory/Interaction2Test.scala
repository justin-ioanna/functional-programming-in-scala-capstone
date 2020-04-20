package observatory

import Interaction2._

import org.junit.Assert._
import org.junit.Test

trait Interaction2Test extends MilestoneSuite {
  private val milestoneTest = namedMilestoneTest("interactive user interface", 6) _

  private val years: Range = 1975 to 2015
  private val testLayer = Layer(LayerName.Temperatures, ColorScale.Temperatures, years)

  @Test def testYearBounds(): Unit = {
    val bounds: Signal[Range] = yearBounds(Signal(testLayer))
    assertEquals(years, bounds())
  }

  @Test def testYearSelectionExact(): Unit = {
    val year = yearSelection(Signal(testLayer), Signal(2000))
    assertEquals(2000, year())
  }

  @Test def testYearSelectionClosest(): Unit = {
    assertEquals(1975, yearSelection(Signal(testLayer), Signal(1960))())
    assertEquals(2015, yearSelection(Signal(testLayer), Signal(2020))())
  }

  @Test def testLayerUrlPattern(): Unit = {
    assertEquals("target/temperatures/2015/{z}/{x}-{y}.png", layerUrlPattern(Signal(testLayer), Signal(2015))())
  }

  @Test def testCaption(): Unit = {
    assertEquals("Temperatures (2015)", caption(Signal(testLayer), Signal(2015))())
  }

}
