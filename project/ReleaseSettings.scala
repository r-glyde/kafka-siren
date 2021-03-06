import com.tapad.docker.DockerComposePlugin.dockerComposeTest
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.{Docker => docker}
import sbt.Keys._
import sbt.taskKey
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._

object ReleaseSettings {
  // Useful tasks to show what versions would be used if a release was performed.
  private val showReleaseVersion = taskKey[String]("the future version once releaseNextVersion has been applied to it")
  private val showNextVersion = taskKey[String]("the future version once releaseNextVersion has been applied to it")

  lazy val common = Seq(
    releaseUseGlobalVersion := false,
    releaseVersionBump := sbtrelease.Version.Bump.Minor,
    releaseTagName := s"${name.value}-${version.value}",
    releaseTagComment := s"Releasing ${version.value} of module: ${name.value}",
    releaseProcess := Seq[ReleaseStep](
      runClean,
      runTest,
      releaseStepCommand(dockerComposeTest),
      checkSnapshotDependencies,
      inquireVersions,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      ReleaseStep(releaseStepTask(publish in docker)),
      setNextVersion,
      commitNextVersion,
      pushChanges
    ),
    showReleaseVersion := { val rV = releaseVersion.value.apply(version.value); println(rV); rV },
    showNextVersion := { val nV = releaseNextVersion.value.apply(version.value); println(nV); nV }
  )
}
