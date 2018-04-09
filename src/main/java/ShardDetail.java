import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.hive.ql.exec.UDF;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by . on 2018/3/9.
 */
public class ShardDetail extends UDF {
    private static final Pattern SHARE_PATTERN = Pattern.compile("shard(\\d+)");

    public String evaluate(final String shardValue, final String collection, final String runtime) {
        return getShardIndex(shardValue,collection,runtime);
    }

    private static String getShardIndex(final String shardValue, final String collection, final String runtime){
        String url = "http://127.0.0.1:8080/config/config.ajax?action=collection_detail_action&event_submit_do_get_shard_index=y&"
        +"runtime="+runtime
        +"&collection="+collection
        +"&resulthandler=advance_query_result";
        JSONArray response = JsonTest.httpGet(url);
        List<Range> ranges = new ArrayList<Range>();
        for(Object shard:response){
            Map shard_entry = (Map) shard;
            Matcher matcher = SHARE_PATTERN.matcher((String) shard_entry.get("key"));
            if(matcher.matches()){
                String value = (String) shard_entry.get("value");
                String[] xs = value.split("-");
                Range range = new Range(xs[1],xs[0],matcher.group(1));
                ranges.add(range);
            }
        }
        String index = "";
        for(Range range:ranges){
            if(inRange(shardValue,range)){
                index = range.getIndex();
            }
        }
        if("".equals(index)){
            throw new IllegalStateException("wrong parameter: "+shardValue
                    + " does not match any range");
        }
        System.out.println(index);
        return index;
    }

    private static boolean inRange(String shardValue, Range range) {
        return shardValue.compareTo(range.getLow())>=0 && shardValue.compareTo(range.getHigh())<=0;
    }


    private static class Range{
        private String index;
        private String low;
        private String high;

        public Range(String low,String high,String index){
            this.low = low;
            this.high = high;
            this.index = index;
        }
        public String getLow() {
            return low;
        }

        public String getHigh() {
            return high;
        }

        public String getIndex() {
            return index;
        }
    }

    public static void main(String[] args) {
          getShardIndex("80000000","search4totalpay","daily");
//        Matcher matcher = SHARE_PATTERN.matcher("shard2");
//        if(matcher.matches()){
//            System.out.println(matcher.group(0));
//        }
//        System.out.println(getShardIndex("9FFFF","b","c"));
//        System.out.println(inRange("9BADC","8FFFF","9FFFF"));
//        String url = "http://127.0.0.1:8080/config/config.ajax?action=collection_detail_action&event_submit_do_get_shard_index=y&runtime=daily&collection=search4totalpay&resulthandler=advance_query_result";
//        JSONArray response = JsonTest.httpGet(url);
//        Map entry = (Map) response.get(0);
//        System.out.println(entry.get("key"));
//        System.out.println("80000000".compareTo("80000000"));
//        System.out.println("80000000".compareTo("7fffffff"));

    }
}

