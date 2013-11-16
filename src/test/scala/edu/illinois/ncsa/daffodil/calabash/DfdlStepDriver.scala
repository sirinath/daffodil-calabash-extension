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
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.EnumSet
import scala.xml.PrettyPrinter

/**
 * Driver for DfdlStep.  Unit tests should be derived using this driver.
 * 
 * @author Jonathan Cranford
 */
object DfdlStepDriver {

  // assuming project directory is current working directory
  val REL_RESOURCES_DIR = "examples" 
  val CSV_SCHEMA = "csv.dfdl.xsd"
  val CSV_DATA = "simpleCSV"
  
  def main(args: Array[String]): Unit = {
    val eitherWarningsOrParseResult = 
      DfdlParseStep.parse(
          Seq(new File(REL_RESOURCES_DIR, CSV_SCHEMA)),
          Files.newByteChannel(
              Paths.get(REL_RESOURCES_DIR, CSV_DATA), 
              EnumSet.of(StandardOpenOption.READ)));
    eitherWarningsOrParseResult match {
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
	      //Console.out.println(pr.result.toString)
	      Console.out.println(new PrettyPrinter(80, 2).format(pr.result))
      }
    }
  }

}
