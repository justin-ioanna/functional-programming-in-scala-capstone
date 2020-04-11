package observatory

import java.time.LocalDate

/**
  * Introduced in Week 1. Compound key that uniquely identifies a station.
  * @param stn Station identifier.
  * @param wban Weather Bureau Army Navy identifier.
  */
case class Station(stn: Option[String], wban: Option[String])

/**
  * Introduced in Week 1. Represents a parsed record from a stations file.
  * @param station Compound key to identify a station.
  * @param location Location on the globe.
  */
case class LocationRecord(station: Station, location: Option[Location]) extends Serializable

/**
  * Introduced in Week 1. Represents a parsed record from a temperatures file.
  * @param station Compound key to identify a station.
  * @param date Date the temperate was recorded on.
  * @param temperature in Celsius.
  */
case class TemperatureRecord(station: Station, date: LocalDate, temperature: Option[Temperature]) extends Serializable

/**
  * Introduced in Week 1. Represents a location on the globe.
  * @param lat Degrees of latitude, -90 ≤ lat ≤ 90
  * @param lon Degrees of longitude, -180 ≤ lon ≤ 180
  */
case class Location(lat: Double, lon: Double)

/**
  * Introduced in Week 2. Represents a location on the globe.
  * @param lat Radians of latitude, -π/2 ≤ lat ≤ π/2
  * @param lon Radians of latitude, -π ≤ lon ≤ π
  */
case class LocationRad(lat: Double, lon: Double)

/**
  * Introduced in Week 3. Represents a tiled web map tile.
  * See https://en.wikipedia.org/wiki/Tiled_web_map
  * Based on http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
  * @param x X coordinate of the tile
  * @param y Y coordinate of the tile
  * @param zoom Zoom level, 0 ≤ zoom ≤ 19
  */
case class Tile(x: Int, y: Int, zoom: Int)

/**
  * Introduced in Week 4. Represents a point on a grid composed of
  * circles of latitudes and lines of longitude.
  * @param lat Circle of latitude in degrees, -89 ≤ lat ≤ 90
  * @param lon Line of longitude in degrees, -180 ≤ lon ≤ 179
  */
case class GridLocation(lat: Int, lon: Int)

/**
  * Introduced in Week 5. Represents a point inside of a grid cell.
  * @param x X coordinate inside the cell, 0 ≤ x ≤ 1
  * @param y Y coordinate inside the cell, 0 ≤ y ≤ 1
  */
case class CellPoint(x: Double, y: Double)

/**
  * Introduced in Week 2. Represents an RGB color.
  * @param red Level of red, 0 ≤ red ≤ 255
  * @param green Level of green, 0 ≤ green ≤ 255
  * @param blue Level of blue, 0 ≤ blue ≤ 255
  */
case class Color(red: Int, green: Int, blue: Int)
