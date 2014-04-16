/*
 NOTICE

 This technical data was produced for the U. S. Government under
 Contract No. W15P7T-13-C-A802, and is subject to the Rights in
 Technical Data-Noncommercial Items clause at DFARS 252.227-7013 (FEB
 2012)

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
 */
package edu.illinois.ncsa.daffodil.calabash

import java.io.File
import java.io.FileInputStream
import java.io.StringReader
import java.net.URI
import java.nio.channels.ReadableByteChannel
import java.nio.file.Files
import java.nio.file.Paths

import org.xml.sax.InputSource

import com.xmlcalabash.core.XProcConstants
import com.xmlcalabash.core.XProcRuntime
import com.xmlcalabash.io.WritablePipe
import com.xmlcalabash.library.DefaultStep
import com.xmlcalabash.model.RuntimeValue
import com.xmlcalabash.runtime.XAtomicStep
import com.xmlcalabash.util.TreeWriter

import edu.illinois.ncsa.daffodil.api.DFDL.ParseResult
import edu.illinois.ncsa.daffodil.api.Diagnostic
import edu.illinois.ncsa.daffodil.calabash.DaffodilFacade.WithDiagnosticsError
import edu.illinois.ncsa.daffodil.calabash.DaffodilFacade.wdToRichWd
import edu.illinois.ncsa.daffodil.compiler.Compiler
import net.sf.saxon.s9api.QName

/**
 * A Calabash extension step that uses a DFDL schema to parse an input file.
 * 
 * @author Jonathan Cranford
 */
final class DfdlParseFileStep(runtime: XProcRuntime, step: XAtomicStep)
extends DefaultStep(runtime, step) {

  val FileOption = new QName("", "file");
  val SchemaOption = new QName("", "schema")
  val RootOption = new QName("", "root")
  var result: WritablePipe = null
  
  
  // TODO add diagnostics output port
  override def setOutput(port: String, pipe: WritablePipe) {
    result = pipe
  }
  
  override def reset() {
    if (result != null) {
    	result.resetWriter()
    }
  }
  
  
  override def run() {
    super.run()
    
    val schemaFile = new File(resolveURI(getOption(SchemaOption)))
    // FIXME - do a better job checking that file exists
    assert(schemaFile.exists())
    
    // read input and wrap for testing
    val fileURI = resolveURI(getOption(FileOption))
//    outputFile(fileURI)
    
    // parse input using schema
    val compiler = Compiler()
    val rootOption = getOption(RootOption)
    if (rootOption != null) {
      val rootQName = rootOption.getQName()
      compiler.setDistinguishedRootNode(rootQName.getLocalName(), rootQName.getNamespaceURI())
    }
    
    val input = new FileInputStream(new File(fileURI)).getChannel()
    try {
      val pr = compiler.compile(schemaFile)
		.mapOrThrow(_.onPath("/"))
		.mapOrThrow(_.parse(input, -1))
      // TODO use something better than Console
      if (!pr.getDiagnostics.isEmpty) {
        Console.out.println(pr.getDiagnosticsMessage)
      }
        // TODO use saxon-jdom to turn JDOM result into XdmNode
        val s = pr.result.toString()
        val is = new InputSource(new StringReader(s))
        val xdmNode = runtime.parse(is)
        result.write(xdmNode)
    } catch {
      case e:WithDiagnosticsError => Console.err.println(e.getDiagnosticsMessage) 
    }
  }
  
  
  
  
//  def toFile(x: XdmNode): File = {
//    val s = makeSerializer()
//    // Create temp file (adapted from XMLUtils)
//    val tmpSchemaFile = File.createTempFile("daffodil_tmp_", ".dfdl.xsd")
//    // Delete temp file when program exits
//    tmpSchemaFile.deleteOnExit
//    using(new BufferedOutputStream(new FileOutputStream(tmpSchemaFile))) {
//      bos: BufferedOutputStream => 
//        s.setOutputStream(bos)
//        S9apiUtils.serialize(runtime, x, s)
//    }
//    tmpSchemaFile
//  }
  
  
  private def resolveURI(v: RuntimeValue): URI =
    v.getBaseURI().resolve(v.getString())
  
  
  private def outputFile(fileURI: java.net.URI): Unit = {
	  val t = new TreeWriter(runtime)
	  t.startDocument(fileURI)
		  t.addStartElement(XProcConstants.c_data)
			  t.startContent()
			  t.addText(new String(Files.readAllBytes(Paths.get(fileURI))))
		  t.addEndElement()
	  t.endDocument()
	  result.write(t.getResult())
  }
  
  
  // TODO add custom LogWriter
  
  // TODO add support for validation of schemas
  
  // TODO add support for DFDL validation
  
}
