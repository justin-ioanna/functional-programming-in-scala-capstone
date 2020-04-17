package observatory

import com.sksamuel.scrimage.Image

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
  * 2nd milestone: basic visualization
  */
object Visualization extends VisualizationInterface {

  import math.{abs, acos, cos, sin}

  val conf: SparkConf = new SparkConf()
    .set("spark.driver.host", "localhost")
    .set("spark.driver.allowMultipleContexts", "true")
    .setMaster("local")
    .setAppName("Visualization")

  val sc: SparkContext = new SparkContext(conf)

  val EarthRadius = 6371000

  private def toRadians(loc: Location): LocationRad = LocationRad(loc.lat * (math.Pi / 180), loc.lon * (math.Pi / 180))

  def greatCircleDistance(a: Location, b: Location): Distance = {

    val (ar, br) = (toRadians(a), toRadians(b))

    def isAntipodal: Boolean = a.lat == -b.lat && (a.lon == b.lon + 180 || a.lon == b.lon - 180)

    val thetaRadians: Double = {
      if (a == b) 0d
      else if (isAntipodal) math.Pi
      else acos((sin(ar.lat) * sin(br.lat)) + (cos(ar.lat) * cos(br.lat) * cos(abs(br.lon - ar.lon))))
    }

    thetaRadians * EarthRadius

  }

  private def inverseDistanceWeighting(temperatureByDistance: RDD[(Distance, Temperature)], power: Double): Temperature = {
    val minimumDistance = 1000
    val closeLocations = temperatureByDistance.filter({ case (distance, _) => distance < minimumDistance }).persist()

    if (closeLocations.count() > 0) {
      closeLocations.first()._2
    } else {
      val (numerator, denominator) = temperatureByDistance
        .map({ case (distance, temperature) => (math.pow(distance, power) * temperature, math.pow(distance, power)) })
        .reduce({ case ((numAcc, denomAcc), (num, denom)) => (numAcc + num, denomAcc + denom) })
      numerator / denominator
    }

  }

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Temperature)], location: Location): Temperature = {

    val temperatureByDistance: RDD[(Distance, Temperature)] =
      sc.parallelize(temperatures.toSeq)
      .map({case (stationLocation, temperature) => (greatCircleDistance(stationLocation, location), temperature)})
      .persist()

    inverseDistanceWeighting(temperatureByDistance, 2)

  }


  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Temperature, Color)], value: Temperature): Color = {
    ???
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)]): Image = {
    ???
  }

}

