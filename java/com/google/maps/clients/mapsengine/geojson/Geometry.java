package com.google.maps.clients.mapsengine.geojson;

import com.google.api.services.mapsengine.model.Feature;
import com.google.api.services.mapsengine.model.GeoJsonGeometry;
import com.google.api.services.mapsengine.model.GeoJsonGeometryCollection;
import com.google.api.services.mapsengine.model.GeoJsonLineString;
import com.google.api.services.mapsengine.model.GeoJsonMultiLineString;
import com.google.api.services.mapsengine.model.GeoJsonMultiPoint;
import com.google.api.services.mapsengine.model.GeoJsonMultiPolygon;
import com.google.api.services.mapsengine.model.GeoJsonPoint;
import com.google.api.services.mapsengine.model.GeoJsonPolygon;

import java.util.Map;

/**
 * An alternative to the API-generated {@link GeoJsonGeometry} class that provides native
 * construction and manipulation without needing to manipulate multi-dimensional lists of
 * decimals.
 */
public abstract class Geometry {

  public static final String FEATURE_TYPE = "Feature";

  /**
   * Returns a Feature that can be used by the Maps Engine API.
   * @param properties  The properties to attach to the feature
   * @return an API-compatible Feature object
   */
  public abstract Feature asFeature(Map<String, Object> properties);

  /**
   * Factory method for creating geometries from known GeoJsonGeometries.
   * @param geoJsonGeometry  The API-generated GeoJSON geometry
   * @return One of the supported geometries from the Maps Engine API
   * @see <a href="
   * https://developers.google.com/maps-engine/documentation/reference/v1/tables/features#resource">
   *    Supported geometries</a>
   */
  public static Geometry fromGeoJson(GeoJsonGeometry geoJsonGeometry) {
    Feature shellFeature = new Feature();
    shellFeature.setGeometry(geoJsonGeometry);

    if (geoJsonGeometry instanceof GeoJsonPoint) {
      return new Point(shellFeature);
    } else if (geoJsonGeometry instanceof GeoJsonMultiPoint) {
      return new MultiPoint(shellFeature);
    } else if (geoJsonGeometry instanceof GeoJsonLineString) {
      return new LineString(shellFeature);
    } else if (geoJsonGeometry instanceof GeoJsonMultiLineString) {
      return new MultiLineString(shellFeature);
    } else if (geoJsonGeometry instanceof GeoJsonPolygon) {
      return new Polygon(shellFeature);
    } else if (geoJsonGeometry instanceof GeoJsonMultiPolygon) {
      return new MultiPolygon(shellFeature);
    } else if (geoJsonGeometry instanceof GeoJsonGeometryCollection) {
      return new GeometryCollection(shellFeature);
    } else {
      throw new IllegalArgumentException("Geometry type is not known: " +
          geoJsonGeometry.getType());
    }
  }
}
