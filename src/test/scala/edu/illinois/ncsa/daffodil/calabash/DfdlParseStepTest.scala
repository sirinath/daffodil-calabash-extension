package edu.illinois.ncsa.daffodil.calabash

import org.junit.Test
import org.junit.Assert.assertEquals

final class DfdlParseStepTest {

  val fixture = new Object with DfdlParseUtilities
  
  @Test
  def parseCharsetFromContentType {
    assertEquals("US-ASCII", fixture.parseCharsetFromContentType("test/csv;charset=US-ASCII")) 
    assertEquals("US-ASCII", fixture.parseCharsetFromContentType("test/csv; charset = US-ASCII"))
	assertEquals("US-ASCII", fixture.parseCharsetFromContentType("test/csv; charset = \"US-ASCII\" "))
  }
  
}