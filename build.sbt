name := "spray-json-member-presence"
organization := "com.github.bigwheel"
version := "0.1"
scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "io.spray" %%  "spray-json" % "1.3.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
)

// about maven publish
publishMavenStyle := true
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
publishArtifact in Test := false
licenses := Seq("MIT License" -> url("https://github.com/bigwheel/spray-json-member-presence/blob/master/LICENSE"))
homepage := Some(url("https://github.com/bigwheel/spray-json-member-presence"))
pomExtra := (
  <scm>
    <url>git@github.com:bigwheel/spray-json-member-presence.git</url>
    <connection>scm:git:git@github.com:bigwheel/spray-json-member-presence.git</connection>
  </scm>
    <developers>
      <developer>
        <id>bigwheel</id>
        <name>k.bigwheel</name>
        <url>https://github.com/bigwheel</url>
      </developer>
    </developers>
  )