package com.dfire.fieldtype;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.schema.StrField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author 粟谷 sugu@2dfire.com
 * @date 2018年03月21日 上午11:01:51
 */
public class JsonParser extends StrField {

    private static final Log logger = LogFactory.getLog(JsonParser.class);
    private IndexSchema schema;

    @Override
    protected void init(IndexSchema schema, Map<String, String> args) {
        super.init(schema, args);
        this.schema = schema;
    }

    @Override
    public List<IndexableField> createFields(SchemaField field, Object value, float boost) {
        List<IndexableField> fields = new ArrayList<IndexableField>();
        String jsonStr = String.valueOf(value);
        //logger.info("the jsonStr is " + jsonStr);
        if(value == null || !StringUtils.startsWith(jsonStr,"{")){
            return Collections.emptyList();
        }
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        for(String key:jsonObject.keySet()){
            JSONArray jsonArray = jsonObject.getJSONArray(key);
            if(jsonArray.size()==0)
                continue;
            JSONObject lang_value= jsonArray.getJSONObject(0);
            String field_name = key + "_" + lang_value.get("lang");
            String field_value = lang_value.getString("value");
            if("null".equalsIgnoreCase(field_value))
                continue;
            fields.add(schema.getFieldOrNull(field_name).createField(field_value,boost));
        }
        if(field.stored()){
            fields.add(this.createField(field,jsonStr,1.0f));
        }
        return fields;
    }

}

