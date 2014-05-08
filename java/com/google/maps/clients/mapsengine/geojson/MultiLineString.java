package com.google.maps.clients.mapsengine.geojson;

import com.google.api.services.mapsengine.model.Feature;
import com.google.api.services.mapsengine.model.GeoJsonGeometry;
import com.google.api.services.mapsengine.model.GeoJsonMultiLineString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A collection of line strings.
 */
public class MultiLineString extends Geometry {
  protected final List<LineString> lineStrings;

  /**
   * Constructs a MultiLineString based on the lines provided.
   * @param lines  the LineStrings to use, in order
   */
  public MultiLineString(List<LineString> lines) {
    lineStrings = lines;
  }

  /**
   * Converts the provided Feature into a typed MultiLineString, discarding properties.  Throws an
   * exception if the feature is not a multi line-string.
   * @param feature  the Feature returned by the API
   * @throws IllegalArgumentException when feature is not a multi line-string
   */
  public MultiLineString(Feature feature) throws IllegalArgumentException {
    GeoJsonGeometry geometry = feature.getGeometry();
    if (!(geometry instanceof GeoJsonMultiLineString)) {
      throw new IllegalArgumentException("Feature is not a MultiLineString: " + geometry.getType());
    }

    GeoJsonMultiLineString multiLineString = (GeoJsonMultiLineString) geometry;
    List<List<List<Double>>> rawPoints = multiLineString.getCoordinates();

    lineStrings = new ArrayList<LineString>();
    for (List<List<Double>> rawLineStringPoints : rawPoints) {
      lineStrings.add(LineString.fromRawPoints(rawLineStringPoints));
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

    List<List<List<Double>>> coords =
        new ArrayList<List<List<Double>>>(lineStrings.size());
    for (LineString line : lineStrings) {
      coords.add(line.toCoordinates());
    }

    GeoJsonMultiLineString geometry = new GeoJsonMultiLineString();
    geometry.setCoordinates(coords);

    Feature feature = new Feature();
    feature.setType(FEATURE_TYPE);
    feature.setProperties(properties);
    feature.setGeometry(geometry);

    return feature;
  }

  public List<LineString> getLines() {
    return lineStrings;
  }
}
