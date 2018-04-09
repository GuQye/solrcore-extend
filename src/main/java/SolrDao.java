//import org.apache.solr.client.solrj.SolrQuery;
//import org.apache.solr.client.solrj.SolrServer;
//import org.apache.solr.client.solrj.SolrServerException;
//import org.apache.solr.client.solrj.impl.HttpSolrServer;
//import org.apache.solr.client.solrj.response.QueryResponse;
//import org.apache.solr.common.SolrDocument;
//import org.apache.solr.common.SolrDocumentList;
//import org.apache.solr.common.SolrInputDocument;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by . on 2018/2/26.
// */
//public class SolrDao {
//
//    public void addDefaultField() throws SolrServerException,IOException{
//        String url = "http://localhost:8983/solr";
//        SolrServer solr = new HttpSolrServer(url);
//        SolrInputDocument doc = new SolrInputDocument();
//        doc.addField("id","defaultId");
//        doc.addField("name","defaultName");
//        doc.addField("description","defaultDescription");
//        solr.add(doc);
//
//        solr.commit();
//
//    }
//
//
//    public void addDynamicField() throws SolrServerException,IOException{
//        String url = "http://localhost:8983/solr";
//        SolrServer solr = new HttpSolrServer(url);
//        SolrInputDocument doc = new SolrInputDocument();
//        doc.addField("id","adsd");
//        doc.addField("name_s","*_s");
//        doc.addField("desc_s","*_s");
//        solr.add(doc);
//        solr.commit();
//    }
//
//    public void addIndex(Product product) throws SolrServerException,IOException{
//        String url = "http://localhost:8983/solr";
//        SolrServer solr = new HttpSolrServer(url);
//        solr.addBean(product);
//        solr.commit();
//    }
//
//    // 更新索引
//    public void updateIndex(Product product) throws IOException, SolrServerException {
//        // 声明要连接solr服务器的地址
//        String url = "http://localhost:8983/solr";
//        SolrServer solr = new HttpSolrServer(url);
//        solr.addBean(product);
//        solr.commit();
//    }
//
//    // 删除索引
//    public void delIndex(String id) throws SolrServerException, IOException {
//        // 声明要连接solr服务器的地址
//        String url = "http://localhost:8983/solr";
//        SolrServer solr = new HttpSolrServer(url);
//        solr.deleteById(id);
//        // solr.deleteByQuery("id:*");
//        solr.commit();
//    }
//
//    public void findIndex() throws SolrServerException{
//        String url = "http://localhost:8983/solr";
//        SolrServer solr = new HttpSolrServer(url);
//
//        SolrQuery solrParams = new SolrQuery();
//        solrParams.setStart(0);
//        solrParams.setRows(10);
//
//
//        solrParams.setQuery("name:苹果 + description:全新4G");
//
//        solrParams.setHighlight(true);
//        solrParams.setHighlight(true);
//        solrParams.setHighlightSimplePre("<font color='red'>");
//        solrParams.setHighlightSimplePost("</font>");
//
//        // 设置高亮的字段
//        solrParams.setParam("hl.fl", "name,description");
//
//
//        QueryResponse queryResponse = solr.query(solrParams);
//
//        // (一)获取查询的结果集合
//        SolrDocumentList solrDocumentList = queryResponse.getResults();
//
//
//        Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
//        for(SolrDocument solrDocument:solrDocumentList){
//            System.out.println("=====" + solrDocument.toString());
//            Map<String, List<String>> fieldsMap = highlighting.get(solrDocument.get("id"));
//            List<String> highname = fieldsMap.get("name");
//            List<String> highdesc = fieldsMap.get("description");
//            System.out.println("highname======" + highname);
//            System.out.println("highdesc=====" + highdesc);
//        }
//
//
//        List<Product> products = queryResponse.getBeans(Product.class);
//
//        System.out.println(products + "+++++++++++");
//        for (Product product : products) {
//            System.out.println(product);
//        }
//
//
//
//
//
//
//
//    }
//}
