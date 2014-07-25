INTRODUCTION
============

This project contain extension steps for Calabash
(http://xmlcalabash.com/) that uses Daffodil
(https://opensource.ncsa.illinois.edu/confluence/display/DFDL/Daffodil%3A+Open+Source+DFDL)
to parse a file with a DFDL schema.


NEW XPROC STEPS
===============

This project introduces two new steps: dfdl:parse-file and dfdl:parse.


dfdl:parse-file
---------------

dfdl:parse-file parses a file into an XML Infoset, using Daffodil. The XProc 
signature for dfdl:parse-file looks like this:
    
   <declare-step type="dfdl:parse-file">
      <output port="result" />
      <option name="file" required="true" />   
      <option name="schema" required="true" />
      <option name="root" />          <!-- (QName) -->
   </declare-step>

The root option, if present, must be a QName that is passed to the
Daffodil parser as the distinguished root node of the DFDL Schema to
use when parsing.  The distinguished root node specifies how to start
parsing the input when there are multiple top-level elements in the
DFDL Schema.  When the root option is absent, Daffodil uses the first
top-level element as the distinguished root node (roughly equivalent
to the starting non-terminal of a grammar).


dfdl:parse
----------

dfdl:parse uses an input port instead of the file attribute.  

   <declare-step type="dfdl:parse">
      <input port="source" />   
      <output port="result" />
      <option name="schema" required="true" />
      <option name="root" />          <!-- (QName) -->
   </declare-step>

dfdl:parse expects XML on the input port as produced by the p:data
construct in XProc: an XML wrapper around text data with optional
'encoding', 'content-type', and 'charset' attributes. The text content
of the root element of the input is the input for the parser.
         
If the root element has an 'encoding' attribute with a value of
'base64', then the input is base64-decoded before being passed to
Daffodil.
         
Otherwise, if there is a 'charset' attribute or a charset parameter in
a 'content-type' attribute, then the text is encoded using that
character set before being passed to parser.
         
Otherwise, if no valid character set is specified, the text is encoded
to UTF-8 before being passed to the parser.


Differences between dfdl:parse-file and dfdl:parse
--------------------------------------------------
         
dfdl:parse-file is more efficient than dfdl:parse because the input
file is passed directly to Daffodil; in contrast, for dfdl:parse the
input is converted to XML to get it into the XProc pipeline and then
back to a binary stream of data for Daffodil.  For binary input, the
situation is even worse: the data is base64-encoded to get it into the
XProc pipeline, then base64-decoded before being passed to Daffodil.

On the other hand, dfdl:parse can be combined with other steps more 
easily in an XProc pipeline, as other XProc steps can preprocess the
input before dfdl:parse.


RUNNING FROM COMMAND-LINE
=========================

The daffodil-calabash extension currently only works with scala 2.10,
since that's what daffodil 0.13.0 is built with.  Scala 2.10 must be
be downloaded and installed on the PATH before the daffodil-calabash
extension can be used. See http://scala-lang.org/download/all.html to
download Scala 2.10.

The examples directory contains example XProc pipelines for CSV and
PCAP. The PCAP DFDL examples are from https://github.com/DFDLSchemas/PCAP.
Here's a taste:

<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
                xmlns:dfdl="urn:daffodil:calabash"
                version="1.0">
    <p:output port="result"/>
    <p:import href="../../etc/daffodil-library.xpl"/>
    <dfdl:parse-file file="simpleCSV" schema="csv.dfdl.xsd" 
        root="ex:file" xmlns:ex="http://example.com"/>
</p:declare-step>

Of course, the magic isn't in simply running Daffodil; the magic is in
manipulating the parsed data using XProc.

Running Calabash with the Daffodil extension is as easy as running
Calabash from the command-line with specific arguments.  The examples
below assume that Scala 2.10 is available on the PATH.

From Unix/Linux:
$ ./calabash-daffodil.sh examples/csv/csv-file-pipeline.xpl
(or, for a prettier version of the output)
$ ./calabash-daffodil.sh examples/csv/csv-file-pipeline.xpl | xmllint --format -

From Windows:
> scala -cp lib\*;lib_managed\*;target\scala-2.10\daffodil-calabash-extension_2.10-0.5.0.jar com.xmlcalabash.drivers.Main -c etc\calabash-config.xml examples\csv\csv-file-pipeline.xpl

OR like this for troubleshooting (Unix/Linux example):
$ scala -cp lib/*:lib_managed/*:target/scala-2.10/daffodil-calabash-extension_2.10-0.5.0.jar \
-Djava.util.logging.config.file=etc/logging.properties \
com.xmlcalabash.drivers.Main -D -c etc/calabash-config.xml \
examples/csv/csv-file-pipeline.xpl

Important things to notice in the command-line: 
* This command-line executes Calabash with a custom classpath and
  configuration. 
* The extension and all its dependencies are added to the classpath.
* A configuration file is used to tie the dfdl:parse XProc step to the
  extension code. 


ACKNOWLEDGEMENTS
Thanks for Florent Georges for his very helpful blog post at 
http://fgeorges.blogspot.com/2011/09/writing-extension-step-for-calabash-to.html.


NOTICE 
This software was produced for the U. S. Government 
under Contract No. W15P7T-13-C-A802, and is 
subject to the Rights in Noncommercial Computer Software
and Noncommercial Computer Software Documentation 
Clause 252.227-7014 (FEB 2012)

Copyright (C) 2013-2014 The MITRE Corporation, All Rights Reserved.

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal with the Software without
restriction, including without limitation the rights to use, copy,
modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimers.

 2. Redistributions in binary form must reproduce the above
    copyright notice, this list of conditions and the following
    disclaimers in the documentation and/or other materials
    provided with the distribution.

 3. Neither the names of The MITRE Corporation, nor the names of
    its contributors may be used to endorse or promote products
    derived from this Software without specific prior written
    permission.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE CONTRIBUTORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
DEALINGS WITH THE SOFTWARE.


See the NOTICE.txt file in src/main/resources/META-INF for a list of 
libraries that are distributed with their own license terms.
