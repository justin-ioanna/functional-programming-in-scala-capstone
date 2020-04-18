package observatory

import com.sksamuel.scrimage.{Image, Pixel}

import scala.math.round

/**
  * 2nd milestone: basic visualization
  */
object Visualization extends VisualizationInterface {

  import math.{abs, acos, cos, sin}

  val EarthRadius = 6371000

  private def toRadians(loc: Location): LocationRad = LocationRad(loc.lat * (math.Pi / 180), loc.lon * (math.Pi / 180))

  def greatCircleDistance(a: Location, b: Location): Distance = {
    def isAntipodal: Boolean = a.lat == -b.lat && abs(a.lon - b.lon) == 180

    val (ar, br) = (toRadians(a), toRadians(b))
    val thetaRadians: Double = {
      if (a == b) 0d
      else if (isAntipodal) math.Pi
      else acos((sin(ar.lat) * sin(br.lat)) + (cos(ar.lat) * cos(br.lat) * cos(abs(br.lon - ar.lon))))
    }
    thetaRadians * EarthRadius

  }

  private def inverseDistanceWeighting(temperatures: Iterable[(Distance, Temperature)], power: Int = 2): Temperature = {
    val (numerator, denominator) = temperatures
      .map({ case (dist, temp) => (1 / math.pow(dist, power) * temp, 1 / math.pow(dist, power)) })
      .reduce((a, b) => (a._1 + b._1, a._2 + b._2))
    numerator / denominator
  }

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Temperature)], location: Location): Temperature = {
    val temperatureByDistance = temperatures
      .map({case (stationLocation, temperature) => (greatCircleDistance(stationLocation, location), temperature)})
    if (temperatureByDistance.exists({ case (distance, _) => distance < 1000 })) {
      temperatureByDistance.filter({ case (distance, _) => distance < 1000 }).head._2
    } else {
      inverseDistanceWeighting(temperatureByDistance, 3)
    }
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Temperature, Color)], value: Temperature): Color = {
    def linearInterpolation(min: Int, max: Int, factor: Double): Int = round(min + (max - min) * factor).toInt
    def interpolate(value: Temperature, tMin: Temperature, tMax: Temperature, cMin: Color, cMax: Color): Color = {
      val factor = (value - tMin) / (tMax - tMin)
      Color(
        red = linearInterpolation(cMin.red, cMax.red, factor),
        green = linearInterpolation(cMin.green, cMax.green, factor),
        blue = linearInterpolation(cMin.blue, cMax.blue, factor)
      )
    }

    val (lowerPairs, higherPairs) = points.toList.sortBy(_._1).partition(_._1 <= value)
    if (lowerPairs.isEmpty) higherPairs.head._2
    else if (higherPairs.isEmpty) lowerPairs.last._2
    else {
      val (tMin, cMin) = lowerPairs.last
      val (tMax, cMax) = higherPairs.head
      interpolate(value, tMin, tMax, cMin, cMax)
    }

  }

  private def indexToLocation(width: Int, height: Int, index: Int): Location = {
    val widthScale = 360d / width
    val heightScale = 180d / height
    val lat = 90 - ((index / 360) * heightScale)
    val lon = ((index % 360) * widthScale) - 180
    Location(lat, lon)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)]): Image = {
    val (width, height) = (360, 180)
    val pixels = (0 until width * height).par
      .map(index => indexToLocation(width, height, index))
      .map(location => predictTemperature(temperatures, location))
      .map(temperature => interpolateColor(colors, temperature))
      .map(color => Pixel(color.red, color.green, color.blue, 255))
      .toArray
    Image(width, height, pixels)
  }

}

