package com.google.maps.clients.mapsengine.geojson;

import com.google.api.services.mapsengine.model.Feature;
import com.google.api.services.mapsengine.model.GeoJsonGeometry;
import com.google.api.services.mapsengine.model.GeoJsonMultiPolygon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A collection of {@link Polygon}s.
 */
public class MultiPolygon extends Geometry {

  protected final List<Polygon> polygons;

  /**
   * Creates a MultiPolygon based on the polygons provided
   * @param polygons  The Polygons to use, in order
   */
  public MultiPolygon(List<Polygon> polygons) {
    this.polygons = polygons;
  }

  /**
   * Converts the provided Feature into a typed MultiPolygon, discarding properties.  Throws an
   * exception if the feature is not a multi-polygon.
   * @param feature  the Feature returned by the API
   * @throws IllegalArgumentException when feature is not a multi-polygon
   */
  public MultiPolygon(Feature feature) {
    GeoJsonGeometry geometry = feature.getGeometry();
    if (!(geometry instanceof  GeoJsonMultiPolygon)) {
      throw new IllegalArgumentException("Feature is not a MultiPolygon: " + geometry.getType());
    }

    GeoJsonMultiPolygon multiPolygon = (GeoJsonMultiPolygon) geometry;

    List<List<List<List<Double>>>> rawPoints = multiPolygon.getCoordinates();

    polygons = new ArrayList<Polygon>(rawPoints.size());

    for (List<List<List<Double>>> polygonPoints : rawPoints) {
      polygons.add(Polygon.fromRawPoints(polygonPoints));
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

    List<List<List<List<Double>>>> coords =
        new ArrayList<List<List<List<Double>>>> (polygons.size());
    for (Polygon polygon : polygons) {
      coords.add(polygon.toRawPoints());
    }

    GeoJsonMultiPolygon geometry = new GeoJsonMultiPolygon();
    geometry.setCoordinates(coords);

    Feature feature = new Feature();
    feature.setType(FEATURE_TYPE);
    feature.setProperties(properties);
    feature.setGeometry(geometry);

    return feature;
  }

  public List<Polygon> getPolygons() {
    return polygons;
  }
}
