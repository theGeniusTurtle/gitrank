name := "gitrank"
 
version := "1.0" 
      
lazy val `gitrank` = (project in file(".")).enablePlugins(PlayScala)

      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"

//change default port from 9000 to 8080
PlayKeys.playDefaultPort := 8080
      
scalaVersion := "2.13.5"

libraryDependencies ++= Seq(ehcache, ws , guice,
    "org.apache.commons" % "commons-lang3" % "3.12.0",

    //test dependencies
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test",
    "org.scalatestplus" %% "mockito-3-4" % "3.2.9.0" % "test"
)

addCommandAlias("build", ";clean;compile;test")

addCommandAlias("buildAndRun", ";build;run")