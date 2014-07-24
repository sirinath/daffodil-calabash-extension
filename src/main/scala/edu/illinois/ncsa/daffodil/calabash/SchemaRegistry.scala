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
import net.sf.saxon.s9api.QName
import edu.illinois.ncsa.daffodil.api.DFDL.DataProcessor
import edu.illinois.ncsa.daffodil.compiler.Compiler
import edu.illinois.ncsa.daffodil.calabash.DaffodilFacade.WithDiagnosticsError
import edu.illinois.ncsa.daffodil.calabash.DaffodilFacade.wdToRichWd


/**
 * Keeps track of compiled schemas by name.
 * 
 * @author Jonathan Cranford
 */
object SchemaRegistry {
  
  val getOrCompile = new Memoize(compile)

  def compile(args: Tuple2[File, QName]): DataProcessor = {
	  val schemaFile = args._1;
	  val rootQName = args._2;
	  val compiler = Compiler();
	  if (rootQName != null) {
		  compiler.setDistinguishedRootNode(rootQName.getLocalName(), rootQName.getNamespaceURI())
	  }
	  compiler.compile(schemaFile)  
	  	.mapOrThrow(_.onPath("/"));
  }
}