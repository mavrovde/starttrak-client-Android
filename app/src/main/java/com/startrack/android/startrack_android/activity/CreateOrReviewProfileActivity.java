package com.startrack.android.startrack_android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.startrack.android.startrack_android.R;
import com.startrack.android.startrack_android.api.APIService;
import com.startrack.android.startrack_android.app.StarTrackApplication;
import com.startrack.android.startrack_android.model.BaseProfileProperty;
import com.startrack.android.startrack_android.model.EditableProfileProperty;
import com.startrack.android.startrack_android.model.Profile;
import com.startrack.android.startrack_android.model.SelectableProfileProperty;
import com.startrack.android.startrack_android.view.EditableProfilePropertyView;
import com.startrack.android.startrack_android.view.RoundedImageView;
import com.startrack.android.startrack_android.view.SelectableProfilePropertyView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vrogovskiy on 2/11/16.
 */
public class CreateOrReviewProfileActivity extends Activity implements View.OnClickListener, IActivityWithHandler {

    //private Profile currentProfile;
    private Button doneButton;
    private TextView titleTextView;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private ImageView avatarImageView;
    public static String CREATE_OR_REVIEW_FLAG_EXTRA_NAME = "CreateOrReviewFlag";
    public static String CREATE_EXTRA_VALUE = "Create";
    public static String REVIEW_EXTRA_VALUE = "Review";

    public boolean madeChangesFlag = false;
    public String createOrReviewFlag;

    private Map<Integer,SelectableProfilePropertyView> selectableProfilePropertyViewMap = new HashMap<Integer,SelectableProfilePropertyView>();
    private Map<Integer,EditableProfilePropertyView> editableProfilePropertyViewMap = new HashMap<Integer,EditableProfilePropertyView>();

