package com.google.api.services;

import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.ExponentialBackOff;

import java.io.IOException;

/**
 * Syntactic short-cut that sets up HTTP request retries when an API response errors due to a
 * quota exceeded error. If you wish to tweak the options any further,
 * consider writing your own HttpUnsuccessfulResponseHandler.
 *
 * @see {@link com.google.api.client.util.ExponentialBackOff}
 */
public class BackOffWhenRateLimitedRequestInitializer implements HttpRequestInitializer {

  @Override
  public void initialize(HttpRequest httpRequest) throws IOException {
    // use the built in exponential back-off classes
    HttpBackOffUnsuccessfulResponseHandler failHandler =
        new HttpBackOffUnsuccessfulResponseHandler(new ExponentialBackOff());

    // use this wrapper's custom back-off logic
    failHandler.setBackOffRequired(new RateLimitedBackOffRequired());

    httpRequest.setUnsuccessfulResponseHandler(failHandler);
  }
}