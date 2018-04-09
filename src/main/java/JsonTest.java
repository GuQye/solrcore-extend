import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by . on 2018/3/9.
 */
public class JsonTest{
    public static JSONArray httpGet(String url) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        String content = null;
        try {
            httpclient.addRequestInterceptor(new HttpRequestInterceptor() {
                public void process(
                        final HttpRequest request,
                        final HttpContext context) throws HttpException, IOException {
                    if (!request.containsHeader("Accept-Encoding")) {
                        request.addHeader("Accept", "application/json");
                    }
                }
            });
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                content = EntityUtils.toString(entity);
            }
        }catch (Exception e){
            e.printStackTrace();
            content = "ERROR";
        } finally{
            httpclient.getConnectionManager().shutdown();
        }
        return JSONArray.parseArray(content);
    }

    private static List<Range> ranges = null;

    private static String[] ss = {
            "80000000-95555555",
            "95555556-aaaaaaab",
            "aaaaaaac-c0000001",
            "c0000002-d5555557",
            "d5555558-eaaaaaad",
            "eaaaaaae-00000003",
            "00000004-15555559",
            "1555555a-2aaaaaaf",
            "2aaaaab0-40000005",
            "40000006-5555555b",
            "5555555c-6aaaaab1",
            "6aaaaab2-7fffffff",
    };


    public static void test(String[] values){
        ranges = new ArrayList<Range>();
        for(int i = 0;i<ss.length;i++){
            String value = values[i];
            int middle = value.indexOf('-');
            String minS = value.substring(0, middle);
            String maxS = value.substring(middle + 1);
            long min = Long.parseLong(minS, 16);
            long max = Long.parseLong(maxS, 16);
            ranges.add(new Range((int) min, (int) max, String.valueOf(i+1)));
        }

    }

    private static class Range {
        private String index;
        private int low;
        private int high;

        public Range(int low, int high, String index) {
            this.low = low;
            this.high = high;
            this.index = index;
        }

        public String getIndex() {
            return index;
        }

        public boolean include(String id) {
            int hash = murmurhash3_x86_32(id, 0, id.length(), 0); // according to the solr cloud router algorithm
            return hash >= low && hash <= high;
        }

        private int murmurhash3_x86_32(CharSequence data, int offset, int len, int seed) {

            final int c1 = 0xcc9e2d51;
            final int c2 = 0x1b873593;

            int h1 = seed;

            int pos = offset;
            int end = offset + len;
            int k1 = 0;
            int k2 = 0;
            int shift = 0;
            int bits = 0;
            int nBytes = 0;   // length in UTF8 bytes


            while (pos < end) {
                int code = data.charAt(pos++);
                if (code < 0x80) {
                    k2 = code;
                    bits = 8;

                    /***
                     // optimized ascii implementation (currently slower!!! code size?)
                     if (shift == 24) {
                     k1 = k1 | (code << 24);

                     k1 *= c1;
                     k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
                     k1 *= c2;

                     h1 ^= k1;
                     h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
                     h1 = h1*5+0xe6546b64;

                     shift = 0;
                     nBytes += 4;
                     k1 = 0;
                     } else {
                     k1 |= code << shift;
                     shift += 8;
                     }
                     continue;
                     ***/

                }
                else if (code < 0x800) {
                    k2 = (0xC0 | (code >> 6))
                            | ((0x80 | (code & 0x3F)) << 8);
                    bits = 16;
                }
                else if (code < 0xD800 || code > 0xDFFF || pos>=end) {
                    // we check for pos>=end to encode an unpaired surrogate as 3 bytes.
                    k2 = (0xE0 | (code >> 12))
                            | ((0x80 | ((code >> 6) & 0x3F)) << 8)
                            | ((0x80 | (code & 0x3F)) << 16);
                    bits = 24;
                } else {
                    // surrogate pair
                    // int utf32 = pos < end ? (int) data.charAt(pos++) : 0;
                    int utf32 = (int) data.charAt(pos++);
                    utf32 = ((code - 0xD7C0) << 10) + (utf32 & 0x3FF);
                    k2 = (0xff & (0xF0 | (utf32 >> 18)))
                            | ((0x80 | ((utf32 >> 12) & 0x3F))) << 8
                            | ((0x80 | ((utf32 >> 6) & 0x3F))) << 16
                            |  (0x80 | (utf32 & 0x3F)) << 24;
                    bits = 32;
                }


                k1 |= k2 << shift;

                // int used_bits = 32 - shift;  // how many bits of k2 were used in k1.
                // int unused_bits = bits - used_bits; //  (bits-(32-shift)) == bits+shift-32  == bits-newshift

                shift += bits;
                if (shift >= 32) {
                    // mix after we have a complete word

                    k1 *= c1;
                    k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
                    k1 *= c2;

                    h1 ^= k1;
                    h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
                    h1 = h1*5+0xe6546b64;

                    shift -= 32;
                    // unfortunately, java won't let you shift 32 bits off, so we need to check for 0
                    if (shift != 0) {
                        k1 = k2 >>> (bits-shift);   // bits used == bits - newshift
                    } else {
                        k1 = 0;
                    }
                    nBytes += 4;
                }

            } // inner

            // handle tail
            if (shift > 0) {
                nBytes += shift >> 3;
                k1 *= c1;
                k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
                k1 *= c2;
                h1 ^= k1;
            }

            // finalization
            h1 ^= nBytes;

            // fmix(h1);
            h1 ^= h1 >>> 16;
            h1 *= 0x85ebca6b;
            h1 ^= h1 >>> 13;
            h1 *= 0xc2b2ae35;
            h1 ^= h1 >>> 16;

            return h1;
        }
    }

    public static void main(String[] args) {
        test(ss);
        String[] nums = {
                /*----1----*/
                "00008653",
                "00011403",
                "00008653",
                "00008653",
                "00008653",
                "00011403",
                "00011403",
                "00008653",
                "00011403",
                "00014525",
                /*----2----*/
                "00008988",
                /*----8---*/
                "00013245",
                "00013245",
                "00013245",
                "00009233",
                "00009233",
                "00009233",
                "00013245",
                "00011074",
                "00013245",
                "00011074",
                "00013245",
                "00013245",
                "00011074",
                "00013245",
                "00011074",
                "00013245",
                "00011074",
                "00011074",
                "00011074",
                "00014204",
                /*----12---*/
                "00013868",
                "00013868",
                "00008323",
                "00008323",
                "00013868",
                "00008323",
                "00008323",
                "00013868",
                "00008323",
                "00013868",
                "00008323",
                "00013868",
                "00013868",
                "00008323",
                "00013868",
                "00008323",
                "00013868",
                "00008323",
                "00013868",
                "00008323"

        };
        for(String s:nums){
            for (Range range : ranges) {
                if (range.include(s)) {
                    System.out.println("--------------");
                    System.out.println(s);
                    System.out.println(range.getIndex());
                    System.out.println("--------------");
                }
            }
        }
    }
}