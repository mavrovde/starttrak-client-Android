package com.startrack.android.startrack_android.model;


import com.startrack.android.startrack_android.app.StarTrackApplication;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Created by vrogovskiy on 2/14/16.
 */
public class SelectableProfileProperty extends BaseProfileProperty {

    public static void writeDummyData(){
        Map<Integer,String> companySizeDictinary = new HashMap<Integer,String>() {
            {
                put(0, "None");
                put(1, "10 - 100");
                put(2, "100 - 1000");
            }
        };
        StarTrackApplication.dictionaries.put(4,companySizeDictinary);
        Map<Integer,String> titleDictinary = new HashMap<Integer,String>() {
            {
                put(0, "None");
                put(1, "Mr");
                put(2, "Ms");
            }
        };
        StarTrackApplication.dictionaries.put(5,titleDictinary);
        Map<Integer,String> seniorityDictinary = new HashMap<Integer,String>() {
            {
                put(0, "None");
                put(1, "CTO");
                put(2, "CEO");
            }
        };
        StarTrackApplication.dictionaries.put(6,seniorityDictinary);
        Map<Integer,String> industryDictinary = new HashMap<Integer,String>() {
            {
                put(0, "None");
                put(1, "Internet");
                put(2, "Pharmacy");
            }
        };
        StarTrackApplication.dictionaries.put(7,industryDictinary);
        Map<Integer,String> countryDictinary = new HashMap<Integer,String>() {
            {
                put(0, "None");
                put(1, "USA");
                put(2, "Germany");
            }
        };
        StarTrackApplication.dictionaries.put(8, countryDictinary);
        Map<Integer,String> stateDictinary = new HashMap<Integer,String>() {
            {
                put(0, "None");
                put(1, "CA");
                put(2, "NY");
            }
        };
        StarTrackApplication.dictionaries.put(9,stateDictinary);

    }

    // -1 means not selected
    private int valueId;

    public SelectableProfileProperty(int propertyId){
        super(propertyId);
        this.valueId = (-1);
    }

    public SelectableProfileProperty(int propertyId, int valueId){
        super(propertyId);
        this.valueId = valueId;
    }

    public SelectableProfileProperty(int propertyId, String value){
        super(propertyId);
        setPropertyValue(value);
    }

    public void setValueId(int valueId) {

        this.valueId = valueId;
    }

    public int getValueId() {

        return this.valueId;
    }

    @Override
    public String getPropertyValue() {
        if(this.valueId != (-1)) {
            if (StarTrackApplication.dictionaries.get(this.propertyId) != null) {
                if(this.propertyId!=4) {
                    return (String) StarTrackApplication.dictionaries.get(this.propertyId).get(this.valueId);
                } else {
                    Iterator itr = StarTrackApplication.dictionaries.get(this.propertyId).values().iterator();
                    while(itr.hasNext()) {
                        Map<Integer, String> mapElement = (Map<Integer, String>) itr.next();
                        if((int)mapElement.keySet().toArray()[0]==valueId){
                            return (String) mapElement.values().toArray()[0];
                        }
                    }
                    return null;
                }
            } else {
                return "" + this.valueId;
            }
        } else {
            return null;
        }
    }

    @Override
    public void setPropertyValue(String value){
        if(!value.equals("null")) {
            this.valueId = Integer.parseInt(value);
        } else {
            this.valueId = (-1);
        }
    }

    @Override
    public boolean hasValue() {
        if(this.valueId == (-1)) {
            return false;
        } else {
            return true;
        }
    }

    public void setPropertyTextValue(String value){
        Map<Integer,String> relevantDictinary = StarTrackApplication.dictionaries.get(this.propertyId);

        for (Map.Entry<Integer,String> entry : relevantDictinary.entrySet()) {
            if (value.equals(entry.getValue())) {
                this.valueId = entry.getKey();
            }
        }
    }
}
