package com.google.api.services.mapsengine.geojson;

import com.google.api.services.mapsengine.model.Feature;
import com.google.api.services.mapsengine.model.GeoJsonGeometry;
import com.google.api.services.mapsengine.model.GeoJsonMultiPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A collection of points.
 */
public class MultiPoint extends Geometry {

  protected final List<Point> points;

  /**
   * Constructs a MultiPoint based on the collection of points provided.
   * @param points  The points representing this Geometry
   */
  public MultiPoint(List<Point> points) {
    this.points = points;
  }

  /**
   * Converts the provided Feature into a typed MultiPoint, discarding properties.  Throws an
   * exception if the feature is not a multipoint.
   * @param feature  the Feature returned by the API
   * @throws IllegalArgumentException when feature is not a multi-point
   */
  public MultiPoint(Feature feature) throws IllegalArgumentException {
    GeoJsonGeometry geometry = feature.getGeometry();
    if (!(geometry instanceof GeoJsonMultiPoint)) {
      throw new IllegalArgumentException("Feature is not a MultiPoint: " + geometry.getType());
    }

    GeoJsonMultiPoint multiPoint = (GeoJsonMultiPoint) geometry;
    List<List<Double>> rawPoints = multiPoint.getCoordinates();

    points = new ArrayList<Point>(rawPoints.size());
    for (List<Double> rawPoint : rawPoints) {
      points.add(new Point(rawPoint));
    }
  }


  public List<Point> getPoints() {
    return points;
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

    GeoJsonMultiPoint geometry = new GeoJsonMultiPoint();
    geometry.setCoordinates(coords);

    Feature feature = new Feature();
    feature.setType(FEATURE_TYPE);
    feature.setGeometry(geometry);
    feature.setProperties(properties);

    return feature;
  }
}
