package com.glyde.exporter.prometheus

sealed trait Metric {

  var measurements: Map[Map[String, String], Double] = Map.empty

  def name: String
  def help: Option[String]
  def `type`: String

  def remove(tags: Map[String, String]): Unit = measurements -= tags

  def formatOutput: String = if (measurements.isEmpty) "" else
    s"""|# TYPE $name ${`type`}
        |${measurements.map { case (tags, v) => s"""$name{${formatTag(tags)}} $v""" }.mkString("\n")}""".stripMargin

  private def formatTag(tags: Map[String, String]) =
    tags.map { case (k, v) => s"""$k="$v"""" }.mkString(",")

}

case class Gauge(name: String, help: Option[String] = None) extends Metric {
  override val `type`: String = "gauge"

  def update(tags: Map[String, String], value: Double): Unit = measurements += (tags -> value)

}
