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

import scala.language.implicitConversions

import edu.illinois.ncsa.daffodil.api.WithDiagnostics



/**
 * Facade for Daffodil that allows for easier chaining of method calls. 
 * 
 * Usage: <xmp>
 * import edu.illinois.ncsa.daffodil.compiler.Compiler
 * import edu.illinois.ncsa.daffodil.calabash.DaffodilFacade._
 * ...
 * val compiler = Compiler()
 * ...
   try {
      val pr = compiler.compile(schemaFile)
            .mapOrThrow(_.onPath("/"))
            .mapOrThrow(_.parse(input, -1))
      if (!pr.getDiagnostics.isEmpty) {
        Console.out.println(pr.getDiagnosticsMessage)
      }
      // Do something with pr:ParseResult 
   } catch {
      case e:WithDiagnosticsError => Console.err.println(e.getDiagnosticsMessage) 
   }
 * </xmp>
 * 
 * @author Jonathan Cranford
 */
object DaffodilFacade
{
  
  /** Adds a few methods to WithDiagnostics. */
  // DEVNOTE - when upgrading to Scala 2.10, this can be turned
  //into an implicit class
  class RichWithDiagnostics[A <: WithDiagnostics](w: A) {

    /**
      * If there is no error, calls the given function with the
      * current instance; otherwise, throws a
      * WithDiagnosticsError.  Facilitates chaining methods from
      * several WithDiagnostics classes together.
      */
    def mapOrThrow[B <: WithDiagnostics](f: A => B): B = {
      if (w.isError)
        throw new WithDiagnosticsError(w)
      else
        f(w)
    }
    
    lazy val errors = w.getDiagnostics.filter(_.isError)
    lazy val nonErrorDiagnostics = w.getDiagnostics.filter(!_.isError)

    def getDiagnosticsMessage = {
      val sb: StringBuilder = new StringBuilder(if (w.isError) "ERROR: " else "")
      val nl = System.getProperty("line.separator")
      
      if (errors.length > 0) {
        sb.append("%d errors detected".format(errors.length)).append(nl)
        errors.foreach(sb.append(_).append(nl))
        sb.append(nl)
      }
      if (nonErrorDiagnostics.length > 0) {
        sb.append("%d diagnostics detected".format(nonErrorDiagnostics.length)).append(nl)
        nonErrorDiagnostics.foreach(sb.append(_).append(nl))
      }
      sb.toString
    }
  }
  
  
  implicit final def wdToRichWd[A <: WithDiagnostics](w: A) = 
    new RichWithDiagnostics(w)
  
  
  /** A RuntimeException used to propagate a WithDiagnostics error. */
  class WithDiagnosticsError(diagnostics: WithDiagnostics)
      extends RuntimeException with WithDiagnostics {
    
    /** This class should only be used when the wrapped class is an error
      *  (that is, when isError() == true).
      */  
    require (diagnostics.isError)
    
    def getDiagnostics = diagnostics.getDiagnostics
    def isError = diagnostics.isError
    
    override def getMessage() = wdToRichWd(diagnostics).getDiagnosticsMessage
  }

}
