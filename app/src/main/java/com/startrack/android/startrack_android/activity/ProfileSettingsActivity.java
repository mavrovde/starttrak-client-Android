package com.startrack.android.startrack_android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.startrack.android.startrack_android.R;
import com.startrack.android.startrack_android.app.StarTrackApplication;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by vrogovskiy on 2/11/16.
 */
public class ProfileSettingsActivity extends Activity {

    public static String SELECTABLE_PROPERTY_ID_EXTRA_NAME = "SelectablePropertyIdExtraName";
    public static String SELECTED_ID_EXTRA_NAME = "SelectedIdExtraName";

    public static final int DEFAULT_ID_EXTRA_NAME = (-7);

    public static String PROFILE_SETTINGS_MODE_EXTRA_NAME = "ProfileSettingsModeExtraName";
    public static String PROFILE_SETTINGS_MODE_PROFILE_EXTRA_VALUE = "ProfileSettingsFromProfileScreen";
    public static String PROFILE_SETTINGS_MODE_SEARCH_EXTRA_VALUE = "ProfileSettingsFromSearchScreen";

    private String currentMode;

    private Button doneButton;
    private RadioGroup radioGroup;
    private TextView propertyNameTextView;
    private int propertyId;
    private int propertySelectedValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_property);
        init();
        setTitle();
        fillRadioGroup();
    }

    private void setTitle() {
        if (this.currentMode.equals(PROFILE_SETTINGS_MODE_PROFILE_EXTRA_VALUE)) {
            switch (this.propertyId) {
                case 4:
                    this.propertyNameTextView.setText("Choose Your company size level");
                    break;
                case 5:
                    this.propertyNameTextView.setText("Choose Your title");
                    break;
                case 6:
                    this.propertyNameTextView.setText("Choose Your seniority level");
                    break;
                case 7:
                    this.propertyNameTextView.setText("Choose Your industry");
                    break;
                case 8:
                    this.propertyNameTextView.setText("Choose Your country");
                    break;
                case 10:
                    this.propertyNameTextView.setText("Choose Your position");
                    break;

            }
        } else if (this.currentMode.equals(PROFILE_SETTINGS_MODE_SEARCH_EXTRA_VALUE)) {
            switch (this.propertyId) {
                case 5:
                    this.propertyNameTextView.setText("Choose title");
                    break;
                case 6:
                    this.propertyNameTextView.setText("Choose seniority level");
                    break;
                case 7:
                    this.propertyNameTextView.setText("Choose industry");
                    break;
                case 8:
                    this.propertyNameTextView.setText("Choose country");
                    break;
                case 10:
                    this.propertyNameTextView.setText("Choose position");
                    break;

            }
        }

    }

    private void init() {
        this.doneButton = (Button) findViewById(R.id.DoneButton);
        this.radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        this.propertyNameTextView = (TextView) findViewById(R.id.PropertyNameTextView);
        Typeface custom_font_regular = Typeface.createFromAsset(getAssets(), "fonts/Bariol_Regular.otf");
        this.propertyNameTextView.setTypeface(custom_font_regular);
        this.propertyId = getIntent().getIntExtra(SELECTABLE_PROPERTY_ID_EXTRA_NAME, -1);
        this.currentMode = getIntent().getStringExtra(PROFILE_SETTINGS_MODE_EXTRA_NAME);
        this.propertySelectedValue = getIntent().getIntExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, -1);
        this.doneButton.setTypeface(custom_font_regular);
    }

    private void fillRadioGroup() {
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{

                        new int[]{-android.R.attr.state_enabled}, //disabled
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[]{

                        Color.BLACK //disabled
                        , Color.WHITE //enabled

                }
        );
        Typeface custom_font_regular = Typeface.createFromAsset(getAssets(), "fonts/Bariol_Regular.otf");
        RadioButton firstRadioButton = new RadioButton(this);
        if (currentMode.equals(PROFILE_SETTINGS_MODE_PROFILE_EXTRA_VALUE)) {
            firstRadioButton.setText("None");
            firstRadioButton.setId(R.id.default_value);
        } else if (currentMode.equals(PROFILE_SETTINGS_MODE_SEARCH_EXTRA_VALUE)) {
            firstRadioButton.setText("All");
            firstRadioButton.setId(R.id.default_value);
        }
        if (this.propertySelectedValue == (-1)) {
            firstRadioButton.setChecked(true);
        }
        firstRadioButton.setTextColor(Color.WHITE);
        firstRadioButton.setTypeface(custom_font_regular);
        firstRadioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        firstRadioButton.setButtonDrawable(R.drawable.radio);
//        firstRadioButton.setButtonTintList(colorStateList);//set the color tint list
        firstRadioButton.invalidate(); //could not be necessar
        this.radioGroup.addView(firstRadioButton);
        if (this.propertyId == 4) {
            Map<Integer, Map<Integer, String>> propertyMapValues =
                    StarTrackApplication.dictionaries.get(this.propertyId);

            Iterator itr = propertyMapValues.values().iterator();
            while (itr.hasNext()) {
                Map<Integer, String> element = (Map<Integer, String>) itr.next();

                RadioButton radioButton = new RadioButton(this);
                radioButton.setText((String) element.values().toArray()[0]);
                radioButton.setTextColor(Color.WHITE);
                radioButton.setId(element.keySet().iterator().next());
                radioButton.setButtonDrawable(R.drawable.radio);
                if (element.keySet().iterator().next() == this.propertySelectedValue) {
                    radioButton.setChecked(true);
                }
                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                radioButton.setTypeface(custom_font_regular);
                //radioButton.setButtonTintList(Color.WHITE);


//                radioButton.setButtonTintList(colorStateList);//set the color tint list
                radioButton.invalidate(); //could not be necessar
                this.radioGroup.addView(radioButton);
            }

        } else {
            Map<Integer, String> propertyValues = StarTrackApplication.dictionaries.get(this.propertyId);
            Set<Integer> integerSet = propertyValues.keySet();
            for (int i : integerSet) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(propertyValues.get(i));
                radioButton.setTextColor(Color.WHITE);
                radioButton.setId(i);
                radioButton.setButtonDrawable(R.drawable.radio);
                if (i == this.propertySelectedValue) {
                    radioButton.setChecked(true);
                }
                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                radioButton.setTypeface(custom_font_regular);
//                radioButton.setButtonTintList(colorStateList);//set the color tint list
                radioButton.invalidate(); //could not be necessar
                this.radioGroup.addView(radioButton);
            }
        }
    }

    public void doneOnClick(View v) {
        Intent returnIntent = new Intent();
        int checkedRadioButtonId = this.radioGroup.getCheckedRadioButtonId();
        if (checkedRadioButtonId == R.id.default_value) {
            returnIntent.putExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, DEFAULT_ID_EXTRA_NAME);
        } else {
            if (this.propertyId != 4) {
                returnIntent.putExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, checkedRadioButtonId);
            } else {
                switch (checkedRadioButtonId) {
                    case 1:
                        returnIntent.putExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, 0);
                        break;
                    case 2:
                        returnIntent.putExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, 1);
                        break;
                    case 3:
                        returnIntent.putExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, 11);
                        break;
                    case 4:
                        returnIntent.putExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, 51);
                        break;
                    case 5:
                        returnIntent.putExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, 201);
                        break;
                    case 6:
                        returnIntent.putExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, 501);
                        break;
                    case 7:
                        returnIntent.putExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, 1001);
                        break;
                    case 8:
                        returnIntent.putExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, 5001);
                        break;
                    case 9:
                        returnIntent.putExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, 10001);
                        break;
                }
            }
        }
        returnIntent.putExtra(ProfileSettingsActivity.SELECTABLE_PROPERTY_ID_EXTRA_NAME, this.propertyId);
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, returnIntent);
        } else {
            getParent().setResult(Activity.RESULT_OK, returnIntent);
        }
        finish();
    }
}
