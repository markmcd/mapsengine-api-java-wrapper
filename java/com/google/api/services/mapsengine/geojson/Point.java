package com.google.api.services.mapsengine.geojson;

import com.google.api.services.mapsengine.model.Feature;
import com.google.api.services.mapsengine.model.GeoJsonGeometry;
import com.google.api.services.mapsengine.model.GeoJsonPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A co-ordinate point on the surface of Earth.
 */
public class Point extends Geometry {

  public final double latitude;
  public final double longitude;
  public final double altitude;
  public final boolean hasAltitude;

  /**
   * Constructs a point with the specified latitude and longitude.
   *
   * @param lat  The latitude, between -90 and 90.
   * @param lng  The longitude, between -180 and 180.
   * @throws java.lang.IllegalArgumentException When latitude or long are out of bounds.
   */
  public Point(double lat, double lng) {
    if (lat < -90 || lat > 90) {
      throw new IllegalArgumentException("Latitude is out of bounds");
    }
    if (lng < -180 || lng > 180) {
      throw new IllegalArgumentException("Longitude is out of bounds");
    }

    this.latitude = lat;
    this.longitude = lng;
    this.hasAltitude = false;
    this.altitude = 0.0;
  }

  /**
   * Constructs a point with the specified latitude, longitude and altitude.
   *
   * @param lat  The latitude, between -90 and 90.
   * @param lng  The longitude, between -180 and 180.
   * @param alt  The altitude, in meters.
   */
  public Point(double lat, double lng, double alt) {
    if (lat < -90 || lat > 90) {
      throw new IllegalArgumentException("Latitude is out of bounds");
    }
    if (lng < -180 || lng > 180) {
      throw new IllegalArgumentException("Longitude is out of bounds");
    }

    this.latitude = lat;
    this.longitude = lng;
    this.altitude = alt;
    this.hasAltitude = true;
  }

  /**
   * Converts the provided Feature into a typed Point, discarding properties.  Throws an
   * exception if the feature is not a point.
   * @param feature  the Feature returned by the API
   * @throws IllegalArgumentException when feature is not a single point
   */
  public Point(Feature feature) {
    GeoJsonGeometry geometry = feature.getGeometry();
    if (!(geometry instanceof GeoJsonPoint)) {
      throw new IllegalArgumentException("Feature is not a Point: " + geometry.getType());
    }

    GeoJsonPoint point = (GeoJsonPoint) geometry;
    List<Double> rawPoints = point.getCoordinates();

    if (rawPoints.size() < 2) {
      throw new IllegalArgumentException("Feature must have at least 2 values forming a "
          + "co-ordinate point");
    }

    latitude = rawPoints.get(1);
    longitude = rawPoints.get(0);
    if (rawPoints.size() > 2) {
      altitude = rawPoints.get(2);
      hasAltitude = true;
    } else {
      altitude = 0.0;
      hasAltitude = false;
    }
  }

  /**
   * Constructs a Point based on the list provided.  The list is expected to match the format
   * returned by the Maps Engine API, so should be ordered Longitude, then Latitude, and an
   * optional Altitude.  Any further elements are silently discarded to match the behaviour of
   * the API.
   * @param rawPoints  A list of 2 or more values representing Longitude,
   *  Latitude and optionally Altitude
   */
  public Point(List<Double> rawPoints) {
    if (rawPoints.size() < 2) {
      throw new IllegalArgumentException("Feature must have at least 2 values forming a "
          + "co-ordinate point");
    }

    latitude = rawPoints.get(1);
    longitude = rawPoints.get(0);
    if (rawPoints.size() > 2) {
      altitude = rawPoints.get(2);
      hasAltitude = true;
    } else {
      altitude = 0.0;
      hasAltitude = false;
    }
  }

  /**
   * Returns a Feature that can be used by the Maps Engine API.
   *
   * @param properties The properties to attach to the feature
   * @return an API-compatible Feature object
   */
  @Override
  public Feature asFeature(Map<String, Object> properties) {
    if (properties == null) {
      throw new IllegalArgumentException("Properties are required, even if empty");
    }

    List<Double> coords = new ArrayList<Double>();
    coords.add(longitude);
    coords.add(latitude);
    if (hasAltitude()) {
      coords.add(altitude);
    }

    GeoJsonPoint geometry = new GeoJsonPoint();
    geometry.setCoordinates(coords);

    Feature feature = new Feature();
    feature.setType(FEATURE_TYPE);
    feature.setProperties(properties);
    feature.setGeometry(geometry);

    return feature;
  }

  public List<Double> toCoordinates() {
    List<Double> coords = new ArrayList<Double>();
    coords.add(longitude);
    coords.add(latitude);
    if (hasAltitude()) {
      coords.add(altitude);
    }
    return coords;
  }

  /**
   * Test if this Point has an altitude.
   *
   * @return true if the altitude property has been set.
   */
  public boolean hasAltitude() {
    return hasAltitude;
  }
}
