
package atd.sbtthrift

import sbt._
import classpath._
import Process._
import Keys._

object ThriftPlugin extends Plugin {

  val thriftConfig = config("thrift")

  val thrift = SettingKey[String]("thrift", "thrift executable")
  val thriftSourceDir = SettingKey[File]("source-directory", "Source directory for thrift files. Defaults to src/main/thrift")
  val thriftGenerate = TaskKey[Seq[File]]("generate-java", "Generate java sources from thrift files")
  val thriftOutputDir = SettingKey[File]("output-directory", "Directory where the java files should be placed. Defaults to sourceManaged")
  val thriftJavaOptions = SettingKey[Seq[String]]("thrift-java-options", "additional options for java thrift generation")
  val thriftJavaEnabled = SettingKey[Boolean]("java-enabled", "java generation is enabled. Default - yes")
  val thriftGenerateJs = TaskKey[Seq[File]]("generate-js","Generate javascript sources from thrift files")
  val thriftJsOutputDir = SettingKey[File]("js-output-directory","Direcotry where generated javsacript files should be placed. default target/thrift-js")
  val thriftJsOptions = SettingKey[Seq[String]]("thrift-js-options", "additional options for js thrift generation")
  val thriftJsEnabled = SettingKey[Boolean]("js-enabled", "javascript generation is enabled. Default - no")

  lazy val thriftSettings :Seq[Setting[_]] = inConfig(thriftConfig)(Seq[Setting[_]](
  thrift := "thrift",

  thriftSourceDir <<= (sourceDirectory in Compile){ _ / "thrift"},

  thriftOutputDir <<= (sourceManaged in Compile).identity,

  thriftJavaEnabled := true,

  thriftJavaOptions := Seq[String](),

  thriftJsOutputDir := new File("target/gen-js"),

  thriftGenerate <<= (streams, thriftSourceDir, thriftOutputDir, 
                      thrift, thriftJavaOptions, thriftJavaEnabled) map { 
       (out, sdir, odir, tbin, opts, enabled ) =>
        if (enabled) {
          compileThrift(sdir,odir,tbin,"java",opts,out.log);
        }else{
          Seq[File]()
        }
  },

  thriftJsEnabled := false,

  thriftJsOptions := Seq[String](),

  thriftGenerateJs <<= (streams, thriftSourceDir, thriftJsOutputDir, 
                        thrift, thriftJsOptions, thriftJsEnabled) map { 
        ( out, sdir, odir, tbin, opts, enabled ) =>
        if (enabled) {
          compileThrift(sdir,odir,tbin,"js",opts,out.log);
        } else {
          Seq[File]()
        }
    },


  managedClasspath <<= (classpathTypes, update) map { (cpt, up) =>
    Classpaths.managedJars(thriftConfig, cpt, up)
  },

  (managedResourceDirectories in Compile) <++= (thriftJsOutputDir, thriftJsEnabled) {
      (out, enabled) => if (enabled) {
                              Seq(out)
                        } else {
                              Seq()
                        }
  }

  )) ++ Seq[Setting[_]](
    sourceGenerators in Compile <+= thriftGenerate in thriftConfig,
    resourceGenerators in Compile <+= thriftGenerateJs in thriftConfig,
    ivyConfigurations += thriftConfig
  )

  def compileThrift(sourceDir: File, 
                    outputDir: File, 
                    thriftBin: String, 
                    language: String, 
                    options: Seq[String],
                    logger: Logger):Seq[File] =
  {
    val schemas = (sourceDir ** "*.thrift").get
    outputDir.mkdirs()
    logger.info("Compiling %d thrift files to %s in %s".format(schemas.size, language, outputDir))
    schemas.foreach { schema =>
      val cmd = "%s -gen %s -o %s %s".format(thriftBin, 
                                        language + options.mkString(":",",",""),
                                        outputDir, schema)
      logger.info("Compiling schema with command: %s" format cmd)
      <x>{cmd}</x> !
    }
    (outputDir ** "*.%s".format(language)).get.toSeq
  }


}
