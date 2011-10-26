Thrift plugin for sbt 0.10+ and 0.11+
====================================

# Instructions for use:
### Step 1: Include the plugin in your build

Add the following to your `project/plugins/build.sbt`:

## sbt-0.10.1

    resolvers += "bigtoast-github" at "http://bigtoast.github.com/repo/"

    libraryDependencies += "atd" %% "sbt-thrift" % "0.3"

## sbt-0.11.0

    resolvers += "bigtoast-github" at "http://bigtoast.github.com/repo/"

    addSbtPlugin("atd" % "sbt-thrift" % "0.3")

### Step 2: Add sbt-thrift settings to your build

Add the following to your 'build.sbt' ( if you are using build.sbt )


    import atd.sbtthrift.ThriftPlugin

    seq(ThriftPlugin.thriftSettings: _*)

Or if you are using a build object extending from Build:

    import sbt._
    import Keys._
    import atd.sbtthrift.ThriftPlugin

    class BuildWithThriftShiz extends Build {
         lazy val seniorProject = Project("hola", file("."), settings = 
          Defaults.defaultSettings ++ ThriftPlugin.thriftSettings ++ Seq(/* custom settings go here */))
    }


## Settings

<table>
        <tr>
                <td> <b>thrift</b> </td>
                <td>Thrift executable. This defaults to just 'thrift'</td>
        </tr>
        <tr><td></td><td>

            thrift := "/some/other/path/to/thrift"

        </td></tr>
        <tr>
                <td> <b>thriftSourceDir</b> </td>
                <td>Directory containing thrift sources. This defaults to 'src/main/thrift'.</td>
        </tr>
        <tr><td></td><td>

            thriftSourceDir &lt;&lt;= baseDirectory( _ / "other" / "thrift" / "sourceDir" )

        </td></tr>
        <tr>
                <td> <b>thriftOutputDir</b> </td>
                <td>The output dir for the generated sources. This directory will be added to sourceManaged so it will be automatically get compiled when you run compile. This defaults to 'target/generated-sources'.</td>
        </tr>

</table>

## Tasks

<table>
        <tr>
                <td> <b>thrift-generate-java</b> </td>
                <td>This will run generate java sources from the thrift sources. This task is automatically executed when compile is run.</td>
        </tr>

</table>


Warnings and Notes
------------------
This is my second sbt plugin. So far it has been used in development environments at work and in Jenkins builds. It is pretty straight forward and only generates java. If other languages are requested please let me know.

If any bugs are found or features wanted please file an issue in the github project. I will do my best to accommodate.


Acknoledgements
---------------
I used the following plugins as reference

 * my sbt-liquibase plugin
 * sbt-protobuf plugin for sbt 0.10

