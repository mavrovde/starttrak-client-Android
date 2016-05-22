package com.startrack.android.startrack_android.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.startrack.android.startrack_android.R;
import com.startrack.android.startrack_android.activity.CreateOrReviewProfileActivity;
import com.startrack.android.startrack_android.activity.ProfileSettingsActivity;
import com.startrack.android.startrack_android.activity.WelcomeActivity;
import com.startrack.android.startrack_android.app.StarTrackApplication;
import com.startrack.android.startrack_android.model.BaseProfileProperty;
import com.startrack.android.startrack_android.model.SelectableProfileProperty;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by vrogovskiy on 2/15/16.
 */
public class SelectableProfilePropertyView extends RelativeLayout implements View.OnClickListener {

    private TextView propertyName;
    private TextView selectedPropertyValue;
    private ImageButton selectPropertyButton;
    private CreateOrReviewProfileActivity activity;

    public int getValueId() {
        return valueId;
    }

    public void setValueId(int valueId) {
        this.valueId = valueId;
    }

    private int propertyId;
    private int valueId;
    //private SelectableProfileProperty selectableProfileProperty;

    public SelectableProfilePropertyView(Context context) {
        super(context);
        init();
    }

    public SelectableProfilePropertyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectableProfilePropertyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

/*    public SelectableProfilePropertyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }*/

    public SelectableProfilePropertyView(CreateOrReviewProfileActivity activity, SelectableProfileProperty selectableProfileProperty) {
        super(activity);
        //this.selectableProfileProperty = selectableProfileProperty;
        init();
        this.activity = activity;
        this.propertyId = selectableProfileProperty.getPropertyId();
        this.valueId = selectableProfileProperty.getValueId();
        this.propertyName.setText(selectableProfileProperty.getProfilePropertyName());
        this.selectedPropertyValue.setHint(selectableProfileProperty.getProfilePropertyName());
        Typeface custom_font_regular = Typeface.createFromAsset(activity.getAssets(), "fonts/Bariol_Regular.otf");
        this.propertyName.setTypeface(custom_font_regular);
        this.selectedPropertyValue.setTypeface(custom_font_regular);
        if (this.valueId != (-1)) {
            this.selectedPropertyValue.setText(selectableProfileProperty.getPropertyValue());
        } else {
            this.selectedPropertyValue.setText("");
        }
    }

    public void setValueById(int valueId){
        this.valueId = valueId;
        if(valueId!=(-1)) {
            if(propertyId != 4) {
                this.selectedPropertyValue.setText((String) StarTrackApplication.dictionaries.get(this.propertyId).get(this.valueId));
            } else {
                Iterator itr = StarTrackApplication.dictionaries.get(this.propertyId).values().iterator();
                while(itr.hasNext()) {
                    Map<Integer, String> mapElement = (Map<Integer, String>) itr.next();
                    if((int)mapElement.keySet().toArray()[0]==valueId){
                        this.selectedPropertyValue.setText((String) mapElement.values().toArray()[0]);
                    }
                }


                //this.selectedPropertyValue.setText((Integer) StarTrackApplication.dictionaries.get(this.propertyId).get(this.valueId));
            }
        } else {
            this.selectedPropertyValue.setText("");
        }
    }


    private void init() {
        inflate(getContext(), R.layout.selectable_property_item, this);
        this.propertyName = (TextView)findViewById(R.id.propertyName);
        this.selectedPropertyValue = (TextView)findViewById(R.id.selectedPropertyValue);
        this.selectPropertyButton = (ImageButton)findViewById(R.id.selectPropertyButton);
        this.selectPropertyButton.setOnClickListener(this);

        Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Bariol_Regular.otf");
        this.propertyName.setTypeface(custom_font);
        this.selectedPropertyValue.setTypeface(custom_font);

        this.selectedPropertyValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    propertyName.setVisibility(VISIBLE);
                else propertyName.setVisibility(INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public ImageButton getSelectPropertyButton() {
        return selectPropertyButton;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(activity, ProfileSettingsActivity.class);
        intent.putExtra(ProfileSettingsActivity.SELECTABLE_PROPERTY_ID_EXTRA_NAME, this.propertyId);
        intent.putExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, this.valueId);
        intent.putExtra(ProfileSettingsActivity.PROFILE_SETTINGS_MODE_EXTRA_NAME, ProfileSettingsActivity.PROFILE_SETTINGS_MODE_PROFILE_EXTRA_VALUE);
        this.activity.startActivityForResult(intent,1);

        Message msg = new Message();
        Bundle data = new Bundle();
        //data.putString(WelcomeActivity.GETTING_PROFILE_RESULT_MSG_PROPERTY_NAME, WelcomeActivity.GETTING_PROFILE_MSG_PROPERTY_VALUE_SUCCESSFUL);
        msg.setData(data);
        activity.getHandler().sendMessage(msg);
    }
}
