package com.google.maps.clients.mapsengine.geojson;

import com.google.api.services.mapsengine.model.Feature;
import com.google.api.services.mapsengine.model.GeoJsonGeometry;
import com.google.api.services.mapsengine.model.GeoJsonLineString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A collection of points representing a line.
 */
public class LineString extends Geometry {
  protected final List<Point> points;

  /**
   * Constructs a LineString based on the collection of points provided.
   * @param points The Points to use, in order
   */
  public LineString(List<Point> points) {
    this.points = points;
  }

  /**
   * Converts the provided Feature into a typed LineString, discarding properties.  Throws an
   * exception if the feature is not a line string.
   * @param feature  the Feature returned by the API
   * @throws IllegalArgumentException when feature is not a line string
   */
  public LineString(Feature feature) {
    GeoJsonGeometry geometry = feature.getGeometry();
    if (!(geometry instanceof GeoJsonLineString)) {
      throw new IllegalArgumentException("Feature is not a LineString: " + geometry.getType());
    }

    GeoJsonLineString lineString = (GeoJsonLineString) geometry;
    List<List<Double>> rawPoints = lineString.getCoordinates();
    points = fromRawPoints(rawPoints).getPoints();
  }

  /**
   * Construct a LineString from the raw list of points provided.  The list is expected to match
   * the format returned by the Maps Engine API.
   * @param rawPoints  The list of points (List<BigDecimal>) matching the format described in
   *   {@link Point#Point(java.util.List)}
   */
  static LineString fromRawPoints(List<List<Double>> rawPoints) {
    List<Point> points = new ArrayList<Point>(rawPoints.size());
    for (List<Double> rawPoint : rawPoints) {
      points.add(new Point(rawPoint));
    }
    return new LineString(points);
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

    List<List<Double>> coords = new ArrayList<List<Double>>(points.size());
    for (Point p : points) {
      coords.add(p.toCoordinates());
    }

    GeoJsonLineString geometry = new GeoJsonLineString();
    geometry.setCoordinates(coords);

    Feature feature = new Feature();
    feature.setType(FEATURE_TYPE);
    feature.setProperties(properties);
    feature.setGeometry(geometry);

    return feature;
  }

  public List<Point> getPoints() {
    return points;
  }

  List<List<Double>> toCoordinates() {
    List<List<Double>> coords = new ArrayList<List<Double>>(points.size());
    for (Point p : points) {
      coords.add(p.toCoordinates());
    }
    return coords;
  }
}
