package com.google.maps.clients;

import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler.BackOffRequired;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * Back-off only when the API response signals a "Quota Exceeded" error.
 */
public class RateLimitedBackOffRequired implements BackOffRequired {

  protected static final String QUOTA_EXCEEDED_REASON = "rateLimitExceeded";
  protected static final String BACKEND_REASON = "backendError";

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
      // Copy the response body, leaving the stream open so that HttpResponse.execute() can use it.
      Scanner scanner = new Scanner(httpResponse.getContent(),
          httpResponse.getContentCharset().toString()).useDelimiter("\\A");
      String body = scanner.next();
      if (body == null || body.isEmpty()) {
        return false;
      }

      // Parse the response as JSON.
      JsonObjectParser jsonParser = jsonFactory.createJsonObjectParser();
      ApiErrorResponseJson apiError = jsonParser.parseAndClose(
          new ByteArrayInputStream(body.getBytes()), httpResponse.getContentCharset(),
          ApiErrorResponseJson.class);

      // we will only retry if the *only* failure reason was due to a known error
      if (apiError != null && apiError.error != null && apiError.error.errors != null
          && apiError.error.errors.size() == 1) {
        String reason = apiError.error.errors.get(0).reason;
        if (QUOTA_EXCEEDED_REASON.equals(reason) || BACKEND_REASON.equals(reason)) {
          return true;
        }
      }

      // Known bug: Here the InputStream has been read, so any further dependencies on the
      // stream will not find any data (specifically the getContent/getDetails methods in
      // GoogleJsonResponseException). There is currently no way to mark/reset this stream or to
      // throw a GoogleJsonResponseException as the HttpResponse class is final and can't be
      // re-created with a new LowLevelHttpResponse. The output will be logged according to the
      // default logging settings, at the CONFIG log level.

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
