package com.dfire.valuesourceparser;


import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.DoubleDocValues;
import org.apache.lucene.queries.function.valuesource.VectorValueSource;
import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.lucene.spatial.vector.PointVectorStrategy;
import org.apache.solr.search.function.distance.HaversineConstFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public class FunctionValueSource extends ValueSource {
    final static Logger log= LoggerFactory.getLogger(FunctionValueSource.class);
    final static int FIRST_CIRCLE_SCORE = 100;
    final static int SECOND_CIRCLE_SCORE = 10;
    final static int THIRD_CIRCLE_SCORE = 1;

    private  List<ValueSource>  valueSources;
    private double latCenter;
    private double lonCenter;
    private MyValueParser.Params params;
    private final VectorValueSource p2;
    private final ValueSource latSource;
    private final ValueSource lonSource;
    private final double latCenterRad_cos;

    public FunctionValueSource(double lanCenter, double lonCenter , VectorValueSource other, MyValueParser.Params params, List<ValueSource> source) {
        this.latCenter = lanCenter;
        this.lonCenter = lonCenter;
        this.params = params;
        this.valueSources=source;
        this.p2 = other;
        this.latSource = this.p2.getSources().get(0);
        this.lonSource = this.p2.getSources().get(1);
        this.latCenterRad_cos = Math.cos(latCenter * 0.017453292519943295D);
    }

    @Override
    public FunctionValues getValues(Map map, final LeafReaderContext leafReaderContext) throws IOException {
        final FunctionValues latVals = this.latSource.getValues(map, leafReaderContext);
        final FunctionValues lonVals = this.lonSource.getValues(map, leafReaderContext);
        final NumericDocValues w = DocValues.getNumeric(leafReaderContext.reader(),params.weightField);
        final double latCenterRad = this.latCenter * 0.017453292519943295D;
        final double lonCenterRad = this.lonCenter * 0.017453292519943295D;
        final double latCenterRad_cos = this.latCenterRad_cos;
        return new DoubleDocValues(this) {
            public double doubleVal(int doc) {
                double latRad = latVals.doubleVal(doc) * 0.017453292519943295D;
                double lonRad = lonVals.doubleVal(doc) * 0.017453292519943295D;
                double weight = Float.intBitsToFloat((int)w.get(doc));
                double diffX = latCenterRad - latRad;
                double diffY = lonCenterRad - lonRad;
                double hsinX = Math.sin(diffX * 0.5D);
                double hsinY = Math.sin(diffY * 0.5D);
                double h = hsinX * hsinX + latCenterRad_cos * Math.cos(latRad) * hsinY * hsinY;
                double distance = 12742.0175428D * Math.atan2(Math.sqrt(h), Math.sqrt(1.0D - h));
                double x = (distance >= 0 && distance<=5)?FIRST_CIRCLE_SCORE:(distance>5 && distance<=10)?SECOND_CIRCLE_SCORE:THIRD_CIRCLE_SCORE;
                double finalScore = x + weight;
                log.info("the score details：latCenterRad "+ latCenterRad
                +" lonCenterRad " + lonCenterRad
                +" latRad " + latRad
                +" lonRad " + lonRad
                +" weight " + weight
                +" distance " + distance
                +" x " + x
                +" final score "+ finalScore);
                return finalScore;
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String description() {
        return null;
    }

    public static void main(String[] args) {
        float x = 1.037904089E9f;
        System.out.println(0.0<=5);

    }
}