    private LinearLayout propertiesLinearLayout;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_review_profile);
        init();
        this.createOrReviewFlag = getIntent().getStringExtra(CreateOrReviewProfileActivity.CREATE_OR_REVIEW_FLAG_EXTRA_NAME);
        //StarTrackApplication.sessionId = "89325b83-5730-4e5f-9e2b-4965d130f93e";
        if(createOrReviewFlag.equals(CREATE_EXTRA_VALUE)){
            madeChangesFlag = true;
            showCreateScreen();
            //StarTrackApplication.currentProfile = new Profile();
        } else if(createOrReviewFlag.equals(REVIEW_EXTRA_VALUE)){
            madeChangesFlag = false;
            showReviewScreen();
        }
        //currentProfile = StarTrackApplication.currentProfile;
        showProfile();

        //new APIService.GetProfileOperation().execute();

    }

    public static String CREATING_PROFILE_RESULT_MSG_PROPERTY_NAME = "CreatingProfileResult";
    public static String UPDATING_PROFILE_RESULT_MSG_PROPERTY_NAME = "UpdatingProfileResult";

    public static String CREATING_PROFILE_MSG_PROPERTY_VALUE_SUCCESSFUL = "CREATED_PROFILE_SUCCESSFUL";
    public static String CREATING_PROFILE_MSG_PROPERTY_VALUE_UNSUCCESSFUL = "CREATE_PROFILE_UNSUCCESSFUL";
    public static String UPDATING_PROFILE_MSG_PROPERTY_VALUE_SUCCESSFUL = "UPDATED_PROFILE_SUCCESSFUL";
    public static String UPDATING_PROFILE_MSG_PROPERTY_VALUE_UNSUCCESSFUL = "UPDATE_PROFILE_UNSUCCESSFUL";

    private void initHandler(){
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                if(msg.getData().getString(CreateOrReviewProfileActivity.CREATING_PROFILE_RESULT_MSG_PROPERTY_NAME) != null){
                    if (msg.getData().getString(CreateOrReviewProfileActivity.CREATING_PROFILE_RESULT_MSG_PROPERTY_NAME).equals(CreateOrReviewProfileActivity.CREATING_PROFILE_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                        Intent intent = new Intent(CreateOrReviewProfileActivity.this, SearchActivity.class);
                        hideProgressAndShowViews();
                        startActivity(intent);
                        CreateOrReviewProfileActivity.this.finish();
                    } else if (msg.getData().getString(CreateOrReviewProfileActivity.CREATING_PROFILE_RESULT_MSG_PROPERTY_NAME).equals(CreateOrReviewProfileActivity.CREATING_PROFILE_MSG_PROPERTY_VALUE_UNSUCCESSFUL)){
                        hideProgressAndShowViews();
                        Toast.makeText(CreateOrReviewProfileActivity.this, "Failed to create profile",
                                Toast.LENGTH_LONG).show();
                    }
                } else
                if(msg.getData().getString(CreateOrReviewProfileActivity.UPDATING_PROFILE_RESULT_MSG_PROPERTY_NAME) != null){
                    if (msg.getData().getString(CreateOrReviewProfileActivity.UPDATING_PROFILE_RESULT_MSG_PROPERTY_NAME).equals(CreateOrReviewProfileActivity.UPDATING_PROFILE_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                        Intent intent = new Intent(CreateOrReviewProfileActivity.this, SearchActivity.class);
                        hideProgressAndShowViews();
                        startActivity(intent);
                        CreateOrReviewProfileActivity.this.finish();
                    } else if (msg.getData().getString(CreateOrReviewProfileActivity.UPDATING_PROFILE_RESULT_MSG_PROPERTY_NAME).equals(CreateOrReviewProfileActivity.UPDATING_PROFILE_MSG_PROPERTY_VALUE_UNSUCCESSFUL)){
                        hideProgressAndShowViews();
                        Toast.makeText(CreateOrReviewProfileActivity.this, "Failed to update profile",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                int propertyValue = data.getIntExtra(ProfileSettingsActivity.SELECTED_ID_EXTRA_NAME, -1);
                int propertyId = data.getIntExtra(ProfileSettingsActivity.SELECTABLE_PROPERTY_ID_EXTRA_NAME, -1);
                //currentProfile.selectableProperties.get(propertyId).setValueId(propertyValue);
                if(propertyValue!=(-1)&&propertyId!=(-1)) {
                    if(propertyValue == ProfileSettingsActivity.DEFAULT_ID_EXTRA_NAME){
                        selectableProfilePropertyViewMap.get(propertyId).setValueById(-1);
                    } else {
                        if(propertyId==4){
                            HashMap hashMap = (HashMap)StarTrackApplication.dictionaries.get(4).get(propertyValue);
                            int finalKey = (-1);
                            for (Object key: hashMap.keySet()) {
                                finalKey = (Integer)key;
                            }
                            selectableProfilePropertyViewMap.get(propertyId).setValueById(finalKey);
                        } else {
                            selectableProfilePropertyViewMap.get(propertyId).setValueById(propertyValue);
                        }
                    }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private void showData(){


    }

    private void init(){
        this.doneButton = (Button) findViewById(R.id.DoneButton);
        doneButton.setOnClickListener(this);
        this.titleTextView = (TextView) findViewById(R.id.TitleText);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.progressBar.setVisibility(View.INVISIBLE);
        this.linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        Typeface custom_font_regular = Typeface.createFromAsset(getAssets(), "fonts/Bariol_Regular.otf");
        this.titleTextView.setTypeface(custom_font_regular);
        this.avatarImageView = (ImageView) findViewById(R.id.avatarImageView);
        initHandler();
    }

    private void checkUpdatesOfValues(){
        Map<Integer, EditableProfileProperty> editableProperties = StarTrackApplication.currentProfile.editableProperties;
        for(int i = 0; i<4;i++ ) {
            if(editableProperties.get(i)!=null) {
                if(editableProperties.get(i).getPropertyValue()!=null) {
                    if (!editableProfilePropertyViewMap.get(i).getCurrentValue().equals(editableProperties.get(i).getPropertyValue())) {
                        madeChangesFlag = true;
                        editableProperties.get(i).setPropertyValue(editableProfilePropertyViewMap.get(i).getCurrentValue());
                    }
                }
            }
        }

        if (!editableProfilePropertyViewMap.get(11).getCurrentValue().equals(editableProperties.get(11).getPropertyValue())) {
            madeChangesFlag = true;
            editableProperties.get(11).setPropertyValue(editableProfilePropertyViewMap.get(11).getCurrentValue());
        }

        Map<Integer, SelectableProfileProperty> selectableProperties = StarTrackApplication.currentProfile.selectableProperties;
        for(int i = 4; i < 9; i++) {
            if(selectableProperties.get(i)!=null) {
                if(selectableProfilePropertyViewMap.get(i).getValueId() != selectableProperties.get(i).getValueId()){
                    madeChangesFlag=true;
                    selectableProperties.get(i).setValueId(selectableProfilePropertyViewMap.get(i).getValueId());
                }
            }
        }
        if(selectableProfilePropertyViewMap.get(10).getValueId() != selectableProperties.get(10).getValueId()){
            madeChangesFlag=true;
            selectableProperties.get(10).setValueId(selectableProfilePropertyViewMap.get(10).getValueId());
        }
    }

    @Override
    public void onClick(View v) {
        checkUpdatesOfValues();
        if(!madeChangesFlag) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            CreateOrReviewProfileActivity.this.finish();
        } else if(madeChangesFlag && createOrReviewFlag.equals(CREATE_EXTRA_VALUE)) {
            hideViewsAndShowProgress();
            new APIService.CreateProfileOperation().execute(CreateOrReviewProfileActivity.this);
        } else if(madeChangesFlag && createOrReviewFlag.equals(REVIEW_EXTRA_VALUE)) {
            hideViewsAndShowProgress();
            new APIService.UpdateProfileOperation().execute(CreateOrReviewProfileActivity.this);
        }
    }

    private void showEditableProperties(){

    }

    private void showSelectableProperties(){

    }

    private void showCreateScreen(){
        this.titleTextView.setText("Create Profile");
    }
    private void showReviewScreen(){
        this.titleTextView.setText("Review Profile");
    }

    private void showProfile(){

        //Load image
        if(StarTrackApplication.currentProfile.getProfilePic()!=null){
            if(StarTrackApplication.currentProfile.getProfilePic().length()>5){
                new DownloadImageTask(this, this.avatarImageView)
                        .execute(StarTrackApplication.currentProfile.getProfilePic());
            }
        }

        this.propertiesLinearLayout = (LinearLayout) findViewById(R.id.propertiesLinearLayout);
/*        SelectableProfileProperty selectableProfileProperty = new  SelectableProfileProperty(0, 0);
        SelectableProfilePropertyView selectableProfilePropertyView = new SelectableProfilePropertyView(this, selectableProfileProperty);*/

        Map<Integer, EditableProfileProperty> editableProperties = StarTrackApplication.currentProfile.editableProperties;
        for(int i = 0; i<2;i++ ) {
            if(editableProperties.get(i)!=null) {
                EditableProfilePropertyView editableProfilePropertyView = new EditableProfilePropertyView(this, editableProperties.get(i));
                editableProfilePropertyViewMap.put(i,editableProfilePropertyView);
                this.propertiesLinearLayout.addView(editableProfilePropertyView);
            }
        }
        EditableProfilePropertyView cityEditableProfilePropertyView = new EditableProfilePropertyView(this, editableProperties.get(11));
        editableProfilePropertyViewMap.put(11,cityEditableProfilePropertyView);
        this.propertiesLinearLayout.addView(cityEditableProfilePropertyView);
        for(int i = 2; i<4;i++ ) {
            if(editableProperties.get(i)!=null) {
                EditableProfilePropertyView editableProfilePropertyView = new EditableProfilePropertyView(this, editableProperties.get(i));
                editableProfilePropertyViewMap.put(i,editableProfilePropertyView);
                this.propertiesLinearLayout.addView(editableProfilePropertyView);
            }
        }
        Map<Integer, SelectableProfileProperty> selectableProperties = StarTrackApplication.currentProfile.selectableProperties;
        for(int i = 5; i < 9; i++) {
            if(selectableProperties.get(i)!=null) {
                SelectableProfilePropertyView selectableProfilePropertyView = new SelectableProfilePropertyView(this, selectableProperties.get(i));
                selectableProfilePropertyViewMap.put(i, selectableProfilePropertyView);
                this.propertiesLinearLayout.addView(selectableProfilePropertyView);
            }
        }
        SelectableProfilePropertyView positionSelectableProfilePropertyView = new SelectableProfilePropertyView(this, selectableProperties.get(10));
        selectableProfilePropertyViewMap.put(10, positionSelectableProfilePropertyView);
        this.propertiesLinearLayout.addView(positionSelectableProfilePropertyView);

        SelectableProfilePropertyView companySizeSelectableProfilePropertyView = new SelectableProfilePropertyView(this, selectableProperties.get(4));
        selectableProfilePropertyViewMap.put(4, companySizeSelectableProfilePropertyView);
        this.propertiesLinearLayout.addView(companySizeSelectableProfilePropertyView);

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        private Context mContext;

        public DownloadImageTask(Context context, ImageView bmImage) {
            mContext = context;
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if(result!=null) {
                bmImage.setImageBitmap(RoundedImageView.getCroppedBitmap(result, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 150, mContext.getResources().getDisplayMetrics())));
            }
        }
    }

    public void hideProgressAndShowViews(){
        this.linearLayout.setVisibility(View.VISIBLE);
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
        this.linearLayout.setVisibility(View.INVISIBLE);
        this.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        StarTrackApplication.logout(this);
        super.onBackPressed();
    }
}