package com.startrack.android.startrack_android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.startrack.android.startrack_android.R;
import com.startrack.android.startrack_android.api.APIService;
import com.startrack.android.startrack_android.api.ApiHandler;
import com.startrack.android.startrack_android.app.StarTrackApplication;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by vrogovskiy on 2/11/16.
 */
public class SearchActivity extends Activity implements IActivityWithHandler {

    private TextView titleTextView;
    private EditText searchEditText;

    private TextView SeniorityLevelTitleTextView;
    private TextView SeniorityLevelValueTextView;

    private TextView IndustryTypeTitleTextView;
    private TextView IndustryTypeValueTextView;

    private TextView CountryTitleTextView;
    private TextView CountryValueTextView;

    private TextView PositionTitleTextView;
    private TextView PositionValueTextView;

    private TextView CompanySizeTitleTextView;
    private TextView CompanySizeValueTextView;



    private ProgressBar progressBar;
//    private RelativeLayout relativeLayout;

/*    private String searchText;
    private int industryId;
    private int positionId;
    private int companysizeId;
    private int seniorityId;*/

    //private int countryId;


    public static String SEARCHING_RESULT_MSG_PROPERTY_NAME = "SearchingResultMsgPropertyName";
    public static String SEARCHING_MSG_PROPERTY_VALUE_SUCCESSFUL = "SEARCHING_SUCCESSFUL";
    public static String SEARCHING_MSG_PROPERTY_VALUE_UNSUCCESSFUL = "SEARCHING_UNSUCCESSFUL";

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
        initHandler();
        clearSeachParameters();
    }

    @Override
    protected void onResume(){
        super.onResume();
        // 14400 = 4 hours in seconds
        if(currentTimestamp>0 && ((System.currentTimeMillis()/1000 - currentTimestamp)>14400)){
            new APIService.ValidateSessionIdOperation().execute(SearchActivity.this);
        } else {
            hideProgressAndShowViews();
        }

    }

    private void clearSeachParameters(){
        StarTrackApplication.searchText = "";
        StarTrackApplication.titleId = (-1);
        StarTrackApplication.seniorityId = (-1);
        StarTrackApplication.industryId = (-1);
        StarTrackApplication.countryId = (-1);
        StarTrackApplication.positionId = (-1);
        StarTrackApplication.companysizeId = (-1);
    }

    private void initHandler(){
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                if(msg.getData().getString(SearchActivity.SEARCHING_RESULT_MSG_PROPERTY_NAME) != null){
                    if (msg.getData().getString(SearchActivity.SEARCHING_RESULT_MSG_PROPERTY_NAME).equals(SearchActivity.SEARCHING_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                        Intent intent = new Intent(SearchActivity.this, MatchesActivity.class);
                        startActivity(intent);
                    } else if (msg.getData().getString(SearchActivity.SEARCHING_RESULT_MSG_PROPERTY_NAME).equals(SearchActivity.SEARCHING_MSG_PROPERTY_VALUE_UNSUCCESSFUL)){
                        hideProgressAndShowViews();
                        Toast.makeText(SearchActivity.this, "Failed to get search results",
                                Toast.LENGTH_LONG).show();
                    }
                } else if(msg.getData().getString(ApiHandler.VALIDATING_SESSION_ID_RESULT_MSG_PROPERTY_NAME) != null){
                    if (msg.getData().getString(ApiHandler.VALIDATING_SESSION_ID_RESULT_MSG_PROPERTY_NAME).equals(ApiHandler.VALIDATING_SESSION_ID_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                        hideProgressAndShowViews();
                    } else if (msg.getData().getString(ApiHandler.VALIDATING_SESSION_ID_RESULT_MSG_PROPERTY_NAME).equals(ApiHandler.VALIDATING_SESSION_ID_MSG_PROPERTY_VALUE_UNSUCCESSFUL)){
                        currentTimestamp = (long)0;
                        SearchActivity.super.onBackPressed();
                    }
                }
            }
        };
    }

    private Long currentTimestamp = (long)0;

    @Override
    protected void onPause (){
        currentTimestamp = System.currentTimeMillis()/1000;
        super.onPause();
    }


    private void setupViews(){
        Typeface custom_font_regular = Typeface.createFromAsset(getAssets(), "fonts/Bariol_Regular.otf");

        this.titleTextView = (TextView)  findViewById(R.id.TitleText);
        this.searchEditText = (EditText) findViewById(R.id.SearchEditText);
        this.SeniorityLevelTitleTextView  = (TextView)  findViewById(R.id.SeniorityTitleTextView);
        this.SeniorityLevelValueTextView = (TextView)  findViewById(R.id.SeniorityValueTextView);
        this.IndustryTypeTitleTextView = (TextView)  findViewById(R.id.IndustryTitleTextView);
        this.IndustryTypeValueTextView  = (TextView)  findViewById(R.id.IndustryValueTextView);
        this.CountryTitleTextView  = (TextView)  findViewById(R.id.CountryTitleTextView);
        this.CountryValueTextView  = (TextView)  findViewById(R.id.CountryValueTextView);
        this.PositionTitleTextView  = (TextView)  findViewById(R.id.PositionTitleTextView);
        this.PositionValueTextView  = (TextView)  findViewById(R.id.PositionValueTextView);
        this.CompanySizeTitleTextView  = (TextView)  findViewById(R.id.CompanySizeTitleTextView);
        this.CompanySizeValueTextView  = (TextView)  findViewById(R.id.CompanySizeValueTextView);

//        this.relativeLayout = (RelativeLayout) findViewById(R.id.RelativeLayout);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.progressBar.setVisibility(View.INVISIBLE);

        this.searchEditText.setTypeface(custom_font_regular);
        this.titleTextView.setTypeface(custom_font_regular);
        this.SeniorityLevelTitleTextView.setTypeface(custom_font_regular);
        this.SeniorityLevelValueTextView.setTypeface(custom_font_regular);
        this.IndustryTypeTitleTextView.setTypeface(custom_font_regular);
        this.IndustryTypeValueTextView.setTypeface(custom_font_regular);
        this.CountryTitleTextView.setTypeface(custom_font_regular);
        this.CountryValueTextView.setTypeface(custom_font_regular);
        this.PositionTitleTextView.setTypeface(custom_font_regular);
        this.PositionValueTextView.setTypeface(custom_font_regular);
        this.CompanySizeTitleTextView.setTypeface(custom_font_regular);
        this.CompanySizeValueTextView.setTypeface(custom_font_regular);
    }

    public void titleButtonOnClick(View v) {
        Intent intent = new Intent(SearchActivity.this, ProfileSettingsActivity.class);
        intent.putExtra(ProfileSettingsActivity.SELECTABLE_PROPERTY_ID_EXTRA_NAME, 5);
        intent.putExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, StarTrackApplication.titleId);
        intent.putExtra(ProfileSettingsActivity.PROFILE_SETTINGS_MODE_EXTRA_NAME, ProfileSettingsActivity.PROFILE_SETTINGS_MODE_SEARCH_EXTRA_VALUE);
        SearchActivity.this.startActivityForResult(intent, 1);
    }

    public void seniorityButtonOnClick(View v) {
        Intent intent = new Intent(SearchActivity.this, ProfileSettingsActivity.class);
        intent.putExtra(ProfileSettingsActivity.SELECTABLE_PROPERTY_ID_EXTRA_NAME, 6);
        intent.putExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, StarTrackApplication.seniorityId);
        intent.putExtra(ProfileSettingsActivity.PROFILE_SETTINGS_MODE_EXTRA_NAME, ProfileSettingsActivity.PROFILE_SETTINGS_MODE_SEARCH_EXTRA_VALUE);
        SearchActivity.this.startActivityForResult(intent,1);
    }


    public void industryButtonOnClick(View v) {
        Intent intent = new Intent(SearchActivity.this, ProfileSettingsActivity.class);
        intent.putExtra(ProfileSettingsActivity.SELECTABLE_PROPERTY_ID_EXTRA_NAME, 7);
        intent.putExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, StarTrackApplication.industryId);
        intent.putExtra(ProfileSettingsActivity.PROFILE_SETTINGS_MODE_EXTRA_NAME, ProfileSettingsActivity.PROFILE_SETTINGS_MODE_SEARCH_EXTRA_VALUE);
        SearchActivity.this.startActivityForResult(intent, 1);
    }


    public void countryButtonOnClick(View v) {
        Intent intent = new Intent(SearchActivity.this, ProfileSettingsActivity.class);
        intent.putExtra(ProfileSettingsActivity.SELECTABLE_PROPERTY_ID_EXTRA_NAME, 8);
        intent.putExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, StarTrackApplication.countryId);
        intent.putExtra(ProfileSettingsActivity.PROFILE_SETTINGS_MODE_EXTRA_NAME, ProfileSettingsActivity.PROFILE_SETTINGS_MODE_SEARCH_EXTRA_VALUE);
        SearchActivity.this.startActivityForResult(intent,1);
    }

    public void positionButtonOnClick(View v) {
        Intent intent = new Intent(SearchActivity.this, ProfileSettingsActivity.class);
        intent.putExtra(ProfileSettingsActivity.SELECTABLE_PROPERTY_ID_EXTRA_NAME, 10);
        intent.putExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, StarTrackApplication.positionId);
        intent.putExtra(ProfileSettingsActivity.PROFILE_SETTINGS_MODE_EXTRA_NAME, ProfileSettingsActivity.PROFILE_SETTINGS_MODE_SEARCH_EXTRA_VALUE);
        SearchActivity.this.startActivityForResult(intent,1);
    }
    public void companySizeButtonOnClick(View v) {
        Intent intent = new Intent(SearchActivity.this, ProfileSettingsActivity.class);
        intent.putExtra(ProfileSettingsActivity.SELECTABLE_PROPERTY_ID_EXTRA_NAME, 4);
        intent.putExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, StarTrackApplication.companysizeId);
        intent.putExtra(ProfileSettingsActivity.PROFILE_SETTINGS_MODE_EXTRA_NAME, ProfileSettingsActivity.PROFILE_SETTINGS_MODE_SEARCH_EXTRA_VALUE);
        SearchActivity.this.startActivityForResult(intent,1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                int propertyValue = data.getIntExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, -1);
                int propertyId = data.getIntExtra(ProfileSettingsActivity.SELECTABLE_PROPERTY_ID_EXTRA_NAME, -1);
                switch(propertyId){
                    case 6:
                        if(propertyValue == ProfileSettingsActivity.DEFAULT_ID_EXTRA_NAME){
                            this.SeniorityLevelValueTextView.setText("All");
                            StarTrackApplication.seniorityId =  (-1);
                        } else {
                            this.SeniorityLevelValueTextView.setText((String)StarTrackApplication.dictionaries.get(6).get(propertyValue));
                            StarTrackApplication.seniorityId = propertyValue;
                        }
                        break;
                    case 7:
                        if(propertyValue == ProfileSettingsActivity.DEFAULT_ID_EXTRA_NAME){
                            this.IndustryTypeValueTextView.setText("All");
                            StarTrackApplication.industryId =  (-1);
                        } else {
                            this.IndustryTypeValueTextView.setText((String)StarTrackApplication.dictionaries.get(7).get(propertyValue));
                            StarTrackApplication.industryId = propertyValue;
                        }
                        break;
                    case 8:
                        if(propertyValue == ProfileSettingsActivity.DEFAULT_ID_EXTRA_NAME){
                            this.CountryValueTextView.setText("All");
                            StarTrackApplication.countryId =  (-1);
                        } else {
                            this.CountryValueTextView.setText((String)StarTrackApplication.dictionaries.get(8).get(propertyValue));
                            StarTrackApplication.countryId = propertyValue;
                        }
                        break;
                    case 4:
                    if(propertyValue == ProfileSettingsActivity.DEFAULT_ID_EXTRA_NAME){
                        this.CompanySizeValueTextView.setText("All");
                        StarTrackApplication.companysizeId =  (-1);
                    } else {
                        HashMap hashMap = (HashMap)StarTrackApplication.dictionaries.get(4).get(propertyValue);
                        int finalKey = (-1);
                        for (Object key: hashMap.keySet()) {
                            finalKey = (Integer)key;
                        }
                        this.CompanySizeValueTextView.setText((String) hashMap.get(finalKey));
                        //this.CompanySizeValueTextView.setText((String)StarTrackApplication.dictionaries.get(4).get(propertyValue));
                        StarTrackApplication.companysizeId = finalKey;
                    }
                    break;
                    case 10:
                        if(propertyValue == ProfileSettingsActivity.DEFAULT_ID_EXTRA_NAME){
                            this.PositionValueTextView.setText("All");
                            StarTrackApplication.positionId =  (-1);
                        } else {
                            this.PositionValueTextView.setText((String)StarTrackApplication.dictionaries.get(10).get(propertyValue));
                            StarTrackApplication.positionId = propertyValue;
                        }
                        break;

                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private boolean isSearchCriteriasSelected(){
        boolean flag = false;
        if(!StarTrackApplication.searchText.equals("")){
            flag = true;
        }
        if(StarTrackApplication.titleId > (-1)){
            flag = true;
        }
        if(StarTrackApplication.seniorityId > (-1)){
            flag=true;
        }
        if(StarTrackApplication.industryId > (-1)){
            flag=true;
        }
        if(StarTrackApplication.countryId > (-1)){
            flag=true;
        }
        if(StarTrackApplication.positionId > (-1)){
            flag=true;
        }
        if(StarTrackApplication.companysizeId > (-1)){
            flag=true;
        }
       return flag;
    }

    public void doneButtonOnClick(View v) {
        StarTrackApplication.searchText = this.searchEditText.getText().toString();
        if(isSearchCriteriasSelected()) {
            hideViewsAndShowProgress();
            new APIService.SearchOperation().execute(SearchActivity.this);
        } else {
            Toast.makeText(this, "Please specify either search text or select at least one search criteria in the list",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void clearButtonOnClick(View v) {
        this.searchEditText.setText("");
    }

    public void hideProgressAndShowViews(){
//        this.relativeLayout.setVisibility(View.VISIBLE);
        this.progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showAuthorizarionDialogForXing() {

    }

    public void hideViewsAndShowProgress(){
//        this.relativeLayout.setVisibility(View.INVISIBLE);
        this.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onBackPressed() {
        currentTimestamp = (long)0;
        StarTrackApplication.logout(this);
        super.onBackPressed();
    }
}