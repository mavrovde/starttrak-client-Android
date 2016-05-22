package com.startrack.android.startrack_android.model;

import android.text.TextUtils;

/**
 * Created by vrogovskiy on 2/14/16.
 */
public class EditableProfileProperty extends BaseProfileProperty {
    private String value;

    public EditableProfileProperty(int propertyId) {
        super(propertyId);
        this.value = "";
    }

    public EditableProfileProperty(int propertyId, String value) {
        super(propertyId);
        this.value = value;
    }

    @Override
    public void setPropertyValue(String value) {
        this.value = value;
    }

    @Override
    public boolean hasValue() {
//        if(this.value.equals("")) {
//            return false;
//        } else {
//            return true;
//        }
        return !TextUtils.isEmpty(value);
    }

    @Override
    public String getPropertyValue() {
        return value;
    }
}
