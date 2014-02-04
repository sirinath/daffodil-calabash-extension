/*
 NOTICE

 This technical data was produced for the U. S. Government under
 Contract No. W15P7T-13-C-A802, and is subject to the Rights in
 Technical Data-Noncommercial Items clause at DFARS 252.227-7013 (FEB
 2012)

 Copyright (C) 2013 The MITRE Corporation, All rights reserved.
 */
package edu.illinois.ncsa.daffodil.calabash

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.nio.channels.ReadableByteChannel
import java.nio.file.Files
import java.nio.file.Paths
import com.xmlcalabash.core.XProcConstants
import com.xmlcalabash.core.XProcRuntime
import com.xmlcalabash.io.WritablePipe
import com.xmlcalabash.library.DefaultStep
import com.xmlcalabash.model.RuntimeValue
import com.xmlcalabash.runtime.XAtomicStep
import com.xmlcalabash.util.TreeWriter
import edu.illinois.ncsa.daffodil.api.DFDL.ParseResult
import edu.illinois.ncsa.daffodil.api.Diagnostic
import edu.illinois.ncsa.daffodil.compiler.Compiler
import net.sf.saxon.s9api.QName
import net.sf.saxon.s9api.XdmNode
import org.xml.sax.InputSource
import java.io.StringReader

/**
 * A Calabash extension step that uses a DFDL schema to parse an input file.
 * 
 * @author Jonathan Cranford
 */
final class DfdlParseStep(runtime: XProcRuntime, step: XAtomicStep)
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
    val eitherPrOrDiags = DfdlParseStep.parse(
        compiler,
        Seq(schemaFile), 
        new FileInputStream(new File(fileURI)).getChannel())
    eitherPrOrDiags match {
      // TODO use something better than Console
      case Left(diags) => {
        Console.err.println("%d errors detected".format(diags.length))
      	diags.foreach(Console.err.println)
      }
      case Right(pr) => {
        if (!pr.getDiagnostics.isEmpty) {
        	Console.out.println("%d warnings detected".format(pr.getDiagnostics.length))
        	pr.getDiagnostics.foreach(Console.out.println)
        	Console.out.println
        }
        // TODO use saxon-jdom to turn JDOM result into XdmNode
        val s = pr.result.toString()
        val is = new InputSource(new StringReader(s))
        val xdmNode = runtime.parse(is)
        // TODO handle warnings
        result.write(xdmNode)
      }
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


object DfdlParseStep {
   
  /**
   * Parses the given input using the given schemaFiles.
   * 
   * @return if an error occurs, returns sequence of diagnostics; otherwise
   * returns a ParseResult
   */
  def parse(
      compiler: Compiler,
      schemaFiles: Seq[File], 
      input: ReadableByteChannel
      ) : Either[Seq[Diagnostic], ParseResult] = {
    for {
      pf <- DaffodilFacade.compile(compiler, schemaFiles).right
      dp <- DaffodilFacade.getRootProcessor(pf).right
      pr <- DaffodilFacade.parse(dp, input, -1).right
    } yield pr
  }
}
