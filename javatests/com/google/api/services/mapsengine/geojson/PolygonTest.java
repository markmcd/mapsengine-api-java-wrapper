package com.google.api.services.mapsengine.geojson;

import com.google.api.services.mapsengine.model.Feature;
import com.google.api.services.mapsengine.model.GeoJsonGeometry;
import com.google.api.services.mapsengine.model.GeoJsonPolygon;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Polygon tests */
@RunWith(JUnit4.class)
public class PolygonTest {
  private static final double ERROR_MARGIN = 1e-16;

  @Test
  public void testFeatureConstructor() throws Exception {
    // create a polygon feature that looks like an API response
    Feature feature = new Feature();
    feature.setType(Geometry.FEATURE_TYPE);

    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put("gx_id", "12345");
    feature.setProperties(properties);

    List<List<Double>> outerRing = new ArrayList<List<Double>>();
    outerRing.add(Arrays.asList(0.0, 0.0));
    outerRing.add(Arrays.asList(2.0, 0.0));
    outerRing.add(Arrays.asList(2.0, 2.0));
    outerRing.add(Arrays.asList(0.0, 2.0));
    outerRing.add(Arrays.asList(0.0, 0.0));

    List<List<Double>> innerRing = new ArrayList<List<Double>>();
    innerRing.add(Arrays.asList(0.5, 0.5));
    innerRing.add(Arrays.asList(1.5, 0.5));
    innerRing.add(Arrays.asList(1.5, 1.5));
    innerRing.add(Arrays.asList(0.5, 1.5));
    innerRing.add(Arrays.asList(0.5, 0.5));

    List<List<List<Double>>> polyRings = new ArrayList<List<List<Double>>>();
    polyRings.add(outerRing);
    polyRings.add(innerRing);

    GeoJsonPolygon geometry = new GeoJsonPolygon();
    geometry.setCoordinates(polyRings);
    feature.setGeometry(geometry);

    // do the conversion
    Polygon nicePoly = new Polygon(feature);

    // test the internals for success
    List<List<Point>> polyPoints = nicePoly.getPoints();
    Assert.assertEquals(2, polyPoints.size());  // number of rings

    List<Point> niceOuterRing = polyPoints.get(0);
    Assert.assertEquals(5, niceOuterRing.size());
    Assert.assertEquals(2, niceOuterRing.get(3).latitude, ERROR_MARGIN);
    Assert.assertEquals(0, niceOuterRing.get(3).longitude, ERROR_MARGIN);

    List<Point> niceInnerRing = polyPoints.get(1);
    Assert.assertEquals(5, niceInnerRing.size());
    Assert.assertEquals(0.5, niceInnerRing.get(1).latitude, ERROR_MARGIN);
    Assert.assertEquals(1.5, niceInnerRing.get(1).longitude, ERROR_MARGIN);
  }

  @Test
  public void testSimplePolygonAsFeature() throws Exception {
    // create a normal polygon
    Polygon polygon = Polygon.createSimplePolygon(Arrays.asList(
        new Point(0, 0),
        new Point(1, 0),
        new Point(1, 1),
        new Point(0, 1),
        new Point(0, 0)
    ));

    // turn it into a feature
    Feature feature = polygon.asFeature(new HashMap<String, Object>());
    GeoJsonGeometry geometry = feature.getGeometry();
    Assert.assertTrue(geometry instanceof GeoJsonPolygon);

    // check the internals
    List<List<List<Double>>> thePoints = ((GeoJsonPolygon) geometry).getCoordinates();
    Assert.assertEquals(1, thePoints.size()); // number of rings
    Assert.assertEquals(5, thePoints.get(0).size());  // number of points in 1st ring

    List<Double> secondPoint = thePoints.get(0).get(1);
    Assert.assertEquals(0, secondPoint.get(0), ERROR_MARGIN);
    Assert.assertEquals(1, secondPoint.get(1), ERROR_MARGIN);
  }

  @Test
  public void testMultiRingPolygonAsFeature() throws Exception {
    // create a multi-ring polygon
    Polygon twoRingPoly = Polygon.createMultiRingPolygon(Arrays.asList(Arrays.asList(
        new Point(0, 0),
        new Point(2, 0),
        new Point(2, 2),
        new Point(0, 2),
        new Point(0, 0)
    ), Arrays.asList(
        new Point(0.5, 0.5),
        new Point(1.5, 0.5),
        new Point(1.5, 1.5),
        new Point(0.5, 1.5),
        new Point(0.5, 0.5)
    )));

    // turn it into a feature
    Feature feature = twoRingPoly.asFeature(new HashMap<String, Object>());
    GeoJsonGeometry geometry = feature.getGeometry();
    Assert.assertTrue(geometry instanceof GeoJsonPolygon);

    // check the internals
    List<List<List<Double>>> thePoints = ((GeoJsonPolygon) geometry).getCoordinates();
    Assert.assertEquals(2, thePoints.size()); // number of rings
    Assert.assertEquals(5, thePoints.get(0).size());  // number of points in 1st ring
    Assert.assertEquals(5, thePoints.get(1).size());  // number of points in 2nd ring

    List<Double> ring1Point2 = thePoints.get(0).get(1);
    Assert.assertEquals(0, ring1Point2.get(0), ERROR_MARGIN);
    Assert.assertEquals(2, ring1Point2.get(1), ERROR_MARGIN);

    List<Double> ring2Point4 = thePoints.get(1).get(3);
    Assert.assertEquals(1.5, ring2Point4.get(0), ERROR_MARGIN);
    Assert.assertEquals(0.5, ring2Point4.get(1), ERROR_MARGIN);
  }

}
