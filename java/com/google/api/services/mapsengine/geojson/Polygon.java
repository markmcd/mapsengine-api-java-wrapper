package com.google.api.services.mapsengine.geojson;

import com.google.api.services.mapsengine.model.Feature;
import com.google.api.services.mapsengine.model.GeoJsonGeometry;
import com.google.api.services.mapsengine.model.GeoJsonPolygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A 3 or more sided shape with straight sides.
 */
public class Polygon extends Geometry {

  protected List<List<Point>> points;

  /**
   * Disallow constructor in favour of static factory methods below.  Required due to type erasure.
   */
  private Polygon() {}

  /**
   * Converts the provided Feature into a typed Polygon, discarding properties.  Throws an
   * exception if the feature is not a polygon.
   * @param feature  the Feature returned by the API
   * @throws IllegalArgumentException when feature is not a polygon
   */
  public Polygon(Feature feature) {
    GeoJsonGeometry geometry = feature.getGeometry();
    if (!(geometry instanceof GeoJsonPolygon)) {
      throw new IllegalArgumentException("Feature is not a Polygon: " + geometry.getType());
    }

    GeoJsonPolygon polygon = (GeoJsonPolygon) geometry;

    List<List<List<Double>>> rawPoints = polygon.getCoordinates();

    points = fromRawPoints(rawPoints).points;
  }

  /**
   * Creates a polygon using the list of points provided.  The first and last points must be the
   * same in order to close the polygon, there must be four or more points and they must be in
   * counter-clockwise order.  No checking is done in this client, any errors will be returned by
   * the server-side API.
   * @param points  A list of 4 or more points, in counter-clockwise order
   */
  public static Polygon createSimplePolygon(List<Point> points) {
    Polygon poly = new Polygon();
    poly.points = Collections.singletonList(points);
    return poly;
  }

  /**
   * Creates a multi-ring polygon using the list of points provided.  The first outer list
   * represents the outer ring of the polygon and each successive outer list represents inner
   * rings, or holes, in the polygon.  The first and last points of each ring must be the same in
   * order to close the ring, there must be four or more points and they must be in
   * counter-clockwise  order.  No checking is done in this client, any errors will be returned
   * by the server-side API.
   * @param points  A list of 1 or more linear rings (lists of points)
   */
  public static Polygon createMultiRingPolygon(List<List<Point>> points) {
    Polygon poly = new Polygon();
    poly.points = points;
    return poly;
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

    GeoJsonPolygon geometry = new GeoJsonPolygon();
    geometry.setCoordinates(toRawPoints());

    Feature feature = new Feature();
    feature.setType(FEATURE_TYPE);
    feature.setProperties(properties);
    feature.setGeometry(geometry);

    return feature;
  }

  /**
   * Construct a Polygon from the raw list of points provided.  The list is expected to match
   * the format returned by the Maps Engine API.
   * @param rawPoints  The list of rings (outer first, then inner),
   *   which are lists of points (List<BigDecimal>) matching the format described in {@link
   *   Point#Point(java.util.List)}
   */
  static Polygon fromRawPoints(List<List<List<Double>>> rawPoints) {
    List<List<Point>> niceRingPoints = new ArrayList<List<Point>>(rawPoints.size());
    for (List<List<Double>> rawRingPoints : rawPoints) {
      List<Point> nicePoints = new ArrayList<Point>(rawRingPoints.size());
      for (List<Double> rawCoords : rawRingPoints) {
        nicePoints.add(new Point(rawCoords));
      }
      niceRingPoints.add(nicePoints);
    }
    return Polygon.createMultiRingPolygon(niceRingPoints);
  }

  /**
   * Generate a list of raw points for this Polygon.  Used internally to construct an API request.
   * @return A list of polygon rings, containing a list of points, which are lists of decimals
   */
  List<List<List<Double>>> toRawPoints() {
    List<List<List<Double>>> coords = new ArrayList<List<List<Double>>>(points.size());
    for (List<Point> ring : points) {
      List<List<Double>> ringCoords = new ArrayList<List<Double>>(ring.size());
      for (Point point : ring) {
        ringCoords.add(point.toCoordinates());
      }
      coords.add(ringCoords);
    }
    return coords;
  }

  /**
   * Get the list of points that make up this polygon.  If only a single ring was used to create
   * this Polygon, the resulting outer list will contain exactly one element.
   * @return  A list of 1 or more linear rings (lists of points) representing this polygon
   */
  public List<List<Point>> getPoints() {
    return points;
  }
}
