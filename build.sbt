name := "kafka-siren"
scalaVersion := "2.12.11"

scalacOptions += "-Ypartial-unification"

enablePlugins(JavaServerAppPackaging, DockerComposePlugin, ReleasePlugin)

DockerSettings.common
ReleaseSettings.common
Defaults.itSettings
configs(IntegrationTest)

libraryDependencies ++= Seq(
  "org.typelevel"                %% "cats-effect"           % "2.1.3",
  "org.typelevel"                %% "cats-core"             % "2.1.1",
  "com.github.fd4s"              %% "fs2-kafka"             % "1.0.0",
  "io.kamon"                     %% "kamon-core"            % "2.1.0",
  "io.kamon"                     %% "kamon-prometheus"      % "2.1.0",
  "com.github.pureconfig"        %% "pureconfig"            % "0.12.3",
  "ch.qos.logback"               % "logback-classic"        % "1.2.3",
  "com.typesafe.scala-logging"   %% "scala-logging"         % "3.9.2",
  "org.apache.kafka"             % "kafka-clients"          % "2.5.0" % "test,it",
  "org.scalatest"                %% "scalatest"             % "3.0.8" % "test,it",
  "org.scalacheck"               %% "scalacheck"            % "1.13.5" % "test,it",
  "com.danielasfregola"          %% "random-data-generator" % "2.8" % "test,it",
  "io.github.embeddedkafka"      %% "embedded-kafka"        % "2.5.0" % "test,it",
  "com.softwaremill.sttp.client" %% "core"                  % "2.1.1" % "test,it"
)
