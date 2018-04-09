package com.dfire.valuesourceparser;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.ConstNumberSource;
import org.apache.lucene.queries.function.valuesource.DoubleConstValueSource;
import org.apache.lucene.queries.function.valuesource.MultiValueSource;
import org.apache.lucene.queries.function.valuesource.VectorValueSource;
import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.schema.AbstractSpatialFieldType;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;
import org.apache.solr.search.function.distance.GeoDistValueSourceParser;
import org.apache.solr.util.DistanceUnits;
import org.apache.solr.util.SpatialUtils;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.shape.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by qindongliang on 2016/5/11.
 * solrconfig.xml里面配置示例
 <valueSourceParser name="myfunc" class="com.easy.custom.functionquery.MyValueParser" >
 <lst name="params">
 <int name="firstCircle">5</int>
 <int name="secondCircle">10</int>
 <int name="thirdCircle">15</int>
 <str name="weightField">weight</str>
 </lst>
 </valueSourceParser>
 *
 *
 *
 */
public class MyValueParser extends ValueSourceParser {


    final static Logger log= LoggerFactory.getLogger(MyValueParser.class);

    //接受传过来的参数
    private int firstCircle;
    private int secondCircle;
    private int thirdCircle;
    private String weightField;
    private Params params;

    // handle configuration parameters
    // passed through solrconfig.xml
    public void init(NamedList args) {
        //得到一个映射之后，转成NamedList便于操作
        firstCircle=(Integer) ((NamedList)args.get("params")).get("firstCircle");
        secondCircle=(Integer) ((NamedList)args.get("params")).get("secondCircle");
        thirdCircle=(Integer) ((NamedList)args.get("params")).get("thirdCircle");
        weightField=(String) ((NamedList)args.get("params")).get("weightField");
        params = new Params(firstCircle,secondCircle,thirdCircle,weightField);
        log.info("init params：  firstCircle : " + firstCircle
                +" secondCircle : " + secondCircle
                +" thirdCircle : " + thirdCircle
                +" weightField : " + weightField);
    }

    @Override
    public ValueSource parse(FunctionQParser fq) throws SyntaxError {
        Point mv1 = null;
        MultiValueSource mv2 = null;
        mv1 = parsePoint(fq);
        mv2 = parseSfield(fq);
        return new FunctionValueSource(mv1.getY(),mv1.getX(),(VectorValueSource)mv2,params,fq.parseValueSourceList());
    }
    private Point parsePoint(FunctionQParser fp) throws SyntaxError {
        String ptStr = fp.getParam("pt");
        log.info("ptStr is "+ ptStr);
        if(ptStr == null) {
            return null;
        } else {
            Point point = SpatialUtils.parsePointSolrException(ptStr, SpatialContext.GEO);
            return point;
        }
    }

    private MultiValueSource parseSfield(FunctionQParser fp) throws SyntaxError {
        String sfield = fp.getParam("sfield");
        log.info("sfield is "+ sfield);
        if(sfield == null) {
            return null;
        } else {
            SchemaField sf = fp.getReq().getSchema().getField(sfield);
            FieldType type = sf.getType();
            ValueSource vs = type.getValueSource(sf, fp);
            if(vs instanceof MultiValueSource) {
                return (MultiValueSource)vs;
            } else {
                throw new SyntaxError("Spatial field must implement MultiValueSource or extend AbstractSpatialFieldType:" + sf);
            }
        }
    }

    protected class Params{
        protected int firstCircle;
        protected int secondCircle;
        protected int thirdCircle;
        protected String weightField;
        public Params( int firstCircle,int secondCircle,int thirdCircle,String weightField) {
            this.firstCircle = firstCircle;
            this.secondCircle = secondCircle;
            this.thirdCircle = thirdCircle;
            this.weightField = weightField;
        }
    }


    public static void main(String[] args) {
        String field = "name*";
        Pattern dynamicField = Pattern.compile(field);
        Matcher matcher = dynamicField.matcher("na1me");
        if (matcher.find()) {
            System.out.println("true");
        }
        else{
            System.out.println("false");
        }
        System.out.println( false & true);
    }
}