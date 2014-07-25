name := "daffodil-calabash-extension"

version := "0.5.0"

organization := "edu.illionois.ncsa"

startYear := Some(2013)

description := "An extension to Calabash that adds dfdl:parse and dfdl:parse-file XProc steps, which in turn call Daffodil."

licenses := Seq("University of Illinois/NCSA Open Source License" -> url("http://opensource.org/licenses/UoI-NCSA.php"))

scalaVersion := "2.10.4"

libraryDependencies += ("edu.illinois.ncsa" %% "daffodil-core" % "0.14.0-SNAPSHOT" 
  exclude("com.novocode", "junit-interface")
  excludeAll(ExclusionRule(name = "jline"))
  exclude("net.sourceforge.expectj", "expectj")
  exclude("org.rogach", "scallop_2.10")
  exclude("org.scala-tools.testing", "test-interface")
)

libraryDependencies += ("edu.illinois.ncsa" %% "daffodil-lib" % "0.14.0-SNAPSHOT"
  exclude("com.novocode", "junit-interface")
  excludeAll(ExclusionRule(name = "jline"))
  exclude("net.sourceforge.expectj", "expectj")
  exclude("org.rogach", "scallop_2.10")
  exclude("org.scala-tools.testing", "test-interface")
)

libraryDependencies ++= Seq(
  "commons-logging" % "commons-logging" % "1.1.+"
    , "commons-codec" % "commons-codec" % "1.6"
    , "commons-io" % "commons-io" % "2.4"
)


scalacOptions += "-feature"

// retrieve dependencies to lib_managed for convenience when running
// from the command-line
retrieveManaged := true

resolvers += ("NCSA Sonatype Releases" 
  at "https://opensource.ncsa.illinois.edu/nexus/content/repositories/releases"
)

resolvers += ("NCSA Sonatype Snapshots" 
  at "https://opensource.ncsa.illinois.edu/nexus/content/repositories/snapshots"
)


///////////////////////////
// eclipse plugin settings

// download source artifacts and create Eclipse source attachments for
// library dependencies
EclipseKeys.withSource := true

// add resources directories to .classpath
//EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource

