package com.google.api.services.mapsengine.geojson;

import com.google.api.services.mapsengine.model.Feature;
import com.google.api.services.mapsengine.model.GeoJsonGeometry;
import com.google.api.services.mapsengine.model.GeoJsonGeometryCollection;
import com.google.api.services.mapsengine.model.GeoJsonLineString;
import com.google.api.services.mapsengine.model.GeoJsonPoint;
import com.google.api.services.mapsengine.model.GeoJsonPolygon;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** GeometryCollection tests */
public class GeometryCollectionTest {

  private static final double ERROR_MARGIN = 1e-16;

  @Test
  public void testFeatureConstructor() throws Exception {
    // create an API-like Feature
    Feature feature = new Feature();
    feature.setType(Geometry.FEATURE_TYPE);

    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put("gx_id", "12345");
    feature.setProperties(properties);

    GeoJsonPoint pointGeometry = new GeoJsonPoint();
    pointGeometry.setCoordinates(Arrays.asList(150.693972, -33.752039));

    List<List<Double>> polyRing = new ArrayList<List<Double>>();
    polyRing.add(Arrays.asList(0.0, 0.0));
    polyRing.add(Arrays.asList(2.0, 0.0));
    polyRing.add(Arrays.asList(2.0, 2.0));
    polyRing.add(Arrays.asList(0.0, 2.0));
    polyRing.add(Arrays.asList(0.0, 0.0));

    GeoJsonPolygon polygonGeometry = new GeoJsonPolygon();
    polygonGeometry.setCoordinates(Arrays.asList(polyRing));

    GeoJsonGeometryCollection geometryCollection = new GeoJsonGeometryCollection();
    geometryCollection.setGeometries(Arrays.asList(pointGeometry, polygonGeometry));

    feature.setGeometry(geometryCollection);

    // do the conversion
    GeometryCollection niceGC = new GeometryCollection(feature);

    // test the internals for success
    Assert.assertEquals(2, niceGC.getGeometries().size());  // got both geometries
    Point nicePoint = (Point) niceGC.getGeometries().get(0);
    Assert.assertEquals(-33.752039, nicePoint.latitude, ERROR_MARGIN);
    Assert.assertEquals(150.693972, nicePoint.longitude, ERROR_MARGIN);
    Polygon nicePoly = (Polygon) niceGC.getGeometries().get(1);
    Point fourthPointInOuterRing = nicePoly.getPoints().get(0).get(3);
    Assert.assertEquals(2, fourthPointInOuterRing.latitude, ERROR_MARGIN);
    Assert.assertEquals(0, fourthPointInOuterRing.longitude, ERROR_MARGIN);
  }

  @Test
  public void testGeometryCollectionAsFeature() throws Exception {
    // create a Geometry Collection
    GeometryCollection geoms = new GeometryCollection(Arrays.asList(
        new Point(-45, 100),
        new LineString(Arrays.asList(
            new Point(0, 0),
            new Point(1, 1),
            new Point(0, 1)
        ))
    ));

    // turn it into a feature
    Feature feature = geoms.asFeature(new HashMap<String, Object>());
    GeoJsonGeometry geometry = feature.getGeometry();
    Assert.assertTrue(geometry instanceof GeoJsonGeometryCollection);

    // check the internals
    List<GeoJsonGeometry> rawGeoms = ((GeoJsonGeometryCollection) geometry).getGeometries();
    Assert.assertEquals(2, rawGeoms.size());

    GeoJsonPoint firstPoly = ((GeoJsonPoint) rawGeoms.get(0));
    Assert.assertEquals(-45, firstPoly.getCoordinates().get(1), ERROR_MARGIN);
    Assert.assertEquals(100, firstPoly.getCoordinates().get(0), ERROR_MARGIN);

    GeoJsonLineString secondPoly = (GeoJsonLineString) rawGeoms.get(1);
    Assert.assertEquals(3, secondPoly.getCoordinates().size());  // number of points
    // check the lat/long of point #3 in the linestring
    Assert.assertEquals(0, secondPoly.getCoordinates().get(2).get(1), ERROR_MARGIN);
    Assert.assertEquals(1, secondPoly.getCoordinates().get(2).get(0), ERROR_MARGIN);
  }}
