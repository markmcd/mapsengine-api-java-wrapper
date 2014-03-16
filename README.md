Maps Engine API wrapper lib for Java
====================================

Use [Google Maps Engine]?  Use the [API][maps-engine-api]?
Use Java?  This helper library provides a little bit of sugar to make your life easier.

Background
----------
Traditionally Google release client libraries for each of their APIs, for a
number of different languages.  These [libraries are generated][generator] based
on static API definitions that map the request/response fields to types and
parameters.

While Google are continuously working on these library generators, we feel that
sometimes a human touch can provide a better experience.  This library provides
some more natural ways of interacting with the Maps Engine API in Java.

Features
--------

### POJOs instead of List&lt;List&lt;List&lt;Double&gt;&gt;&gt;

[GeoJSON] defines coordinates using multi-dimensional lists of doubles, which is exactly
how the machine-generated libraries present them.  Here's our alternative.

```java
// Creating a polygon
Polygon poly = Polygon.createSimplePolygon(Arrays.asList(
    new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(0, 1), new Point(0, 0)
));
Feature feature = poly.asFeature(properties);
```

The objects are currently very lightweight.  If you want to see some more functionality,
add a request to the [Issue Tracker].


### Automatic retry of requests when rate limited

When your application is firing requests too frequently, the API server will deny them,
sending `quotaExceeded` responses.  This request initializer will monitor responses,
check for a failure due to rate limiting and retry with back-off, using the [default
exponential back-off policy][backoff-policy].

```java
HttpRequestInitializer retrier = new BackOffWhenRateLimitedRequestInitializer();

Mapsengine engine = new Mapsengine.Builder(httpTransport, jsonFactory, retrier)
    .setApplicationName("Google-MapsEngineSample/1.0")
    .build();
```

### HttpRequestInitializer chaining

The API client library only allows a single [HttpRequestInitializer], including the one
required for authenticating requests with OAuth credentials.  Rather than forcing you to
write your own custom initializer to handle everything, you can chain them together using
`HttpRequestInitializerPipeline`, like so.

```java
List<HttpRequestInitializer> httpInits = new ArrayList<HttpRequestInitializer>();

// perform oauth steps
GoogleCredential credential = authorize();
httpInits.add(credential);

// ensure we retry if throttled
httpInits.add(new BackOffWhenRateLimitedRequestInitializer());

HttpRequestInitializer pipeline = new HttpRequestInitializerPipeline(httpInits);
Mapsengine engine = new Mapsengine.Builder(transport, jsonFactory, pipeline)
    .setApplicationName("Google-MapsEngineSample/1.0")
    .build();
```

### Where clause escaping

Maps Engine's SQL-like query syntax is simple & convenient, particularly if you are
familiar with SQL.  One side-effect is that it does require the developer to properly
escape any query data that has come from an untrusted source (such as a text box on
a web page).  The `Security` class comes with an escaping and quoting function to help.

```java
String untrustedInput = "Alice' OR gx_id = 1234 AND name <> '";

// bad!
FeaturesListResponse badResponse = engine.tables().features().list(TABLE_ID)
    .setWhere("name = '" + untrustedInput + "'")
    .execute();

// good!
FeaturesListResponse goodResponse = engine.tables().features().list(TABLE_ID)
    .setWhere(String.format("name = %s", Security.escapeAndQuoteString(untrustedInput)))
    .execute();
```

How to use
----------

Download the [latest JAR], update pom.xml (Maven) or update build.gradle (Gradle).   Note
that the direct JAR download depends on the [Google API client] and the [Google HTTP
client] libraries for Java.  You'll need to download and add them to your project too.

### Maven
```xml
<dependency>
    <groupId>com.google.mapsengine</groupId>
    <artifactId>wrapper</artifactId>
    <version>(insert latest version)</version>
</dependency>
```

### Gradle
```groovy
repositories {
    mavenCentral()
}

dependencies {
    compile 'com.google.mapsengine:wrapper:(insert latest version)'
    ...
}
```

You can find the latest version by searching [Maven Central] or [Gradle, Please].

Support
-------
This library is provided for public use on a best-effort basis.  We'd love for you
to help it grow by forking & contributing (we love pull requests!)

That said, if you have an issue you can post your questions on [Stack Overflow]
(be sure to tag them with `google-maps-engine`).  You can also try the [Google
Maps Engine Users] mailing list.  If you've found a bug or have a feature request
that you can't fulfill yourself, please use the [Issue Tracker].

Contraindications
-----------------
This library may not be for you, if:

 * You aren't using Java,
 * You prefer to hand-roll your HTTP API requests,
 * You prefer to use a REST library for your API requests, such as [Retrofit][retrofit],
 * Need commercial support (talk to your account manager about options)


[google maps engine]: http://www.google.com.au/enterprise/mapsearth/products/mapsengine.html
[maps-engine-api]: https://developers.google.com/maps-engine/
[issue tracker]: https://github.com/googlemaps/mapsengine-api-java-client/issues
[generator]: https://code.google.com/p/google-apis-client-generator/
[geojson]: http://geojson.org/
[backoff-policy]: http://javadoc.google-http-java-client.googlecode.com/hg/1.17.0-rc/index.html?com/google/api/client/util/ExponentialBackOff.html
[httprequestinitializer]: http://javadoc.google-http-java-client.googlecode.com/hg/1.17.0-rc/index.html?com/google/api/client/http/HttpRequestInitializer.html
[latest jar]: TODO!
[google api client]: https://code.google.com/p/google-api-java-client/
[google http client]: https://code.google.com/p/google-http-java-client/
[maven central]: http://search.maven.org/
[gradle, please]: http://gradleplease.appspot.com/
[stack overflow]: http://stackoverflow.com/
[google maps engine users]: https://groups.google.com/forum/#!forum/google-maps-engine-users
[retrofit]: http://square.github.io/retrofit/

