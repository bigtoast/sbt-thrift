
sbtPlugin := true

organization := "com.github.bigtoast"

name := "sbt-thrift"

version := "0.7"

scalaVersion := "2.10.4"

publishTo := Some(Resolver.file("bigtoast.github.com", file(Path.userHome + "/Projects/BigToast/bigtoast.github.com/repo")))
