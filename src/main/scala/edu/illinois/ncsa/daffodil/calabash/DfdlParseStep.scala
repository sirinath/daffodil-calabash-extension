/*
 NOTICE

 This technical data was produced for the U. S. Government under
 Contract No. W15P7T-13-C-A802, and is subject to the Rights in
 Technical Data-Noncommercial Items clause at DFARS 252.227-7013 (FEB
 2012)

 Copyright (C) 2013-2014 The MITRE Corporation, All rights reserved.
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
import com.xmlcalabash.io.ReadablePipe
import com.xmlcalabash.util.Base64
import java.io.ByteArrayInputStream
import java.nio.channels.Channels

/**
 * A Calabash extension step that uses a DFDL schema to parse data from input
 * port.
 * 
 * @author Jonathan Cranford
 */
final class DfdlParseStep(runtime: XProcRuntime, step: XAtomicStep)
extends DefaultStep(runtime, step) {
  
  // init block 
  {
	  Console.out.println("DfdlParseStep constructor called")
  }

  val SchemaOption = new QName("", "schema")
  val RootOption = new QName("", "root")
  var result: WritablePipe = null
  var inputPipe: ReadablePipe = null
  
  override def setInput(port: String, pipe: ReadablePipe) {
    inputPipe = pipe
  }
  
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
    // FIXME - do a better job reporting an error if schema file doesn't exist
    // see Error step for better way to report
    assert(schemaFile.exists())
    
    // decode input - assumes that the text node of the root element is
    // base64-encoded, as is done by p:data
    // Downside of this design: it reads the whole document into memory.
    // As near as I can tell, this is a limitation of Calabash.
    val text = inputPipe.read().getUnderlyingNode().getStringValue();
    val decoded = Base64.decode(text)
//    Console.out.println("base64-encoded input is: ")
//    Console.out.println(text)
    
    // parse input using schema
    val compiler = Compiler()
    val rootOption = getOption(RootOption)
    if (rootOption != null) {
      val rootQName = rootOption.getQName()
      compiler.setDistinguishedRootNode(rootQName.getLocalName(), rootQName.getNamespaceURI())
    }
    
    val input = Channels.newChannel(new ByteArrayInputStream(decoded))
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
  
  
//  private def resolveURI(v: RuntimeValue): URI =
//    v.getBaseURI().resolve(v.getString())
//  
//  
//  private def outputFile(fileURI: java.net.URI): Unit = {
//	  val t = new TreeWriter(runtime)
//	  t.startDocument(fileURI)
//		  t.addStartElement(XProcConstants.c_data)
//			  t.startContent()
//			  t.addText(new String(Files.readAllBytes(Paths.get(fileURI))))
//		  t.addEndElement()
//	  t.endDocument()
//	  result.write(t.getResult())
//  }
//  
  
  // TODO add custom LogWriter
  
  // TODO add support for validation of schemas
  
  // TODO add support for DFDL validation
  
}
