package com.spatialyse.jts2geojson;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.*;
import com.spatialyse.geojson.Feature;

public class GeoJSONWriter {

    final static GeoJSONReader reader = new GeoJSONReader();

    public com.spatialyse.geojson.Geometry write(Geometry geometry) {
        Class<? extends Geometry> c = geometry.getClass();
        if (c.equals(Point.class))
            return convert((Point) geometry);
        else if (c.equals(LineString.class))
            return convert((LineString) geometry);
        else if (c.equals(LinearRing.class))
            return convert((LinearRing) geometry);
        else if (c.equals(Polygon.class))
            return convert((Polygon) geometry);
        else if (c.equals(MultiPoint.class))
            return convert((MultiPoint) geometry);
        else if (c.equals(MultiLineString.class))
            return convert((MultiLineString) geometry);
        else if (c.equals(MultiPolygon.class))
            return convert((MultiPolygon) geometry);
        else if (c.equals(GeometryCollection.class))
            return convert((GeometryCollection) geometry);
        else
            throw new UnsupportedOperationException();
    }

    public com.spatialyse.geojson.FeatureCollection write(List<Feature> features) {
        var size = features.size();
        var featuresJson = new com.spatialyse.geojson.Feature[size];
        for (var i = 0; i < size; i++)
            featuresJson[i] = features.get(i);
        return new com.spatialyse.geojson.FeatureCollection(featuresJson);
    }

    com.spatialyse.geojson.Point convert(Point point) {
        return new com.spatialyse.geojson.Point(convert(point.getCoordinate()));
    }

    com.spatialyse.geojson.MultiPoint convert(MultiPoint multiPoint) {
        return new com.spatialyse.geojson.MultiPoint(
                convert(multiPoint.getCoordinates()));
    }

    com.spatialyse.geojson.LineString convert(LineString lineString) {
        return new com.spatialyse.geojson.LineString(
                convert(lineString.getCoordinates()));
    }

    com.spatialyse.geojson.LineString convert(LinearRing ringString) {
        return new com.spatialyse.geojson.LineString(
                convert(ringString.getCoordinates()));
    }

    com.spatialyse.geojson.MultiLineString convert(MultiLineString multiLineString) {
        var size = multiLineString.getNumGeometries();
        List<double[][]> lineStrings = new ArrayList<double[][]>(size);
        for (int i = 0; i < size; i++)
            lineStrings.add(convert(multiLineString.getGeometryN(i).getCoordinates()));
        return new com.spatialyse.geojson.MultiLineString(lineStrings);
    }

    com.spatialyse.geojson.Polygon convert(Polygon polygon) {
        var size = polygon.getNumInteriorRing() + 1;
        List<double[][]> rings = new ArrayList<double[][]>(size);
        rings.add(convert(polygon.getExteriorRing().getCoordinates()));
        for (int i = 0; i < size - 1; i++)
            rings.add(convert(polygon.getInteriorRingN(i).getCoordinates()));
        return new com.spatialyse.geojson.Polygon(rings);
    }

    com.spatialyse.geojson.MultiPolygon convert(MultiPolygon multiPolygon) {
        var size = multiPolygon.getNumGeometries();
        List<List<double[][]>> polygons = new ArrayList<List<double[][]>>(size);
        for (int i = 0; i < size; i++)
            polygons.add(convert((Polygon) multiPolygon.getGeometryN(i)).getCoordinates());
        return new com.spatialyse.geojson.MultiPolygon(polygons);
    }

    com.spatialyse.geojson.GeometryCollection convert(GeometryCollection gc) {
        var size = gc.getNumGeometries();
        var geometries = new com.spatialyse.geojson.Geometry[size];
        for (int i = 0; i < size; i++)
            geometries[i] = write((Geometry) gc.getGeometryN(i));
        return new com.spatialyse.geojson.GeometryCollection(geometries);
    }

    double[] convert(Coordinate coordinate) {
        if (Double.isNaN(coordinate.getZ()))
            return new double[] { coordinate.x, coordinate.y };
        else
            return new double[] { coordinate.x, coordinate.y, coordinate.getZ() };
    }

    double[][] convert(Coordinate[] coordinates) {
        var array = new double[coordinates.length][];
        for (int i = 0; i < coordinates.length; i++)
            array[i] = convert(coordinates[i]);
        return array;
    }
}
