package com.dfire.fieldtype;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.schema.BoolField;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by . on 2018/3/30.
 */
public class IsNullField extends BoolField {
    @Override
    public String toInternal(String val) {
        return StringUtils.isNotEmpty(val) ? "T" : "F";
    }

    public static void main(String[] args) {
        LinkedList<String> arr = new LinkedList<String>();
        arr.add("1");
        arr.add("3");
        arr.add("2");
        Collections.sort(arr);
        System.out.println(arr);
    }
}
