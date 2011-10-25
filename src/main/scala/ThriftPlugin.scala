
package atd.sbtthrift

import sbt._
import classpath._
import Process._
import Keys._

object ThriftPlugin extends Plugin {
  val thriftConfig = config("thrift")

  val thrift = SettingKey[String]("thrift", "thrift executable")
  val thriftSourceDir = SettingKey[File]("thrift-source-directory", "Source directory for thrift files. Defaults to src/main/thrift")
  //val sources = TaskKey[Seq[File]]("sources", "Directory containing thrift files")
  val thriftGenerate = TaskKey[Seq[File]]("thrift-generate-java", "Generate java sources from thrift files")
  val thriftOutputDir = SettingKey[File]("thrift-output-directory", "Directory where the java files should be placed. Defaults to target/generated-sources")

  lazy val thriftSettings :Seq[Setting[_]] = inConfig(thriftConfig)(Seq[Setting[_]](
  thrift := "thrift",

  thriftSourceDir <<= (sourceDirectory in Compile){ _ / "thrift"},

  thriftOutputDir <<= (target in Compile) { _ / "generated-sources" },

    thriftGenerate <<= (streams, thriftSourceDir, thriftOutputDir, thrift) map { ( out, sdir, odir, tbin ) =>
    val schemas = (sdir ** "*.thrift").get
    odir.mkdirs()
    out.log("Compiling %d thrift files to %s".format(schemas.size, odir))
    schemas.foreach { schema =>
      val cmd = "%s -gen java -o %s %s".format(tbin, odir, schema)
      out.log("Compiling schema with command: %s" format cmd)
      Process(cmd).run()
    }
    (odir ** "*.java").get.toSeq
  },

   sourceGenerators in Compile <+= thriftGenerate
  ))

}
