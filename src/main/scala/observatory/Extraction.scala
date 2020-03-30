package observatory

import java.time.LocalDate
import scala.io.Source

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
  * 1st milestone: data extraction
  */
object Extraction extends ExtractionInterface {

  val conf: SparkConf = new SparkConf()
    .set("spark.driver.host", "localhost")
    .setMaster("local")
    .setAppName("Extraction")

  val sc: SparkContext = new SparkContext(conf)

  private def readLines(filePath: String): List[String] =
    Option(getClass.getResourceAsStream(filePath)) match {
      case Some(resource) => Source.fromInputStream(resource).getLines().toList
      case None           => sys.error(s"File path $filePath not found")
    }

  def parseLocationRecord(line: String): LocationRecord = {
    val arr = line.split(",", -1)
    LocationRecord(
      station = Station(if (arr(0) == "") None else Some(arr(0)), if (arr(1) == "") None else Some(arr(1))),
      location =
        if (arr(2) == "" || arr(3) == "") None
        else Some(Location(arr(2).toDouble, arr(3).toDouble))
    )
  }

  def parseTemperatureRecord(line: String, year: Year): TemperatureRecord = {
    val arr = line.split(",", -1)
    TemperatureRecord(
      station = Station(if (arr(0) == "") None else Some(arr(0)), if (arr(1) == "") None else Some(arr(1))),
      date = LocalDate.of(year, arr(2).toInt, arr(3).toInt),
      temperature = if (arr(4) == "9999.9") None else Some(fahrenheitToCelsius(arr(4).toDouble))
    )
  }

  def fahrenheitToCelsius(fahrenheit: Double): Temperature = (fahrenheit - 32) * (5d / 9d)

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(
      year: Year,
      stationsFile: String,
      temperaturesFile: String
  ): Iterable[(LocalDate, Location, Temperature)] = {

    val locations: RDD[(Station, Location)] =
      sc.parallelize(readLines(stationsFile))
        .map(parseLocationRecord)
        .flatMap(record =>
          record.location match {
            case Some(location) => Some((record.station, location))
            case None           => None
          }
        )

    val temperatures: RDD[(Station, (LocalDate, Temperature))] =
      sc.parallelize(readLines(temperaturesFile))
        .map(line => parseTemperatureRecord(line, year))
        .flatMap(record =>
          record.temperature match {
            case Some(temperature) => Some((record.station, (record.date, temperature)))
            case None              => None
          }
        )

    temperatures
      .join(locations)
      .map({ case (_, ((date, temperature), location)) => (date, location, temperature) })
      .collect()

  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Temperature)]): Iterable[(Location, Temperature)] =
    sc.parallelize(records.toSeq)
      .map({case (_, location, temperature) => (location, (temperature, 1))})
      .reduceByKey((v1, v2) => (v1._1 + v2._1, v1._2 + v2._2))
      .mapValues({case (temperature, days) => temperature / days})
      .collect()

}
