package observatory

import Extraction._
import Visualization._

object Main extends App {

  val temperatureRecords = locateTemperatures(2015, "/stations.csv", "/2015.csv")
  val averageTemperatures = locationYearlyAverageRecords(temperatureRecords)

  val colors: List[(Temperature, Color)] = List(
    (60, Color(255, 255, 255)),
    (32, Color(255, 0, 0)),
    (12, Color(255, 255, 0)),
    (0, Color(0, 255, 255)),
    (-15, Color(0, 0, 255)),
    (-27, Color(255, 0, 255)),
    (-50, Color(33, 0, 107)),
    (-60, Color(0, 0, 0)),
  )

  val image = visualize(averageTemperatures, colors)

  image.output(new java.io.File("target/some-image.png"))

}
