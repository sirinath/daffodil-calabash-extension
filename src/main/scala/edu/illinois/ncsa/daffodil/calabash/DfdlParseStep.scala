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

import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.net.URI
import java.nio.channels.Channels
import java.nio.charset.Charset

import org.apache.commons.io.input.CharSequenceInputStream

import com.xmlcalabash.core.XProcRuntime
import com.xmlcalabash.io.ReadablePipe
import com.xmlcalabash.model.RuntimeValue
import com.xmlcalabash.runtime.XAtomicStep
import com.xmlcalabash.util.Base64

import net.sf.saxon.s9api.XdmNode

/**
 * A Calabash extension step that uses a DFDL schema to parse data from input
 * port.
 * 
 * @author Jonathan Cranford
 */
final class DfdlParseStep(runtime: XProcRuntime, step: XAtomicStep)
extends AbstractDfdlParseStep(runtime, step) with DfdlParseUtilities {
  
  val xpathCompiler = runtime.getProcessor().newXPathCompiler()

  val EncodingXPath = "/*/@encoding"
  val CharsetXPath = "/*/@charset"
  val ContentTypeXPath = "/*/@content-type"
      
  
  // init block 
  {
	  debug("constructor called")
	  xpathCompiler.setCaching(true)
  }

  var inputPipe: ReadablePipe = null
  
  override def setInput(port: String, pipe: ReadablePipe) {
    inputPipe = pipe
  }
  
  
  override def run() {
    super.run()
    
    val schemaFile = new File(resolveURI(getOption(SchemaOption)))
    // FIXME - do a better job reporting an error if schema file doesn't exist
    // see Error step for better way to report
    assert(schemaFile.exists())
    
    /*
     * The text content of the root element of the input is the input for the
     * parser.
         If the root element has an 'encoding' attribute with a value of 
         'base64', then the input is base64-decoded before being passed
         to Daffodil.
         
         charset - either charset attribute or charset in content-type attribute
         - encode to that before passing to parser; otherwise, use UTF-8
         
         Note that p:data produces exactly the right kind of XML to be
         processed by this step.  See http://www.w3.org/TR/xproc/#p.data
         for more details.
     */
    // Downside of this design: it reads the whole document into memory.
    // As near as I can tell, this is a limitation of Calabash.
    val inputNode = inputPipe.read()
    val text = inputNode.getUnderlyingNode().getStringValue();
    
    val encodingAtt = xpathCompiler.evaluateSingle(EncodingXPath, inputNode)
    val encoding = if (encodingAtt == null) null else encodingAtt.getStringValue()
    debug("encoding is " + encoding)
    val inputStream: InputStream = if ("base64".equals(encoding)) {
    	debug("Using base64 encoding...");
	    new ByteArrayInputStream(Base64.decode(text))
    } else {
    	val charset = getDeclaredCharset(inputNode)
    	debug("Using " + charset + " charset...")
		new CharSequenceInputStream(text, toCharset(charset))
    }
    
    parse(schemaFile, Channels.newChannel(inputStream))
  }
  
  
  // utilty method - if something more complex is needed, use a real logging framework
  private def debug(s: String) {
    if (runtime.getDebug()) {
      Console.err.println(getClass().getSimpleName() + ": " + s)
    }
  }
  
  
  /**
   *  charset - either charset attribute or charset in content-type attribute;
         otherwise, use the default
   */
  private def getDeclaredCharset(root: XdmNode): String = {
    val charsetAtt = xpathCompiler.evaluateSingle(CharsetXPath, root)
    if (charsetAtt == null) {
    	val contentType = xpathCompiler.evaluateSingle(ContentTypeXPath, root)
    	if (contentType == null) {
    	  DefaultCharset
    	} else {
    	  parseCharsetFromContentType(contentType.getStringValue())
    	}
    } else {
    	charsetAtt.getStringValue()
    }
  }
  
  
  /**
   * Converts given charset to Charset object, using DefaultCharset in case
   * of failure.
   */
  private def toCharset(charset: String): Charset = {
    try {
      Charset.forName(charset)
    } catch {
      case e:Exception => {
        // TODO use something better than Console
        Console.err.println("Invalid charset: " + charset + ": " + e.toString)
        Charset.forName(DefaultCharset)
      }
    }
  }
  
  
  // Resolves URIs against the base-uri.  Works with relative paths too.
  private def resolveURI(v: RuntimeValue): URI = v.getBaseURI().resolve(v.getString());
  
   
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


// trait to hold utility methods and constants to simplify testing
trait DfdlParseUtilities {
  
  val DefaultCharset = "UTF-8"
    
  // public to allow for unit testing
  def parseCharsetFromContentType(contentType: String): String = {
    // NOTE: This is a hack.  A secure implementation would restrict this to valid charsets only.
    val charsetRegex = """;[ \t]*charset[ \t]*=[ \t]*"?([-\w\.:+]+)""".r
	charsetRegex findFirstIn contentType match {
	  case Some(charsetRegex(cs)) => cs			// extracts from regex group
	  case None				    => DefaultCharset
    }
  }
}