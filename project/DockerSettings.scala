import java.io.File

import com.tapad.docker.DockerComposePlugin.autoImport._
import com.typesafe.sbt.SbtNativePackager.autoImport.packageName
import com.typesafe.sbt.packager.docker.Cmd
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._
import sbt.Keys._
import sbt.librarymanagement.Configurations.IntegrationTest

object DockerSettings {

  lazy val common = Seq(
    dockerBaseImage := "openjdk:8u201-alpine",
    dockerRepository := Some("glyderj"),
    dockerLabels := Map("maintainer" -> "r-glyde"),
    dockerUpdateLatest := true,
    packageName in Docker := "kafka-siren",
    dockerCommands ++= Seq(
      Cmd("USER", "root"),
      Cmd("RUN", "apk update && apk add bash && apk add eudev")
    ),
    testCasesPackageTask := (packageBin in IntegrationTest).value,
    testCasesJar := artifactPath.in(IntegrationTest, packageBin).value.getAbsolutePath,
    dockerImageCreationTask := (publishLocal in Docker).value,
    testDependenciesClasspath :=
      (fullClasspath in IntegrationTest).value.files.map(_.getAbsoluteFile).mkString(File.pathSeparator)
  )

}