import com.trueaccord.scalapb.{ScalaPbPlugin => PB}
import com.typesafe.sbt.SbtMultiJvm
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys._
import Keys.{compile => sbtCompile}

lazy val akkaVersion = "2.4.3"
lazy val actor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
lazy val persistence = "com.typesafe.akka" %% "akka-persistence" % akkaVersion
lazy val remote = "com.typesafe.akka" %% "akka-remote" % akkaVersion
lazy val leveldb = "org.iq80.leveldb" % "leveldb" % "0.7"
lazy val leveldbjni = "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"
lazy val scalaPbRuntime = "com.trueaccord.scalapb" %% "scalapb-runtime" % "0.4.20" % PB.protobufConfig
lazy val multiNodeTestKit = "com.typesafe.akka" %% "akka-multi-node-testkit" % "2.4.8"
lazy val scalatest = "org.scalatest" %% "scalatest" % "2.2.4"

lazy val allSettings = PB.protobufSettings ++ Seq(
  scalaVersion := "2.11.7",
  resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  libraryDependencies ++= Seq(actor, persistence, remote, leveldb, leveldbjni, scalaPbRuntime, multiNodeTestKit, scalatest),
  PB.javaConversions in PB.protobufConfig := false,
  PB.runProtoc in PB.protobufConfig := (args =>
    com.github.os72.protocjar.Protoc.runProtoc(
      "-v261" +: s"-I${file("common").getAbsolutePath}/src/main/protobuf" +: args.toArray)),
  fork in run := true
)

lazy val root = project.in(file("."))
  .settings(name := "root")
  .settings(allSettings)
  .settings(SbtMultiJvm.multiJvmSettings: _*)
  .settings(
    sbtCompile in MultiJvm <<= (sbtCompile in MultiJvm) triggeredBy (sbtCompile in Test),
    parallelExecution in Test := false,
    executeTests in Test <<= (executeTests in Test, executeTests in MultiJvm) map {
      case (testResults, multiNodeResults)  =>
        val overall =
          if (testResults.overall.id < multiNodeResults.overall.id)
            multiNodeResults.overall
          else
            testResults.overall
        Tests.Output(overall,
          testResults.events ++ multiNodeResults.events,
          testResults.summaries ++ multiNodeResults.summaries)
    }
  )
  .settings(multiNodeHosts in MultiJvm := Seq("root@139.59.163.87", "root@139.59.168.181"))
  .configs (MultiJvm)
  .dependsOn(common, version1, version2)
  .aggregate(common, version1, version2)

lazy val common = project.in(file("common"))
  .settings(name := "common")
  .settings(allSettings)

lazy val version1 = project.in(file("version1"))
  .settings(name := "version1")
  .settings(allSettings)
  .dependsOn(common)

lazy val version2 = project.in(file("version2"))
  .settings(name := "version2")
  .settings(allSettings)
  .dependsOn(common)
