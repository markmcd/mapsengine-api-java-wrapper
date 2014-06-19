package com.google.maps.clients;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler.BackOffRequired;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.jackson.JacksonFactory;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Tests for {@link com.google.maps.clients.RateLimitedBackOffRequired}.
 */
@PrepareForTest(HttpResponse.class)
@RunWith(PowerMockRunner.class)
public class RateLimitedBackOffRequiredTest {
  @Test
  public void testRetryHappensWhenQuotaExceeded() throws Exception {
    // typical response
    Charset responseCharset = Charset.forName("UTF-8");
    String apiErrorResponse = "{\n"
        + " \"error\": {\n"
        + "  \"errors\": [\n"
        + "   {\n"
        + "    \"domain\": \"usageLimits\",\n"
        + "    \"reason\": \"rateLimitExceeded\",\n"
        + "    \"message\": \"Rate Limit Exceeded\"\n"
        + "   }\n"
        + "  ],\n"
        + "  \"code\": 403,\n"
        + "  \"message\": \"Rate Limit Exceeded\"\n"
        + " }\n"
        + "}\n";
    InputStream apiErrorStream = new ByteArrayInputStream(apiErrorResponse.getBytes());

    // mock magic
    HttpResponse mockResponse = PowerMock.createNiceMock(HttpResponse.class);
    expect(mockResponse.getContentCharset()).andReturn(responseCharset).anyTimes();
    expect(mockResponse.getContent()).andReturn(apiErrorStream);
    replay(mockResponse);

    // code under test
    BackOffRequired backOff = new RateLimitedBackOffRequired();
    boolean actual = backOff.isRequired(mockResponse);

    Assert.assertEquals(true, actual);
  }

  @Test
  public void testRetryHappensWithBackendError() throws Exception {
    // typical response
    Charset responseCharset = Charset.forName("UTF-8");
    int statusCode = 503;
    String apiResponse = "{\n"
        + " \"error\": {\n"
        + "  \"errors\": [\n"
        + "   {\n"
        + "    \"domain\": \"global\",\n"
        + "    \"reason\": \"backendError\",\n"
        + "    \"message\": \"A service exceeded the maximum allowed time.\"\n"
        + "   }\n"
        + "  ],\n"
        + "  \"code\": 503,\n"
        + "  \"message\": \"A service exceeded the maximum allowed time.\"\n"
        + " }\n"
        + "}\n";
    InputStream apiErrorStream = new ByteArrayInputStream(apiResponse.getBytes());

    // mock magic
    HttpResponse mockResponse = PowerMock.createNiceMock(HttpResponse.class);
    expect(mockResponse.getContentCharset()).andReturn(responseCharset).anyTimes();
    expect(mockResponse.getStatusCode()).andReturn(statusCode).anyTimes();
    expect(mockResponse.getContent()).andReturn(apiErrorStream);
    replay(mockResponse);

    // code under test
    BackOffRequired backOff = new RateLimitedBackOffRequired();
    boolean actual = backOff.isRequired(mockResponse);

    Assert.assertEquals(true, actual);
  }

  @Test
  public void testRetryDoesNotHappenWithNonQuotaError() throws Exception {
    // typical response
    Charset responseCharset = Charset.forName("UTF-8");
    String apiResponse = "{\n"
        + " \"error\": {\n"
        + "  \"errors\": [\n"
        + "   {\n"
        + "    \"domain\": \"usageLimits\",\n"
        + "    \"reason\": \"limitExceeded\",\n"
        + "    \"message\": \"This resource is too large to be accessed via this API call.\",\n"
        + "   }\n"
        + "  ],\n"
        + "  \"code\": 403,\n"
        + "  \"message\": \"This resource is too large to be accessed via this API call.\"\n"
        + " }\n"
        + "}";
    InputStream apiErrorStream = new ByteArrayInputStream(apiResponse.getBytes());

    // mock magic
    HttpResponse mockResponse = PowerMock.createNiceMock(HttpResponse.class);
    expect(mockResponse.getContentCharset()).andReturn(responseCharset).anyTimes();
    expect(mockResponse.getContent()).andReturn(apiErrorStream);
    replay(mockResponse);

    // code under test
    BackOffRequired backOff = new RateLimitedBackOffRequired();
    boolean actual = backOff.isRequired(mockResponse);

    Assert.assertEquals(false, actual);
  }

  @Test
  public void testRetryWithJacksonFactory() throws Exception {
    // typical response
    Charset responseCharset = Charset.forName("UTF-8");
    String apiErrorResponse = "{\n"
        + " \"error\": {\n"
        + "  \"errors\": [\n"
        + "   {\n"
        + "    \"domain\": \"usageLimits\",\n"
        + "    \"reason\": \"rateLimitExceeded\",\n"
        + "    \"message\": \"Rate Limit Exceeded\"\n"
        + "   }\n"
        + "  ],\n"
        + "  \"code\": 403,\n"
        + "  \"message\": \"Rate Limit Exceeded\"\n"
        + " }\n"
        + "}\n";
    InputStream apiErrorStream = new ByteArrayInputStream(apiErrorResponse.getBytes());

    // mock magic
    HttpResponse mockResponse = PowerMock.createNiceMock(HttpResponse.class);
    expect(mockResponse.getContentCharset()).andReturn(responseCharset).anyTimes();
    expect(mockResponse.getContent()).andReturn(apiErrorStream);
    replay(mockResponse);

    // code under test
    BackOffRequired backOff = new RateLimitedBackOffRequired(new JacksonFactory());
    boolean actual = backOff.isRequired(mockResponse);

    Assert.assertEquals(true, actual);
  }
}
