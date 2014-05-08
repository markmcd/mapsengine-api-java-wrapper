package com.google.maps.clients.mapsengine.geojson;

import com.google.api.services.mapsengine.model.Feature;
import com.google.api.services.mapsengine.model.GeoJsonGeometry;
import com.google.api.services.mapsengine.model.GeoJsonMultiLineString;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** MultiLineString tests */
@RunWith(JUnit4.class)
public class MultiLineStringTest {

  private static final double ERROR_MARGIN = 1e-16;

  @Test
  public void testAsMultiLineStringWorks() throws Exception {
    // create a Feature that looks like an API response
    Feature feature = new Feature();
    feature.setType(Geometry.FEATURE_TYPE);

    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put("gx_id", "12345");
    feature.setProperties(properties);

    List<List<Double>> listString1 = new ArrayList<List<Double>>();
    listString1.add(Arrays.asList(1.0, 0.0));
    listString1.add(Arrays.asList(0.0, 1.0));

    List<List<Double>> listString2 = new ArrayList<List<Double>>();
    listString2.add(Arrays.asList(0.0, 0.0));
    listString2.add(Arrays.asList(10.0, 10.0));

    List<List<List<Double>>> lineStrings = new ArrayList<List<List<Double>>>();
    lineStrings.add(listString1);
    lineStrings.add(listString2);

    GeoJsonMultiLineString geometry = new GeoJsonMultiLineString();
    geometry.setCoordinates(lineStrings);
    feature.setGeometry(geometry);

    // do the conversion
    MultiLineString multiLineString = new MultiLineString(feature);

    // test the internals for success
    List<LineString> lines = multiLineString.getLines();
    Assert.assertEquals(2, lines.size());

    List<Point> firstLinePoints = lines.get(0).getPoints();
    Assert.assertEquals(0, firstLinePoints.get(0).latitude, ERROR_MARGIN);
    Assert.assertEquals(1, firstLinePoints.get(0).longitude, ERROR_MARGIN);

    List<Point> secondLinePoints = lines.get(1).getPoints();
    Assert.assertEquals(10, secondLinePoints.get(1).latitude, ERROR_MARGIN);
    Assert.assertEquals(10, secondLinePoints.get(1).longitude, ERROR_MARGIN);
  }


  @Test
  public void testAsFeature() throws Exception {
    // create the multi-line-string
    List<LineString> lines = new ArrayList<LineString>();
    lines.add(new LineString(Arrays.asList(
        new Point(0, 0),
        new Point(1, 1),
        new Point(0, 1)
    )));
    MultiLineString multiLineString = new MultiLineString(lines);

    // turn it into a feature
    Feature feature = multiLineString.asFeature(new HashMap<String, Object>());
    GeoJsonGeometry geometry = feature.getGeometry();
    Assert.assertTrue(geometry instanceof GeoJsonMultiLineString);

    // check the internals to ensure it's converted successfully
    List<List<List<Double>>> thePoints = ((GeoJsonMultiLineString) geometry).getCoordinates();
    Assert.assertEquals(1, thePoints.size());
    Assert.assertEquals(3, thePoints.get(0).size());

    List<Double> lastPoint = thePoints.get(0).get(2);
    Assert.assertEquals(1, lastPoint.get(0), ERROR_MARGIN);
    Assert.assertEquals(0, lastPoint.get(1), ERROR_MARGIN);
  }
}
