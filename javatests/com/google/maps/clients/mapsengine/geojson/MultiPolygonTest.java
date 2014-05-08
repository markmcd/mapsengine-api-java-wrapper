package com.google.maps.clients.mapsengine.geojson;

import com.google.api.services.mapsengine.model.Feature;
import com.google.api.services.mapsengine.model.GeoJsonGeometry;
import com.google.api.services.mapsengine.model.GeoJsonMultiPolygon;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** MultiPolygon tests */
public class MultiPolygonTest {

  private static final double ERROR_MARGIN = 1e-16;

  @Test
  public void testFeatureConstructor() throws Exception {
    // create an API-like Feature
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

    List<List<List<Double>>> firstPoly = new ArrayList<List<List<Double>>>();
    firstPoly.add(outerRing);
    firstPoly.add(innerRing);

    List<List<Double>> polyRing = new ArrayList<List<Double>>();
    polyRing.add(Arrays.asList(0.0, 0.0));
    polyRing.add(Arrays.asList(1.0, 0.0));
    polyRing.add(Arrays.asList(1.0, 1.0));
    polyRing.add(Arrays.asList(0.0, 1.0));
    polyRing.add(Arrays.asList(0.0, 0.0));

    GeoJsonMultiPolygon geometry = new GeoJsonMultiPolygon();
    geometry.setCoordinates(Arrays.asList(firstPoly, Arrays.asList(polyRing)));
    feature.setGeometry(geometry);

    // do the conversion
    MultiPolygon multiPolygon = new MultiPolygon(feature);

    // check the internals
    Assert.assertEquals(2, multiPolygon.getPolygons().size());  // 2 polygons

    Polygon niceFirstPoly = multiPolygon.getPolygons().get(0);
    Assert.assertEquals(2, niceFirstPoly.getPoints().size()); // 2 rings in the 1st polygon

    Point firstPoly1stRing2ndPoint = niceFirstPoly.getPoints().get(0).get(1);
    Assert.assertEquals(0, firstPoly1stRing2ndPoint.latitude, ERROR_MARGIN);
    Assert.assertEquals(2, firstPoly1stRing2ndPoint.longitude, ERROR_MARGIN);

    Point firstPoly2ndRing4thPoint = niceFirstPoly.getPoints().get(1).get(3);
    Assert.assertEquals(1.5, firstPoly2ndRing4thPoint.latitude, ERROR_MARGIN);
    Assert.assertEquals(0.5, firstPoly2ndRing4thPoint.longitude, ERROR_MARGIN);

    Polygon niceSecondPoly = multiPolygon.getPolygons().get(1);
    Assert.assertEquals(1, niceSecondPoly.getPoints().size()); // 1 ring in the 2nd polygon

    Point secondPoly2ndPoint = niceSecondPoly.getPoints().get(0).get(1);
    Assert.assertEquals(0, secondPoly2ndPoint.latitude, ERROR_MARGIN);
    Assert.assertEquals(1, secondPoly2ndPoint.longitude, ERROR_MARGIN);
  }

  @Test
  public void testMultiPolygonAsFeature() throws Exception {
    // create a multi polygon using the previously used polygon templates above
    MultiPolygon multiPolygon = new MultiPolygon(Arrays.asList(
        Polygon.createSimplePolygon(Arrays.asList(
            new Point(0, 0),
            new Point(1, 0),
            new Point(1, 1),
            new Point(0, 1),
            new Point(0, 0)
        )),
        Polygon.createMultiRingPolygon(Arrays.asList(Arrays.asList(
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
        )))
    ));

    // turn it into a feature
    Feature feature = multiPolygon.asFeature(new HashMap<String, Object>());
    GeoJsonGeometry geometry = feature.getGeometry();
    Assert.assertTrue(geometry instanceof GeoJsonMultiPolygon);

    // check the internals
    List<List<List<List<Double>>>> thePoints = ((GeoJsonMultiPolygon) geometry).getCoordinates();
    Assert.assertEquals(2, thePoints.size()); // number of polygons

    List<List<List<Double>>> firstPoly = thePoints.get(0);
    Assert.assertEquals(1, firstPoly.size());  // rings in 1st polygon

    List<Double> firstPolySecondPoint = firstPoly.get(0).get(1);
    Assert.assertEquals(1, firstPolySecondPoint.get(1), ERROR_MARGIN); // lat
    Assert.assertEquals(0, firstPolySecondPoint.get(0), ERROR_MARGIN); // lng

    List<List<List<Double>>> secondPoly = thePoints.get(1);
    Assert.assertEquals(2, secondPoly.size());  // rings in 2nd polygon

    List<Double> secondPoly2ndRing4thPoint = secondPoly.get(1).get(3);
    Assert.assertEquals(0.5, secondPoly2ndRing4thPoint.get(1), ERROR_MARGIN);
    Assert.assertEquals(1.5, secondPoly2ndRing4thPoint.get(0), ERROR_MARGIN);
  }

}
