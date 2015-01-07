package com.github.bigtoast.sbtthrift

import sbt.Keys._
import sbt.{Process, _}

object ThriftPlugin extends Plugin {

  val Thrift = config("thrift")

  val thrift = SettingKey[String]("thrift", "thrift executable")
  val thriftSourceDir = SettingKey[File]("source-directory", "Source directory for thrift files. Defaults to src/main/thrift")

  val thriftJavaEnabled = SettingKey[Boolean]("java-enabled", "java generation is enabled. Default - yes")
  val thriftGenerateJava = TaskKey[Seq[File]]("generate-java", "Generate java sources from thrift files")
  val thriftJavaOptions = SettingKey[Seq[String]]("thrift-java-options", "additional options for java thrift generation")
  val thriftOutputDir = SettingKey[File]("java-output-directory", "Directory where the java files should be placed. Defaults to sourceManaged")

  val thriftJsEnabled = SettingKey[Boolean]("js-enabled", "javascript generation is enabled. Default - no")
  val thriftGenerateJs = TaskKey[Seq[File]]("generate-js", "Generate javascript sources from thrift files")
  val thriftJsOptions = SettingKey[Seq[String]]("thrift-js-options", "additional options for js thrift generation")
  val thriftJsOutputDir = SettingKey[File]("js-output-directory", "Directory where generated javascript files should be placed. default target/thrift-js")

  val thriftRubyEnabled = SettingKey[Boolean]("ruby-enabled", "ruby generation is enabled. Default - no")
  val thriftGenerateRuby = TaskKey[Seq[File]]("generate-ruby", "Generate ruby sources from thrift files.")
  val thriftRubyOptions = SettingKey[Seq[String]]("thrift-ruby-options", "additional options for ruby thrift generation")
  val thriftRubyOutputDir = SettingKey[File]("ruby-output-directory", "Directory where generated ruby files should be placed. default target/thrift-ruby")

  val thriftPythonEnabled = SettingKey[Boolean]("python-enabled", "python generation is enabled. Default - no")
  val thriftGeneratePython = TaskKey[Seq[File]]("generate-python", "Generate python sources from thrift files.")
  val thriftPythonOptions = SettingKey[Seq[String]]("thrift-python-options", "additional options for python thrift generation")
  val thriftPythonOutputDir = SettingKey[File]("python-output-directory", "Directory where generated python files should be placed. default target/thrift-python")

  lazy val thriftSettings = inConfig(Thrift)(Seq(
    thrift := "thrift",
    thriftSourceDir := (sourceDirectory in Compile).value / "thrift",
    thriftJavaEnabled := true,
    thriftJavaOptions := Seq("hashcode"),
    thriftOutputDir := (sourceManaged in Compile).value,
    thriftGenerateJava := {
      if (thriftJavaEnabled.value)
        compileThrift(thriftSourceDir.value, thriftOutputDir.value, thrift.value, "java", thriftJavaOptions.value, streams.value.log, streams.value.cacheDirectory / "thrift-java")
      else
        Seq()
    },
    thriftJsEnabled := false,
    thriftJsOptions := Seq(),
    thriftJsOutputDir := file("target/gen-js"),
    thriftGenerateJs := {
      if (thriftJsEnabled.value)
        compileThrift(thriftSourceDir.value, thriftJsOutputDir.value, thrift.value, "js", thriftJsOptions.value, streams.value.log, streams.value.cacheDirectory / "thrift-js")
      Seq()
    },
    thriftRubyEnabled := false,
    thriftRubyOptions := Seq(),
    thriftRubyOutputDir := file("target/gen-ruby"),
    thriftGenerateRuby := {
      if (thriftRubyEnabled.value)
        compileThrift(thriftSourceDir.value, thriftRubyOutputDir.value, thrift.value, "rb", thriftRubyOptions.value, streams.value.log, streams.value.cacheDirectory / "thrift-ruby")
      Seq()
    },
    thriftPythonEnabled := false,
    thriftPythonOptions := Seq(),
    thriftPythonOutputDir := file("target/gen-python"),
    thriftGeneratePython := {
      if (thriftPythonEnabled.value)
        compileThrift(thriftSourceDir.value, thriftPythonOutputDir.value, thrift.value, "py", thriftPythonOptions.value, streams.value.log, streams.value.cacheDirectory / "thrift-python")
      Seq()
    },
    managedClasspath <<= (classpathTypes, update) map { (cpt, up) =>
      Classpaths.managedJars(Thrift, cpt, up)
    }
  )) ++ Seq(
    sourceGenerators in Compile <+= thriftGenerateJava in Thrift,
    sourceGenerators in Compile <+= thriftGenerateRuby in Thrift,
    sourceGenerators in Compile <+= thriftGeneratePython in Thrift,
    sourceGenerators in Compile <+= thriftGenerateJs in Thrift,
    watchSources ++= (thriftSourceDir.value ** "*").get,
    ivyConfigurations += Thrift
  )

  def compileThrift(sourceDir: File, outputDir: File, thriftBin: String,
                    language: String, options: Seq[String], logger: Logger, cache: File): Seq[File] = {
    val doIt = FileFunction.cached(cache, inStyle = FilesInfo.lastModified, outStyle = FilesInfo.exists) { files =>
      if (!outputDir.exists)
        outputDir.mkdirs
      files.foreach { schema =>
        val cmd = s"""$thriftBin -gen ${language + options.mkString(":", ",", "")} -o $outputDir $schema"""
        logger.info(s"Compiling thrift schema with command: $cmd")
        val code = Process(cmd) ! logger
        if (code != 0) {
          sys.error(s"Thrift compiler exited with code $code")
        }
      }
      (outputDir ** s"*.$language").get.toSet
    }
    doIt((sourceDir ** "*.thrift").get.toSet).toSeq
  }
}
