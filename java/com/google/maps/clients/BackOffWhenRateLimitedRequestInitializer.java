package com.google.maps.clients;

import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.ExponentialBackOff;

import java.io.IOException;

/**
 * Syntactic short-cut that sets up HTTP request retries when an API response errors due to
 * quota exceeded errors or back-end errors. If you wish to tweak the options any further,
 * consider writing your own HttpUnsuccessfulResponseHandler.
 *
 * {@link com.google.api.client.util.ExponentialBackOff}
 */
public class BackOffWhenRateLimitedRequestInitializer implements HttpRequestInitializer {

  protected RateLimitedBackOffRequired backOffRequired;

  @Override
  public void initialize(HttpRequest httpRequest) throws IOException {
    // use the built in exponential back-off classes
    HttpBackOffUnsuccessfulResponseHandler failHandler =
        new HttpBackOffUnsuccessfulResponseHandler(new ExponentialBackOff());

    // use this wrapper's custom back-off logic
    backOffRequired = new RateLimitedBackOffRequired();
    failHandler.setBackOffRequired(backOffRequired);

    httpRequest.setUnsuccessfulResponseHandler(failHandler);
  }

  /**
   * Retrieve the last response body. Responses are cached with each processed request to ensure
   * that the data from an InputStream that has been consumed can be made available.
   *
   * You should only rely on the data in this method when catching a GoogleJsonResponseException
   * and when the getContent or getDetails calls return null. This is not thread-safe.
   *
   * @return The full response body from the last JSON error response handled by the
   * UnsuccessfulResponseHandler.
   */
  public String getLastResponseBody() {
    return backOffRequired.responseBody;
  }
}
