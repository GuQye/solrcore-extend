package com.dfire.valuesourceparser;

import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.docvalues.DoubleDocValues;
import org.apache.solr.search.function.distance.HaversineConstFunction;
import org.joda.time.DateTime;
import org.joda.time.Years;


public class ScoreTools {


    /***
     *
     * @param time 传入当前计算的日期毫秒数
     * @param maxYears 设置加权因子的最大年份上限
     * @return 该日期的一个动态加权评分
     */
    public static float getYearScore(long time,int maxYears){
        if(time==0){//没有日期的数据，不做加权操作
            return 1;
        }
        DateTime now=new DateTime();
        DateTime varTime=new DateTime(time);
        int year= Years.yearsBetween(varTime,now).getYears();
        float score=1;
        if(year>0&&year<=maxYears){
            return year*score;
        }else if(year>maxYears){ //超过上限者，统一按上限值乘以1.5倍算
            return score*maxYears*1.5f;
        }
        return score;

    }


    /***
     *
     * @param money 传入需要计算的注册资金
     * @param maxTimes 设置加权因子的最大倍数上限
     * @param money_base 注册资金基数
     * @return 该资金的一个动态评分值
     */
    public static float getMoneyScore(Double money,int maxTimes,int money_base){
        if(money==0){//没有注册资金的数据，不做加权操作
            return 1;
        }
        int mtimes=(int)(money/money_base);
        float score=1;
        if(mtimes>0&&mtimes<=maxTimes){
            return mtimes*score;
        }else if(mtimes>maxTimes){//超过上限者，统一按上限值乘以1.5倍算
            return score*maxTimes*1.5f;
        }
        return score;

    }

    /***
     *
     * @param lan,lon 传入需要计算的经纬度
     * @param lan_center,lon_center 基准经纬度
     * @return 该资金的一个动态评分值
     */
    public static double getDistanceScore(Double latVals,Double lonVals,Double latCenter,Double lonCenter){
        final double latCenterRad = latCenter * 0.017453292519943295D;
        final double lonCenterRad = lonCenter * 0.017453292519943295D;
        final double latCenterRad_cos = Math.cos(latCenter * 0.017453292519943295D);
        double latRad = latVals * 0.017453292519943295D;
        double lonRad = lonVals * 0.017453292519943295D;
        double diffX = latCenterRad - latRad;
        double diffY = lonCenterRad - lonRad;
        double hsinX = Math.sin(diffX * 0.5D);
        double hsinY = Math.sin(diffY * 0.5D);
        double h = hsinX * hsinX + latCenterRad_cos * Math.cos(latRad) * hsinY * hsinY;
        double score = 12742.0175428D * Math.atan2(Math.sqrt(h), Math.sqrt(1.0D - h));
        return score;
    }



}