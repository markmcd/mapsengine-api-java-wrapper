package com.google.api.services.mapsengine.geojson;

import com.google.api.services.mapsengine.model.Feature;
import com.google.api.services.mapsengine.model.GeoJsonGeometry;
import com.google.api.services.mapsengine.model.GeoJsonPoint;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Point tests */
@RunWith(JUnit4.class)
public class PointTest {

  private static final double ERROR_MARGIN = 1e-16;

  @Test
  public void testFeatureConstructor() throws Exception {
    // build a feature as we expect to see it through the client library
    Feature feature = new Feature();
    feature.setType(Geometry.FEATURE_TYPE);

    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put("gx_id", "12345");
    feature.setProperties(properties);

    GeoJsonPoint geometry = new GeoJsonPoint();
    geometry.setCoordinates(Arrays.asList(150.693972, -33.752039));
    feature.setGeometry(geometry);

    // create a typed Point using the wrapper library
    Point point = new Point(feature);

    // assert that all is well
    Assert.assertEquals(-33.752039, point.latitude, ERROR_MARGIN);
    Assert.assertEquals(150.693972, point.longitude, ERROR_MARGIN);
  }

  @Test
  public void testAsFeatureNoAltitude() throws Exception {
    Point p = new Point(40, -100);
    Feature feature = p.asFeature(new HashMap<String, Object>());

    GeoJsonGeometry geometry = feature.getGeometry();
    Assert.assertTrue(geometry instanceof GeoJsonPoint);

    List<Double> latLng = ((GeoJsonPoint) geometry).getCoordinates();
    Assert.assertEquals(-100, latLng.get(0), ERROR_MARGIN);  // long
    Assert.assertEquals(40, latLng.get(1), ERROR_MARGIN); // latitude
    Assert.assertEquals(2, latLng.size());  // no altitude
  }

  @Test
  public void testAsFeatureWithAltitude() throws Exception {
    Point p = new Point(40, -100, 1234);
    Feature feature = p.asFeature(new HashMap<String, Object>());

    GeoJsonGeometry geometry = feature.getGeometry();
    Assert.assertTrue(geometry instanceof GeoJsonPoint);

    List<Double> latLng = ((GeoJsonPoint) geometry).getCoordinates();
    Assert.assertEquals(-100, latLng.get(0), ERROR_MARGIN); // long
    Assert.assertEquals(40, latLng.get(1), ERROR_MARGIN); // latitude
    Assert.assertEquals(1234, latLng.get(2), ERROR_MARGIN); // altitude
  }

  @Test
  public void testPropertiesAreSet() throws Exception {
    Map<String, Object> inputProps = new HashMap<String, Object>();
    inputProps.put("column1", "value1");
    inputProps.put("column2", 600613);
    Feature actualFeature = new Point(0, 0).asFeature(inputProps);

    Map<String, Object> actualProps = actualFeature.getProperties();

    Assert.assertEquals("value1", actualProps.get("column1"));
    Assert.assertEquals(600613, actualProps.get("column2"));
  }
}
