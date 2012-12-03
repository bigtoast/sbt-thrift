Thrift plugin for sbt 0.12+
====================================

# Instructions for use:
### Step 1: Include the plugin in your build

Add the following to your `project/plugins.sbt`:

    resolvers += "bigtoast-github" at "http://bigtoast.github.com/repo/"

    addSbtPlugin("com.github.bigtoast" % "sbt-thrift" % "0.6")

### Step 2: Add sbt-thrift settings to your build

Add the following to your 'build.sbt' ( if you are using build.sbt )


    import com.github.bigtoast.sbtthrift.ThriftPlugin

    seq(ThriftPlugin.thriftSettings: _*)

Or if you are using a build object extending from Build:

    import sbt._
    import Keys._
    import com.github.bigtoast.sbtthrift.ThriftPlugin

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
        <tr>
                <td> <b>thriftJavaOptions</b> </td>
                <td>Additional options to thrift compiler for java generation.</td>
        </tr>
        <tr>
                <td> <b>thriftJavaEnabled</b> </td>
                <td> Are we generating java source (?)  Default is true.</td>
        </tr>
        <tr>
                <td> <b>thriftJsEnabled</b> </td>
                <td> Are we generating javascript source (?)  Default is false.</td>
        </tr>
        <tr>
                <td> <b>thriftJsOutputDir</b> </td>
                <td>The output dir for the generated javascript. This directory will be added to resourceManaged so it will be automatically get compiled during generation of resources. This defaults to 'target/gen-js'.</td>
        </tr>
        <tr>
                <td> <b>thriftJsOptions</b> </td>
                <td>Additional options to thrift compiler for javascript generation.</td>
        </tr>

        <tr>
                <td> <b>thriftRubyEnabled</b> </td>
                <td> Are we generating ruby source (?)  Default is false.</td>
        </tr>
        <tr>
                <td> <b>thriftRubyOutputDir</b> </td>
                <td>The output dir for the generated Ruby. This directory will be added to sourceManaged so it will be automatically get compiled during generation of resources. This defaults to 'target/gen-rb'.</td>
        </tr>
        <tr>
                <td> <b>thriftRubyOptions</b> </td>
                <td>Additional options to thrift compiler for Ruby generation.</td>
        </tr>

        <tr>
                <td> <b>thriftPythonEnabled</b> </td>
                <td> Are we generating Python source (?)  Default is false.</td>
        </tr>
        <tr>
                <td> <b>thriftPythonOutputDir</b> </td>
                <td>The output dir for the generated Python. This directory will be added to sourceManaged so it will be automatically get compiled during generation of resources. This defaults to 'target/gen-py'.</td>
        </tr>
        <tr>
                <td> <b>thriftPythonOptions</b> </td>
                <td>Additional options to thrift compiler for Python generation.</td>
        </tr>

</table>

## Tasks

<table>
        <tr>
                <td> <b>thrift:generate-java</b> </td>
                <td>This will run generate java sources from the thrift sources. This task is automatically executed when compile is run.</td>
        </tr>
        <tr>
                <td> <b>thrift:generate-js</b> </td>
                <td>This will run generate javascript sources from the thrift sources. This task is automatically executed during the compile phase if thriftJsEnabled is set to true /td>
        </tr>
        <tr>
                <td> <b>thrift:generate-ruby</b> </td>
                <td>This will run generate Ruby sources from the thrift sources. This task is automatically executed during the compile phase if thriftRubyEnabled is set to true /td>
        </tr>
         <tr>
                 <td> <b>thrift:generate-python</b> </td>
                 <td>This will run generate Python sources from the thrift sources. This task is automatically executed during the compile phase if thriftPythonEnabled is set to true /td>
         </tr>

</table>


Notes
------------------

If any bugs are found or features wanted please file an issue in the github project. I will do my best to accommodate.


Acknoledgements
---------------
I used the following plugins as reference

 * my sbt-liquibase plugin
 * sbt-protobuf plugin for sbt 0.10


Contributors
------------
Andrew Headrick [bigtoast]("http://github.com/bigtoast")
Ruslan Shevchenko [rssh]("http://github.com/rssh")
