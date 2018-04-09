import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;

import org.apache.solr.util.SpatialUtils;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.shape.Point;


/**
 * Created by . on 2018/3/7.
 */
public class solrJ {

    //solr 服务器地址
    public static final String solrServerUrl = "http://localhost:8080/solr/";
    //solrhome下的core
    public static final String solrCroeHome = "collection1";
    //待索引、查询字段
//    public static String[] docs = {"二维火",
//            "b古翠路",
//            "翠苑四区",
//            "a城西银泰",
//            "1三益里",
//            "?桔子里"};

    public static String[] docs = {"APELD",
            "apaca",
            "cdasjkkdjas",
            "dcndke",
            "ejvnsaj",
            "fcndjaeo"};
    public static String[] location = {
      "30.3023632444,120.1401867412",
            "30.2885604469,120.1244706261",
            "30.2923806016,120.1326661920",
            "30.3056712646,120.1142381388",
            "30.2504895589,120.1836310942",
            "30.3367825954,120.0753467032"
    };

    private static String jsonStr = "{\"name\":[{\"lang\":\"zh_TW\",\"value\":\"2222\"}],\"shop_category_name\":[{\"lang\":\"zh_TW\",\"value\":\"测试套餐\"}],\"unit_name\":[{\"lang\":\"zh_TW\",\"value\":\"份\"}],\"account_unit_name\":[{\"lang\":\"zh_TW\",\"value\":\"份\"}],\"all_child_spec\":[],\"all_child_make\":[]}";
    //private static String jsonStr = "";
    public static void main(String[] args) throws IOException, SolrServerException {
        SolrClient client = getSolrClient();
        //deleteById(Lists.newArrayList("8"),client);
        //addSolrInput(client);
        //clientQuery(client);
        //testQuery();
        //myfunc(client);
        //String js = JSONObject.toJSONString("acb");
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", 13);
        doc.addField("isNull","1");
        //doc.addField("name","hello world") ;

        client.add(doc);
        client.commit();
    }

    public static void myfunc(SolrClient client){
        SolrQuery query = new SolrQuery();
        String frompt = "30.3367825954,120.0753467032";
        query.setParam("pt",frompt);
        query.setParam("sfield","coordinate");
        query.setQuery("_query_:\"name:*\" _val_:\"{!func}myfunc()\"");
        try {
            QueryResponse queryResponse = client.query(query);
            System.out.println(queryResponse.getResults());
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void testQuery() {
        String ptStr = "10.21,120.33";
        Point point = SpatialUtils.parsePointSolrException(ptStr, SpatialContext.GEO);
        System.out.println(point.getX());
    }

    private static void clientQuery(SolrClient client) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("content_test: content_test_3");
        solrQuery.set("pt","10.21,120.33");
        try {
            QueryResponse queryResponse = client.query(solrQuery);
            System.out.println(queryResponse.getResults());
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addSolrInput(SolrClient client){
        List<SolrInputDocument> solrDocs = new ArrayList<SolrInputDocument>();
        for (int i = 0;i<docs.length;i++) {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", i+1);
            doc.addField("weight", Math.random());
            doc.addField("title", docs[i]);
            doc.addField("store",location[i]);
            solrDocs.add(doc);
        }
        try {
            client.add(solrDocs);
            client.commit();
        } catch (SolrServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void deleteById(List<String> ids,SolrClient client){
        for(String id:ids){
            try {
                client.deleteById(id);
                client.commit();
            } catch (SolrServerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static SolrClient getSolrClient(){
        return new HttpSolrClient(solrServerUrl+"/"+solrCroeHome);
    }
}
