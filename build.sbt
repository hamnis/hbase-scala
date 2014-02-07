name := "hbase-scala"

organization := "net.hamnaberg"

scalaVersion := "2.10.3"

net.virtualvoid.sbt.graph.Plugin.graphSettings

lazy val logging = Seq(
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "jcl-over-slf4j" % "1.7.5",
  "org.slf4j" % "slf4j-simple" % "1.7.5" % "runtime"
)

lazy val transitive = Seq(
  "com.google.protobuf" % "protobuf-java" % "2.4.0a",
  "commons-lang" % "commons-lang" % "2.6",
  "commons-configuration" % "commons-configuration" % "1.6" exclude("commons-logging", "commons-logging"),
  "commons-digester" % "commons-digester" % "1.8" exclude("commons-beanutils", "commons-beanutils") exclude("commons-logging", "commons-logging"),
  "commons-beanutils" % "commons-beanutils-core" % "1.8.0" exclude("commons-logging", "commons-logging")
)

libraryDependencies ++= Seq(
  ExcludeAllTransitiveDeps(target.value, "org.apache.hbase" % "hbase" % "0.94.16"),
  ExcludeAllTransitiveDeps(target.value, "org.apache.hadoop" % "hadoop-core" % "1.0.4"),
  ExcludeAllTransitiveDeps(target.value, "org.apache.zookeeper" % "zookeeper" % "3.4.5")
)

libraryDependencies ++= logging

libraryDependencies ++= transitive
