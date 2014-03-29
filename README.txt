INTRODUCTION
This project contains an extension step for Calabash
(http://xmlcalabash.com/) that uses Daffodil
(https://opensource.ncsa.illinois.edu/confluence/display/DFDL/Daffodil%3A+Open+Source+DFDL)
to parse a file with a DFDL schema.


RUNNING FROM COMMAND-LINE
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
        schema="../examples/csv/csv.dfdl.xsd" 
	root="ex:file" xmlns:ex="http://example.com />
 
</p:declare-step>

Of course, the magic isn't in simply running Daffodil; the magic is in
manipulating the parsed data using XProc.

To run Calabash with the Daffodil extension, it's as easy as running
it from the command-line with specific arguments:

From Unix/Linux:
$ scala -cp lib/*:lib_managed/*:target/scala-2.9.2/daffodil-calabash-extension_2.9.2-0.4.jar \
com.xmlcalabash.drivers.Main -c etc/calabash-config.xml examples/parse-file-test.xpl

From Windows:
> scala -cp lib\*;lib_managed\*;target\scala-2.9.2\daffodil-calabash-extension_2.9.2-0.4.jar com.xmlcalabash.drivers.Main -c etc\calabash-config.xml examples\parse-file-test.xpl

OR like this for troubleshooting (Unix/Linux example):
$ scala -cp lib/*:lib_managed/*:target/scala-2.9.2/daffodil-calabash-extension_2.9.2-0.4.jar \
-Djava.util.logging.config.file=etc/logging.properties \
com.xmlcalabash.drivers.Main -D -c etc/calabash-config.xml examples/parse-file-test.xpl

Important things to notice in the command-line: 
* This command-line executes Calabash with a custom classpath and
  configuration. 
* The extension and all its dependencies are added to the classpath.
* A configuration file is used to tie the dfdl:parse XProc step to the
  extension code. 


RUNNING REST SERVER
Calabash now comes with a RESTful server that can be used to run pipelines.

To see this in action with the Daffodil extension, start the server like so:

From Unix/Linux:
$ scala -cp lib/*:lib_managed/*:target/scala-2.9.2/daffodil-calabash-extension_2.9.2-0.4.jar \
com.xmlcalabash.drivers.Piperack -c etc/calabash-config.xml

From Windows:
> scala -cp lib\*;lib_managed\*;target\scala-2.9.2\daffodil-calabash-extension_2.9.2-0.4.jar com.xmlcalabash.drivers.Piperack -c etc\calabash-config.xml

Note that the etc/calabash-config.xml configuration file preloads two pipelines:
one for CSV and one for PCAP.

The input to the CSV pipeline is hardcoded to examples/csv/simpleCSV.  To see
this pipeline in action, execute the following (or do something similar with an 
HTML page):
$ curl -X POST http://localhost:8088/pipelines/csv/run

The PCAP pipeline accepts a file option to parse a specified file.  To see this
pipeline in action, open examples/pcap/pcap-test-page.html in a web browser.
Then follow these instructions:
1. Paste the absolute path to one of the sample pcap files in examples/pcap
   into the first text box on the page.
2. Press the Set File button.
3. Press the Run Pipeline button.
4. Look at the output in the Output area.


ACKNOWLEDGEMENTS
Thanks for Florent Georges for his very helpful blog post at 
http://fgeorges.blogspot.com/2011/09/writing-extension-step-for-calabash-to.html.
