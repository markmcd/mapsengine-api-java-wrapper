package com.google.api.services.mapsengine;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test the {@link com.google.api.services.mapsengine.Security} class.
 */
@RunWith(JUnit4.class)
public class SecurityTest {

  @Test
  public void testEscapeAndQuoteStringWorksNullToNull() throws Exception {
    // test that nulls work
    assertNull(Security.escapeAndQuoteString(null));
  }

  @Test
  public void testEscapeAndQuoteStringWorksWithSimpleInput() throws Exception {
    // test that no transformation happens here
    String normalInput = "Hello, world!";
    assertEquals("'" + normalInput + "'", Security.escapeAndQuoteString(normalInput));
  }

  @Test
  public void testEscapeAndQuoteStringWorksWithSingleQuotes() throws Exception {
    // test that single quotes are escaped
    String singleQuotedInput = "O'Donald";
    assertEquals("'O\\'Donald'", Security.escapeAndQuoteString(singleQuotedInput));
  }

  @Test
  public void testEscapeAndQuoteStringWorksWithSlashesInInput() throws Exception {
    // test that slashes are escaped
    String slashedInput = "this\\and\\that";
    assertEquals("'this\\\\and\\\\that'", Security.escapeAndQuoteString(slashedInput));
  }

  @Test
  public void testEscapeAndQuoteStringWorksWithMixedInput() throws Exception {
    // test how to behave under a well known XSS attack string
    String sillyMixedInput = "'';!\"<XSS>=&{()}\\";
    assertEquals("'\\'\\';!\"<XSS>=&{()}\\\\'", Security.escapeAndQuoteString(sillyMixedInput));
  }
}
