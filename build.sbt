

name := "ergo-index-contracts"
version := "0.1"
scalaVersion := "2.12.14"

lazy val sonatypePublic = "Sonatype Public" at "https://oss.sonatype.org/content/groups/public/"
lazy val sonatypeReleases = "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
lazy val sonatypeSnapshots = "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

resolvers ++= Seq(Resolver.mavenLocal, sonatypeReleases, sonatypeSnapshots, Resolver.mavenCentral)

libraryDependencies ++= Seq(
  "org.ergoplatform" %% "ergo-playground-env" % "0.0.0-76-0fd15a64-SNAPSHOT"

)
