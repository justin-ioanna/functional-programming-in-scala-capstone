package observatory

import Visualization.predictTemperature

/**
  * 4th milestone: value-added information
  */
object Manipulation extends ManipulationInterface {

  /**
    * @param temperatures Known temperatures
    * @return A function that, given a latitude in [-89, 90] and a longitude in [-180, 179],
    *         returns the predicted temperature at this location
    */
  def makeGrid(temperatures: Iterable[(Location, Temperature)]): GridLocation => Temperature = {
    val locations = for (lat <- -89 to 90; lon <- -180 to 179) yield (lat, lon)
    val gridTemperatures = locations.par
      .map({ case (lat, lon) => (GridLocation(lat, lon), predictTemperature(temperatures, Location(lat, lon))) }).toMap
    (gridLocation: GridLocation) => gridTemperatures(gridLocation)
  }

  /**
    * @param temperaturess Sequence of known temperatures over the years (each element of the collection
    *                      is a collection of pairs of location and temperature)
    * @return A function that, given a latitude and a longitude, returns the average temperature at this location
    */
  def average(temperaturess: Iterable[Iterable[(Location, Temperature)]]): GridLocation => Temperature = {
    val grids = temperaturess.map(makeGrid)
    (gridLocation: GridLocation) => {
      val temps = grids.map(func => func(gridLocation))
      temps.sum / temps.size
    }
  }

  /**
    * @param temperatures Known temperatures
    * @param normals A grid containing the “normal” temperatures
    * @return A grid containing the deviations compared to the normal temperatures
    */
  def deviation(temperatures: Iterable[(Location, Temperature)], normals: GridLocation => Temperature): GridLocation => Temperature = {
    val grid = makeGrid(temperatures)
    (gridLocation: GridLocation) => grid(gridLocation) - normals(gridLocation)
  }


}

