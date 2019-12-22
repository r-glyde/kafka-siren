name := "kafka-siren"
scalaVersion := "2.12.10"

scalacOptions += "-Ypartial-unification"

enablePlugins(JavaServerAppPackaging, DockerComposePlugin, ReleasePlugin)

DockerSettings.common
Defaults.itSettings
configs(IntegrationTest)

libraryDependencies ++= Seq(
  "org.typelevel"                %% "cats-effect"      % "2.0.0",
  "org.typelevel"                %% "cats-core"        % "2.0.0",
  "com.ovoenergy"                %% "fs2-kafka"        % "0.20.2",
  "io.kamon"                     %% "kamon-core"       % "1.1.6",
  "io.kamon"                     %% "kamon-prometheus" % "1.1.2",
  "com.github.pureconfig"        %% "pureconfig"       % "0.12.1",
  "ch.qos.logback"                % "logback-classic"  % "1.2.3",
  "com.typesafe.scala-logging"   %% "scala-logging"    % "3.9.2",
  "org.apache.kafka"              % "kafka-clients"    % "2.3.1"     % "test,it",
  "org.scalatest"                %% "scalatest"        % "3.0.5"     % "test,it",
  "io.github.embeddedkafka"      %% "embedded-kafka"   % "2.3.1"     % "test,it",
  "com.softwaremill.sttp.client" %% "core"             % "2.0.0-RC5" % "test,it"
)
