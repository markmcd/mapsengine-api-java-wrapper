package com.google.api.services.mapsengine.geojson;

import com.google.api.services.mapsengine.model.Feature;
import com.google.api.services.mapsengine.model.GeoJsonGeometry;
import com.google.api.services.mapsengine.model.GeoJsonLineString;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** LineString tests */
@RunWith(JUnit4.class)
public class LineStringTest {
  private static final double ERROR_MARGIN = 1e-16;

  @Test
  public void testFeatureConstructor() throws Exception {
    // create a Feature that looks like an API response
    Feature feature = new Feature();
    feature.setType(Geometry.FEATURE_TYPE);

    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put("gx_id", "12345");
    feature.setProperties(properties);

    List<List<Double>> coords = new ArrayList<List<Double>>();
    coords.add(Arrays.asList(1.0, 0.0));
    coords.add(Arrays.asList(0.0, 1.0));

    GeoJsonLineString geometry = new GeoJsonLineString();
    geometry.setCoordinates(coords);
    feature.setGeometry(geometry);

    // do the conversion
    LineString lineString = new LineString(feature);
    List<Point> points = lineString.getPoints();

    // ensure success
    Assert.assertEquals(0, points.get(0).latitude, ERROR_MARGIN);
    Assert.assertEquals(1, points.get(0).longitude, ERROR_MARGIN);
    Assert.assertEquals(1, points.get(1).latitude, ERROR_MARGIN);
    Assert.assertEquals(0, points.get(1).longitude, ERROR_MARGIN);
  }


  @Test
  public void testAsFeature() {
    List<Point> points = new ArrayList<Point>();
    points.add(new Point(0, 0));
    points.add(new Point(0, 1));
    points.add(new Point(1, 0));
    points.add(new Point(1, 1));
    LineString lineString = new LineString(points);

    Feature feature = lineString.asFeature(new HashMap<String, Object>());

    GeoJsonGeometry geometry = feature.getGeometry();
    Assert.assertTrue(geometry instanceof GeoJsonLineString);

    List<List<Double>> thePoints = ((GeoJsonLineString) geometry).getCoordinates();
    Assert.assertEquals(4, thePoints.size());

    List<Double> firstPoint = thePoints.get(0);
    Assert.assertEquals(0, firstPoint.get(0), ERROR_MARGIN);
    Assert.assertEquals(0, firstPoint.get(1), ERROR_MARGIN);

    List<Double> lastPoint = thePoints.get(3);
    Assert.assertEquals(1, lastPoint.get(0), ERROR_MARGIN);
    Assert.assertEquals(1, lastPoint.get(1), ERROR_MARGIN);
  }

}
