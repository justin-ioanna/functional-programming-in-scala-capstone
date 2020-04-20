package observatory

import math.{atan, Pi, pow, sinh}

import com.sksamuel.scrimage.{Image, Pixel}

import Visualization.{predictTemperature, interpolateColor}

/**
  * 3rd milestone: interactive visualization
  */
object Interaction extends InteractionInterface {

  /**
    * @param tile Tile coordinates
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(tile: Tile): Location = {
    val n = pow(2, tile.zoom)
    val lon = (tile.x * 360.0 / n)  - 180
    val lat = atan(sinh(Pi - (tile.y * 2 * Pi / n))) * 180 / Pi
    Location(lat, lon)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @param tile Tile coordinates
    * @return A 256×256 image showing the contents of the given tile
    */
  def tile(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)], tile: Tile): Image = {
    val (width, height, power) = (256, 256, 8)
    val (xOffset, yOffset) = (tile.x * pow(2, power).toInt, tile.y * pow(2, power).toInt)
    val subTiles = for (y <- 0 until height; x <- 0 until width) yield Tile(x + xOffset, y + yOffset, power + tile.zoom)

    val pixels: Array[Pixel] =
      subTiles
        .map(tile => tileLocation(tile))
        .map(location => predictTemperature(temperatures, location))
        .map(temperature => interpolateColor(colors, temperature))
        .map(color => Pixel(color.red, color.green, color.blue, 127))
        .toArray
    Image(width, height, pixels)
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    * @param yearlyData Sequence of (year, data), where `data` is some data associated with
    *                   `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](yearlyData: Iterable[(Year, Data)], generateImage: (Year, Tile, Data) => Unit): Unit = {
    val yearlyDataTiles = for (
      zoom <- 0 to 3;
      x <- 0 until pow(2, zoom).toInt;
      y <- 0 until pow(2, zoom).toInt;
      (year, data) <- yearlyData
    ) yield (year, Tile(x, y, zoom), data)

    yearlyDataTiles.par.map({case (year, tile, data) => generateImage(year, tile, data)})

  }

}
