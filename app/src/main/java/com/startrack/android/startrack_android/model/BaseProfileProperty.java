package com.startrack.android.startrack_android.model;

import com.startrack.android.startrack_android.app.StarTrackApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vrogovskiy on 2/14/16.
 */
public abstract class BaseProfileProperty {



/*
    public static void init(){
        StarTrackApplication.editableProperties.put(0, new EditableProfileProperty(0));
        StarTrackApplication.editableProperties.put(1, new EditableProfileProperty(1));
        StarTrackApplication.editableProperties.put(2, new EditableProfileProperty(2));
        StarTrackApplication.editableProperties.put(3, new EditableProfileProperty(3));

        StarTrackApplication.selectableProperties.put(4, new SelectableProfileProperty(4));
        StarTrackApplication.selectableProperties.put(5, new SelectableProfileProperty(5));
        StarTrackApplication.selectableProperties.put(6, new SelectableProfileProperty(6));
        StarTrackApplication.selectableProperties.put(7, new SelectableProfileProperty(7));
        StarTrackApplication.selectableProperties.put(8, new SelectableProfileProperty(8));
        StarTrackApplication.selectableProperties.put(9, new SelectableProfileProperty(9));
        StarTrackApplication.selectableProperties.put(10, new SelectableProfileProperty(10));

        //SelectableProfileProperty.writeDummyData();
    }*/

    protected int propertyId;
    /*    0 - first name
*     1 - last name
*     2 - phone number
*     3 - company name
*     4 - company size
*     5 - title
*     6 - seniority
*     7 - industry
*     8 - country
*     9 - state
*     10 - position
* */


    BaseProfileProperty(int propertyId){
        this.propertyId = propertyId;
    }

    public String getProfilePropertyName(){
        return StarTrackApplication.propertyNames.get(this.propertyId);
    }

    public String getPropertyName(){
        return StarTrackApplication.propertyNames.get(this.propertyId);
    }

    public int getPropertyId(){
        return this.propertyId;
    }

    public abstract String getPropertyValue();

    public abstract void setPropertyValue(String propertyValue);

    public abstract boolean hasValue();

}
