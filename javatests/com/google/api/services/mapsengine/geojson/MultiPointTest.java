package com.google.api.services.mapsengine.geojson;

import com.google.api.services.mapsengine.model.Feature;
import com.google.api.services.mapsengine.model.GeoJsonGeometry;
import com.google.api.services.mapsengine.model.GeoJsonMultiPoint;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** MultiPoint tests */
@RunWith(JUnit4.class)
public class MultiPointTest {

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
    coords.add(Arrays.asList(100.0, 0.0));
    coords.add(Arrays.asList(0.0, 50.0));

    GeoJsonMultiPoint geometry = new GeoJsonMultiPoint();
    geometry.setCoordinates(coords);
    feature.setGeometry(geometry);

    // do the conversion
    MultiPoint multi = new MultiPoint(feature);
    List<Point> points = multi.getPoints();

    // ensure conversion was successful
    Assert.assertEquals(0, points.get(0).latitude, ERROR_MARGIN);
    Assert.assertEquals(100, points.get(0).longitude, ERROR_MARGIN);
    Assert.assertEquals(50, points.get(1).latitude, ERROR_MARGIN);
    Assert.assertEquals(0, points.get(1).longitude, ERROR_MARGIN);
  }

  @Test
  public void testAsFeature() throws Exception {
    List<Point> points = new ArrayList<Point>();
    points.add(new Point(0, 0));
    points.add(new Point(40, -100));
    MultiPoint multiPoint = new MultiPoint(points);

    Feature feature = multiPoint.asFeature(new HashMap<String, Object>());

    GeoJsonGeometry geometry = feature.getGeometry();
    Assert.assertTrue(geometry instanceof GeoJsonMultiPoint);

    List<List<Double>> allThePoints = ((GeoJsonMultiPoint) geometry).getCoordinates();
    Assert.assertEquals(2, allThePoints.size());

    List<Double> firstPoint = allThePoints.get(0);
    Assert.assertEquals(0, firstPoint.get(0), ERROR_MARGIN);
    Assert.assertEquals(0, firstPoint.get(1), ERROR_MARGIN);

    List<Double> secondPoint = allThePoints.get(1);
    Assert.assertEquals(-100, secondPoint.get(0), ERROR_MARGIN);
    Assert.assertEquals(40, secondPoint.get(1), ERROR_MARGIN);
  }
}
