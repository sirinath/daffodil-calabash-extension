/*
 NOTICE

 This technical data was produced for the U. S. Government under
 Contract No. W15P7T-13-C-A802, and is subject to the Rights in
 Technical Data-Noncommercial Items clause at DFARS 252.227-7013 (FEB
 2012)

 Copyright (C) 2013 The MITRE Corporation, All rights reserved.
 */
package edu.illinois.ncsa.daffodil.calabash

import java.io.File
import edu.illinois.ncsa.daffodil.api.Diagnostic
import edu.illinois.ncsa.daffodil.compiler.Compiler
import edu.illinois.ncsa.daffodil.api.DFDL.ProcessorFactory
import scala.Left
import edu.illinois.ncsa.daffodil.api.DFDL.DataProcessor
import edu.illinois.ncsa.daffodil.api.DFDL.ParseResult
import java.nio.channels.ReadableByteChannel


/**
 * Facade with methods that return 
 * Either[Seq[Diagnostic], X], since a series of methods that return Either
 * can be composed more generically than the current design using WithDiagnostics.
 * 
 * Usage: <xmp>
 * import DaffodilFacade._
 * ...
 * val eitherWarningsOrParseResult = 
 * 		compile(Compiler(), schemaFiles)
    	.right.flatMap(getRootProcessor(_))
    	.right.flatMap(parse(_, input, -1))
    	
   // OR, if you prefer 
   val eitherWarningsOrParseResult2 = 
 * 	 for {
      pf <- compile(Compiler(), schemaFiles).right
      dp <- getRootProcessor(pf).right
      pr <- parse(dp, input, -1).right
    } yield pr
    
   eitherWarningsOrParseResult match {
      case Left(diags) => { 
      	Console.err.println("%d errors/warnings detected".format(diags.length))
      	diags.foreach(Console.err.println)
      }
      case Right(pr) => {
	      if (!pr.getDiagnostics.isEmpty) {
	        Console.out.println("%d warnings detected".format(pr.getDiagnostics.length))
	        pr.getDiagnostics.foreach(Console.out.println)
	        Console.out.println
	      }
	      Console.out.println(new PrettyPrinter(80, 2).format(pr.result))
      }
   }
 * </xmp>
 * 
 * @author Jonathan Cranford
 */
object DaffodilFacade
{
  
  /**
   * Compiles the given files using the given Compiler.
   * 
   * @return if an error occurs, returns sequence of diagnostics; otherwise
   * returns a ProcessorFactory
   */
  // NOTE: This method may be moved to the Compiler class
  def compile(
      c: Compiler, 
      schemaFiles: Seq[File]
      ): Either[Seq[Diagnostic], ProcessorFactory] = {
    val pf = c.compile(schemaFiles: _*)
    if (pf.isError) {
      Left(pf.getDiagnostics)
    } else {
      Right(pf)
    }
  }
  
  
  /**
   * Retrieves the root processor from the given ProcessorFactory.
   *  
   * @return if an error occurs, returns sequence of diagnostics; otherwise
   * returns a DataProcessor
   */
  // NOTE: This method may be moved to the ProcessorFactory class
  def getRootProcessor(
      pf: ProcessorFactory
      ): Either[Seq[Diagnostic], DataProcessor] = {
    val dp = pf.onPath("/")
    if (dp.isError) {
      Left(dp.getDiagnostics)
    } else {
      Right(dp)
    }
  }
    
    
    /**
     * Parses the given input using the given DataProcessor.
     */
    // NOTE: This method may be moved to the DataProcessor class.
    def parse(
        dp: DataProcessor, 
        input: ReadableByteChannel, 
        lengthLimitInBits: Long = -1
        ) : Either[Seq[Diagnostic], ParseResult] = {
      val pr = dp.parse(input, lengthLimitInBits)
      if (pr.isError) {
        Left(pr.getDiagnostics)
      } else {
        Right(pr)
      } 
    }
    
}
