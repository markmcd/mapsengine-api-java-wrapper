package com.google.maps.clients;

import com.google.api.client.util.Key;

import java.util.List;

/**
 * An error response as returned by the API.  Used for serialisation via {@link com.google.api
 * .client.json.JsonFactory}.
 */
public class ApiErrorResponseJson {
  @Key public ApiErrorCollectionJson error;

  /** The top-level error message, code and list of individual errors. */
  public static class ApiErrorCollectionJson {
    @Key public List<ApiErrorCollectionJson.ApiErrorJson> errors;
    @Key public int code;
    @Key public String message;

    /** The individual errors. */
    public static class ApiErrorJson {
      @Key public String domain;
      @Key public String reason;
      @Key public String message;
    }
  }
}
