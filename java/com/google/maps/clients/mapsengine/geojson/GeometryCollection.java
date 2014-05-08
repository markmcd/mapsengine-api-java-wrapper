package com.google.maps.clients.mapsengine.geojson;

import com.google.api.services.mapsengine.model.Feature;
import com.google.api.services.mapsengine.model.GeoJsonGeometry;
import com.google.api.services.mapsengine.model.GeoJsonGeometryCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A collection of {@link Geometry}s.
 */
public class GeometryCollection extends Geometry {

  private final List<Geometry> geometries;

  /**
   * Creates a GeometryCollection from the provided Geometries
   * @param geometries  The ordered list of geometries
   */
  public GeometryCollection(List<Geometry> geometries) {
    this.geometries = geometries;
  }

  /**
   * Converts the provided Feature into a typed GeometryCollection, discarding properties.  Throws
   * an exception if the feature is not a geometry collection.
   * @param feature  the Feature returned by the API
   * @throws IllegalArgumentException when feature is not a geometry collection
   */
  public GeometryCollection(Feature feature) {
    GeoJsonGeometry geometry = feature.getGeometry();
    if (!(geometry instanceof GeoJsonGeometryCollection)) {
      throw new IllegalArgumentException("Feature is not a GeometryCollection: " +
          geometry.getType());
    }

    GeoJsonGeometryCollection geometryCollection = (GeoJsonGeometryCollection) geometry;
    List<GeoJsonGeometry> jsonGeometries = geometryCollection.getGeometries();

    geometries = new ArrayList<Geometry>(jsonGeometries.size());
    for (GeoJsonGeometry jsonGeometry : jsonGeometries) {
      geometries.add(Geometry.fromGeoJson(jsonGeometry));
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

    List<GeoJsonGeometry> geoJsonGeometries = new ArrayList<GeoJsonGeometry>(geometries.size());
    for (Geometry geometry : geometries) {
      geoJsonGeometries.add(geometry.asFeature(properties).getGeometry());
    }

    GeoJsonGeometryCollection geoJsonCollection = new GeoJsonGeometryCollection();
    geoJsonCollection.setGeometries(geoJsonGeometries);

    Feature feature = new Feature();
    feature.setType(FEATURE_TYPE);
    feature.setGeometry(geoJsonCollection);
    feature.setProperties(properties);

    return feature;
  }

  /** Retrieves the list of geometries */
  public List<Geometry> getGeometries() {
    return geometries;
  }
}
