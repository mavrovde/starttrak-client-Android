package com.startrack.android.startrack_android.model;

import java.util.List;
import java.util.Map;

/**
 * Created by vrogovskiy on 2/14/16.
 */
public class Dictionary {
    private String dictionaryName;
    private String fieldName;
    private List<Map<Integer,String>> array;

    public String getDictionaryName() {
        return dictionaryName;
    }

    public void setDictionaryName(String dictionaryName) {
        this.dictionaryName = dictionaryName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public List<Map<Integer, String>> getArray() {
        return array;
    }

    public void setArray(List<Map<Integer, String>> array) {
        this.array = array;
    }
}
