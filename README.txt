INTRODUCTION
This project contains an extension step for Calabash
(http://xmlcalabash.com/) that uses Daffodil
(https://opensource.ncsa.illinois.edu/confluence/display/DFDL/Daffodil%3A+Open+Source+DFDL)
to parse a file with a DFDL schema.


BUILDING
This project uses sbt as the primary build tool, which can be
downloaded from http://www.scala-sbt.org/.

Build the project like this:
$ sbt package
$ cp lib_managed/jars/*/*/* lib_managed


RUNNING 
The daffodil-calabash extension only works with scala 2.9.2 at
this time, since that's the only version of scala that daffodil 0.12.0
is built with by default.  See http://scala-lang.org/download/all.html
to download Scala 2.9.2.

The examples directory contains a XProc pipeline that calls Daffodil,
using either the CSV and PCAP DFDL examples that are distributed with
Daffodil.  Here's a taste:

<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
                xmlns:c="http://www.w3.org/ns/xproc-step"
                xmlns:dfdl="urn:daffodil:calabash"
                name="pipeline"
                version="1.0">
    <p:output port="result" primary="true">
        <p:pipe port="result" step="parse"/>
    </p:output>

    <p:import href="../etc/daffodil-library.xpl"/>
    
    <dfdl:parse name="parse" file="../examples/csv/simpleCSV" 
        schema="../examples/csv/csv.dfdl.xsd" />
    <!-- <dfdl:parse name="parse" file="../examples/pcap/dns.cap" 
        schema="../examples/pcap/pcap.dfdl.xsd" /> -->
 
</p:declare-step>

Of course, the magic isn't in simply running Daffodil; the magic is in
manipulating the parsed data using XProc.

To run Calabash with the Daffodil extension, it's as easy as running
it from the command-line with specific arguments:

From Unix/Linux:
$ scala -cp lib/*:lib_managed/*:target/scala-2.9.2/daffodil-calabash-extension_2.9.2-0.1.jar \
com.xmlcalabash.drivers.Main -c etc/calabash-config.xml examples/test.xpl

From Windows:
> scala -cp lib\*;lib_managed\*;target\scala-2.9.2\daffodil-calabash-extension_2.9.2-0.1.jar com.xmlcalabash.drivers.Main -c etc\calabash-config.xml examples\test.xpl

OR like this for troubleshooting (Unix/Linux example):
$ scala -cp lib/*:lib_managed/*:target/scala-2.9.2/daffodil-calabash-extension_2.9.2-0.1.jar \
-Djava.util.logging.config.file=etc/logging.properties \
com.xmlcalabash.drivers.Main -D -c etc/calabash-config.xml examples/test.xpl

Important things to notice in the command-line: 
* This command-line executes Calabash with a custom classpath and
  configuration. 
* The extension and all its dependencies are added to the classpath.
* A configuration file is used to tie the dfdl:parse XProc step to the
  extension code. 


ACKNOWLEDGEMENTS
Thanks for Florent Georges for his very helpful blog post at 
http://fgeorges.blogspot.com/2011/09/writing-extension-step-for-calabash-to.html.
