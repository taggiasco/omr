name := "omr"

version := "0.0.1"

scalaVersion := "2.11.7"

shellPrompt := { state => "[" + Project.extract(state).currentRef.project + "] $ " }

scalacOptions ++= Seq("-target:jvm-1.5", "-deprecation", "-unchecked", "-language:dynamics", "-language:implicitConversions", "-language:reflectiveCalls", "-feature", "-language:postfixOps")

javacOptions ++= Seq("-source", "1.5", "-target", "1.5")
