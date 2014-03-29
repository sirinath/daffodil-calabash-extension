name := "daffodil-calabash-extension"

version := "0.4"

scalaVersion := "2.9.2"

libraryDependencies += ("edu.illinois.ncsa" %% "daffodil-core" % "0.12.0" 
  exclude("com.novocode", "junit-interface")
  exclude("jline", "jline")
  exclude("junit", "junit")
  exclude("junit", "junit-dep")
  exclude("net.sourceforge.expectj", "expectj")
  exclude("org.rogach", "scallop_2.9.2")
  exclude("org.scala-tools.testing", "test-interface")
)

libraryDependencies += ("edu.illinois.ncsa" %% "daffodil-lib" % "0.12.0"
  exclude("com.novocode", "junit-interface")
  exclude("jline", "jline")
  exclude("junit", "junit")
  exclude("junit", "junit-dep")
  exclude("net.sourceforge.expectj", "expectj")
  exclude("org.rogach", "scallop_2.9.2")
  exclude("org.scala-tools.testing", "test-interface")
)

libraryDependencies += ("edu.illinois.ncsa" %% "daffodil-runtime1" % "0.12.0"
  exclude("com.novocode", "junit-interface")
  exclude("jline", "jline")
  exclude("junit", "junit")
  exclude("junit", "junit-dep")
  exclude("net.sourceforge.expectj", "expectj")
  exclude("org.rogach", "scallop_2.9.2")
  exclude("org.scala-tools.testing", "test-interface")
)

libraryDependencies ++= Seq(
  "commons-logging" % "commons-logging" % "1.1.+"
    , "commons-codec" % "commons-codec" % "1.6"
    , "commons-io" % "commons-io" % "1.3.1"
)


// retrieve dependencies to lib_managed for convenience when running
// from the command-line
retrieveManaged := true

resolvers += ("NCSA Sonatype Releases" 
  at "https://opensource.ncsa.illinois.edu/nexus/content/repositories/releases"
)

//"com.xmlcalabash" % "xmlcalabash" % "1.0.13-95" exclude("net.sf.saxon", "Saxon-HE")

//resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
