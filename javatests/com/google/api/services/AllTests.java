package com.google.api.services;

import org.junit.runner.JUnitCore;

/**
 * Run the tests in this package.  Since PowerMock requires it's own Runner we need to explicitly
 * list each the classes.
 *
 * @author macd@google.com (Mark McDonald)
 */
public class AllTests {
  private static final String[] TEST_CLASSES = {
      HttpRequestInitializerPipelineTest.class.getName(),
      RateLimitedBackOffRequiredTest.class.getName()
  };

  public static void main(String[] argv) throws Exception {
    JUnitCore.main(TEST_CLASSES);
  }
}

