package com.github.bigtoast.sbtthrift

import sbt._
import Keys._

object ThriftPlugin extends Plugin {

  val Thrift = config("thrift")

  val thrift            = SettingKey[String]("thrift", "thrift executable")
  val thriftSourceDir   = SettingKey[File]("source-directory", "Source directory for thrift files. Defaults to src/main/thrift")
  val thriftGenerate    = TaskKey[Seq[File]]("generate-java", "Generate java sources from thrift files")
  val thriftOutputDir   = SettingKey[File]("output-directory", "Directory where the java files should be placed. Defaults to sourceManaged")
  val thriftJavaOptions = SettingKey[Seq[String]]("thrift-java-options", "additional options for java thrift generation")
  val thriftJavaEnabled = SettingKey[Boolean]("java-enabled", "java generation is enabled. Default - yes")
  
  val thriftGenerateJs  = TaskKey[Seq[File]]("generate-js","Generate javascript sources from thrift files")
  val thriftJsOutputDir = SettingKey[File]("js-output-directory","Direcotry where generated javsacript files should be placed. default target/thrift-js")
  val thriftJsOptions   = SettingKey[Seq[String]]("thrift-js-options", "additional options for js thrift generation")
  val thriftJsEnabled   = SettingKey[Boolean]("js-enabled", "javascript generation is enabled. Default - no")

  val thriftRubyEnabled    = SettingKey[Boolean]("ruby-enabled", "ruby generation is enabled. Default - no")
  val thriftRubyOptions    = SettingKey[Seq[String]]("thrift-ruby-options", "additional options for ruby thrift generation")
  val thriftGenerateRuby   = TaskKey[Seq[File]]("generate-ruby", "Generate ruby source files from thrift sources.")
  val thriftRubyOutputDir  = SettingKey[File]("js-output-directory","Direcotry where generated javsacript files should be placed. default target/thrift-ruby")
  
  val thriftPythonEnabled  = SettingKey[Boolean]("python-enabled", "python generation is enabled. Default - no")
  val thriftPythonOptions  = SettingKey[Seq[String]]("thrift-python-options", "additional options for java thrift generation")
  val thriftGeneratePython = TaskKey[Seq[File]]("generate-python", "Generate python source files from thrift sources.")
  val thriftPythonOutputDir = SettingKey[File]("python-output-directory","Direcotry where generated javsacript files should be placed. default target/thrift-python")

  lazy val thriftSettings :Seq[Setting[_]] = inConfig(Thrift)(Seq[Setting[_]](
    thrift := "thrift",

    thriftSourceDir <<= (sourceDirectory in Compile){ _ / "thrift"},

    thriftOutputDir <<= (sourceManaged in Compile),

    thriftJavaEnabled := true,

    thriftJavaOptions := Seq[String]("hashcode"),

    thriftJsOutputDir := new File("target/gen-js"),

    thriftGenerate <<= (streams, thriftSourceDir, thriftOutputDir,
                        thrift, thriftJavaOptions, thriftJavaEnabled, cacheDirectory) map {
         (out, sdir, odir, tbin, opts, enabled, cache ) =>
          if (enabled) {
            compileThrift(sdir,odir,tbin,"java",opts,out.log, cache / "thirft" );
          }else{
            Seq[File]()
          }
    },

    thriftJsEnabled := false,

    thriftJsOptions := Seq[String](),

    thriftGenerateJs <<= (streams, thriftSourceDir, thriftJsOutputDir,
                          thrift, thriftJsOptions, thriftJsEnabled, cacheDirectory ) map {
          ( out, sdir, odir, tbin, opts, enabled, cache ) =>
          if (enabled)
            compileThrift(sdir,odir,tbin,"js",opts,out.log, cache / "thrift-js" );
          //else
            Seq[File]()
      },

    thriftRubyEnabled := false,

    thriftRubyOptions := Seq[String](),

    thriftRubyOutputDir := new File("target/gen-ruby"),

    thriftGenerateRuby <<= (streams, thriftSourceDir, thriftOutputDir,
                          thrift, thriftRubyOptions, thriftRubyEnabled, cacheDirectory ) map {
          ( out, sdir, odir, tbin, opts, enabled, cache ) =>
          if (enabled)
            compileThrift(sdir,odir,tbin,"rb",opts,out.log, cache / "thrift-ruby" );
          //else
            Seq[File]()
      },

    thriftPythonEnabled := false,

    thriftPythonOptions := Seq[String](),

    thriftPythonOutputDir := new File("target/gen-python"),

    thriftGeneratePython <<= (streams, thriftSourceDir, thriftOutputDir,
                          thrift, thriftPythonOptions, thriftPythonEnabled, cacheDirectory ) map {
          ( out, sdir, odir, tbin, opts, enabled, cache ) =>
          if (enabled)
            compileThrift(sdir,odir,tbin,"py",opts,out.log, cache / "thrift-python" );
          //else
            Seq[File]()
      },

    managedClasspath <<= (classpathTypes, update) map { (cpt, up) =>
      Classpaths.managedJars(Thrift, cpt, up)
    }

  )) ++ Seq[Setting[_]](
    sourceGenerators in Compile <+= thriftGenerate       in Thrift,
    sourceGenerators in Compile <+= thriftGenerateRuby   in Thrift,
    sourceGenerators in Compile <+= thriftGeneratePython in Thrift,
    sourceGenerators in Compile <+= thriftGenerateJs     in Thrift,
    watchSources <++= ( thriftSourceDir ) map { ( tdir ) => ( tdir ** "*" ).get },
    ivyConfigurations += Thrift
  )
  

  def compileThrift(sourceDir: File, outputDir: File, thriftBin: String, 
                    language: String, options: Seq[String], logger: Logger, cache :File ):Seq[File] = {
    
    val doIt = FileFunction.cached( cache , inStyle = FilesInfo.lastModified, outStyle = FilesInfo.exists) { files :Set[File] =>
      if (! outputDir.exists )
        outputDir.mkdirs
      files.foreach { schema =>
        val cmd = "%s -gen %s -o %s %s".format(thriftBin,
                                        language + options.mkString(":",",",""),
                                        outputDir, schema)
        logger.info("Compiling schema with command: %s" format cmd)
        <x>{cmd}</x> ! logger
      } 

    
      (outputDir ** "*.%s".format(language)).get.toSet
    }
    
    doIt(  (sourceDir ** "*.thrift").get.toSet ).toSeq
    
  }


}
