name := "hbase-scala"

organization := "net.hamnaberg"

scalaVersion := "2.10.3"


net.virtualvoid.sbt.graph.Plugin.graphSettings


libraryDependencies += "org.apache.hbase" % "hbase" % "0.94.16" intransitive()

libraryDependencies += "org.apache.hadoop" % "hadoop-core" % "1.0.4" intransitive()

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.5"

libraryDependencies += "org.slf4j" % "jcl-over-slf4j" % "1.7.5"

libraryDependencies += "com.google.protobuf" % "protobuf-java" % "2.4.0a"

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

libraryDependencies += "commons-configuration" % "commons-configuration" % "1.6" exclude("commons-logging", "commons-logging")

libraryDependencies += "commons-digester" % "commons-digester" % "1.8" exclude("commons-beanutils", "commons-beanutils") exclude("commons-logging", "commons-logging")

libraryDependencies += "commons-beanutils" % "commons-beanutils-core" % "1.8.0" exclude("commons-logging", "commons-logging")

libraryDependencies += "org.apache.zookeeper" % "zookeeper" % "3.4.5" intransitive()

libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.5" % "runtime"
