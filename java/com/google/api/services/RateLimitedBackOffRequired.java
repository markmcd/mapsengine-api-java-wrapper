package com.google.api.services;

import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler.BackOffRequired;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;

/**
 * Back-off only when the API response signals a "Quota Exceeded" error.
 */
class RateLimitedBackOffRequired implements BackOffRequired {

  protected static final String QUOTA_EXCEEDED_REASON = "quotaExceeded";

  private JsonFactory jsonFactory;

  /**
   * Create a RateLimitedBackOffRequired using the default {@link JsonFactory},
   * a {@link GsonFactory}.
   */
  public RateLimitedBackOffRequired() {
    jsonFactory = new GsonFactory();
  }

  /**
   * Create a RateLimitedBackOffRequired using the specified {@link JsonFactory}.
   * @param jsonFactory  The JSON library to use to parse the HTTP response
   */
  public RateLimitedBackOffRequired(JsonFactory jsonFactory) {
    this.jsonFactory = jsonFactory;
  }

  @Override
  public boolean isRequired(HttpResponse httpResponse) {
    try {
      JsonObjectParser jsonParser = jsonFactory.createJsonObjectParser();
      ApiErrorResponseJson apiError = jsonParser.parseAndClose(httpResponse.getContent(),
          httpResponse.getContentCharset(), ApiErrorResponseJson.class);

      // we will only retry if the *only* failure reason was due to quota
      if (apiError != null && apiError.error != null && apiError.error.errors != null &&
          apiError.error.errors.size() == 1 &&
          QUOTA_EXCEEDED_REASON.equals(apiError.error.errors.get(0).reason)) {
        return true;
      }

      return false;
    } catch (IOException e) {
      // This could be thrown from the HttpResponse or during the parsing phase.  If it's a
      // genuine I/O issue, then we can't handle or re-throw, so return false to be safe.  If
      // it's a parsing error, then we genuinely want to decline the retry,
      // since it's not something we're expecting.
      return false;
    }
  }
}