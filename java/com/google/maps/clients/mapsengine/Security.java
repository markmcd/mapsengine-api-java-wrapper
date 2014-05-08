package com.google.maps.clients.mapsengine;

import java.util.regex.Matcher;

/**
 * Provides some security tools for manipulating data in Maps Engine.
 *
 * @author macd@google.com (Mark McDonald)
 */
public class Security {

  private Security() {}

  /**
   * Escapes any internal quotes and ensures the parameter is correctly (single)
   * quoted.  This is intended for use in string components of ‘where’ clauses,
   * where user input is untrusted and potentially harmful.  This is not meant
   * for use in quoting column names or aliases in ‘select’ clauses, where
   * quoting is different.
   *
   * {@code FeaturesListResponse response = engine.tables().features().list(TABLE_ID)
   *    .setWhere(String.format("mycolumn = %s", Security.escapeAndQuoteString(userInput)));
   * }
   *
   * @param in A string to escape
   * @return If null input, then null output.  Otherwise a quoted, escaped version of the input.
   */
  public static String escapeAndQuoteString(String in) {
    if (in == null) {
      return null;
    }

    String out = in
        .replaceAll("\\\\", Matcher.quoteReplacement("\\\\"))
        .replaceAll("'", Matcher.quoteReplacement("\\'"));

    return "'" + out + "'";
  }
}
