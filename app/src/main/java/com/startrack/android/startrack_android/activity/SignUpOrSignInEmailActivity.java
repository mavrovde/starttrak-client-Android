package com.startrack.android.startrack_android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.startrack.android.startrack_android.R;
import com.startrack.android.startrack_android.api.APIService;
import com.startrack.android.startrack_android.api.ApiHandler;
import com.startrack.android.startrack_android.app.StarTrackApplication;
import com.startrack.android.startrack_android.model.Profile;

/**
 * Created by vrogovskiy on 2/11/16.
 */
public class SignUpOrSignInEmailActivity extends Activity implements IActivityWithHandler {

    public static String SIGN_UP_OR_SIGN_IN_FLAG_EXTRA_NAME = "SignUpOrSignFlag";
    public static String SIGN_UP_EXTRA_VALUE = "SignUp";
    public static String SIGN_IN_EXTRA_VALUE = "SignIn";

    private EditText emailEditText;
    private EditText passwordEditText;
    private ImageButton backButton;
    private Button doneButton;
    private TextView forgotPasswordButton;
    private TextView titleView;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;

    private ApiHandler handler;

    private String currentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_or_sign_in_with_email);
        init();
        String signInOrSignUpFlag = getIntent().getStringExtra(SignUpOrSignInEmailActivity.SIGN_UP_OR_SIGN_IN_FLAG_EXTRA_NAME);
        if(signInOrSignUpFlag.equals(SignUpOrSignInEmailActivity.SIGN_UP_EXTRA_VALUE)){
            currentMode = SignUpOrSignInEmailActivity.SIGN_UP_EXTRA_VALUE;
            showSignUPScreen();
            //StarTrackApplication.currentProfile = new Profile();
        }
        if(signInOrSignUpFlag.equals(SignUpOrSignInEmailActivity.SIGN_IN_EXTRA_VALUE)){
            currentMode = SignUpOrSignInEmailActivity.SIGN_IN_EXTRA_VALUE;
            showSignINScreen();
        }

        //StarTrackApplication.sessionId = "89325b83-5730-4e5f-9e2b-4965d130f93e";
        //StarTrackApplication.oauthToken = "AQU44QMicY_FY_gmLO0l4Ta2CE8zlHhwOjXwpcFvU8GDl1K8dE66aXvewoVs61T4tAprXCQy8f6yerXVIVButoSiPXKwPwLhjzzQuoXWoWoCaP1Jd5Fsakq_ysE-qlSA77A-Zc6ZKJIe7FgGlwPHlVgN88MlPSqk8X9avMdb6D6jUCNkSBo";
        //StarTrackApplication.soc_network_type = 2;
        //new APIService.GetSessionIdOperation().execute();
        //new APIService.GetDictionariesOperation().execute();
        //StarTrackApplication.currentProfile = APIService.createDummyProfile(0);
    }

/*    private void initHandler(){
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if(msg.getData().getString(ApiHandler.GETTING_SESSION_ID_RESULT_MSG_PROPERTY_NAME) != null){
                    if (msg.getData().getString(ApiHandler.GETTING_SESSION_ID_RESULT_MSG_PROPERTY_NAME).equals(ApiHandler.GETTING_SESSION_ID_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                        ApiHandler.setSocTypeFromPreferences(SignUpOrSignInEmailActivity.this, StarTrackApplication.soc_network_type);
                        ApiHandler.setLoginFromPreferences(SignUpOrSignInEmailActivity.this, StarTrackApplication.internal_account_email);
                        ApiHandler.setPasswordFromPreferences(SignUpOrSignInEmailActivity.this, StarTrackApplication.internal_account_password);
                        new APIService.GetDictionariesOperation().execute(SignUpOrSignInEmailActivity.this);
                    } else if (msg.getData().getString(ApiHandler.GETTING_SESSION_ID_RESULT_MSG_PROPERTY_NAME).equals(ApiHandler.GETTING_SESSION_ID_MSG_PROPERTY_VALUE_UNSUCCESSFUL)){
                        hideProgressAndShowViews();
                        Toast.makeText(SignUpOrSignInEmailActivity.this, "Failed to get session id",
                                Toast.LENGTH_LONG).show();
                    }
                } else
                if(msg.getData().getString(ApiHandler.GETTING_DICTIONARIES_RESULT_MSG_PROPERTY_NAME) != null){
                    if (msg.getData().getString(ApiHandler.GETTING_DICTIONARIES_RESULT_MSG_PROPERTY_NAME).equals(ApiHandler.GETTING_DICTIONARIES_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                        if(currentMode.equals(SignUpOrSignInEmailActivity.SIGN_IN_EXTRA_VALUE)) {
                            if(!StarTrackApplication.allScreensAlwaysForTest) {
                                if (!StarTrackApplication.profileExistsFlag) {
                                    new APIService.GetProfileOperation().execute(SignUpOrSignInEmailActivity.this);
                                } else {
                                    Intent intent = new Intent(SignUpOrSignInEmailActivity.this, SearchActivity.class);
                                    hideProgressAndShowViews();
                                    startActivity(intent);
                                    SignUpOrSignInEmailActivity.this.finish();
                                }
                            } else {
                                new APIService.GetProfileOperation().execute(SignUpOrSignInEmailActivity.this);
                            }
                        } else if(currentMode.equals(SignUpOrSignInEmailActivity.SIGN_UP_EXTRA_VALUE)){
                            Intent intent = new Intent(SignUpOrSignInEmailActivity.this, CreateOrReviewProfileActivity.class);
                            intent.putExtra(CreateOrReviewProfileActivity.CREATE_OR_REVIEW_FLAG_EXTRA_NAME, CreateOrReviewProfileActivity.CREATE_EXTRA_VALUE);
                            hideProgressAndShowViews();
                            startActivity(intent);
                            SignUpOrSignInEmailActivity.this.finish();
                        }
                    } else if (msg.getData().getString(ApiHandler.GETTING_DICTIONARIES_RESULT_MSG_PROPERTY_NAME).equals(ApiHandler.GETTING_DICTIONARIES_MSG_PROPERTY_VALUE_UNSUCCESSFUL)){
                        hideProgressAndShowViews();
                        Toast.makeText(SignUpOrSignInEmailActivity.this, "Failed to get dictionaries",
                                Toast.LENGTH_LONG).show();
                    }
                } else
                if(msg.getData().getString(ApiHandler.GETTING_PROFILE_RESULT_MSG_PROPERTY_NAME) != null){
                    if (msg.getData().getString(ApiHandler.GETTING_PROFILE_RESULT_MSG_PROPERTY_NAME).equals(ApiHandler.GETTING_PROFILE_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                        Intent intent = new Intent(SignUpOrSignInEmailActivity.this, CreateOrReviewProfileActivity.class);
                        intent.putExtra(CreateOrReviewProfileActivity.CREATE_OR_REVIEW_FLAG_EXTRA_NAME, CreateOrReviewProfileActivity.REVIEW_EXTRA_VALUE);
                        hideProgressAndShowViews();
                        startActivity(intent);
                        SignUpOrSignInEmailActivity.this.finish();
                    } else if (msg.getData().getString(ApiHandler.GETTING_PROFILE_RESULT_MSG_PROPERTY_NAME).equals(ApiHandler.GETTING_PROFILE_MSG_PROPERTY_VALUE_UNSUCCESSFUL)){
                        Toast.makeText(SignUpOrSignInEmailActivity.this, "Failed to get profile",
                                Toast.LENGTH_LONG).show();
                        hideProgressAndShowViews();
                    }
                }
            }

        };
    }*/

    public void hideProgressAndShowViews(){
/*        this.SignIn.setVisibility(View.VISIBLE);
        this.signUpWithEmailButton.setVisibility(View.VISIBLE);
        this.facebookButton.setVisibility(View.VISIBLE);
        this.linkedinButton.setVisibility(View.VISIBLE);
        this.xingButton.setVisibility(View.VISIBLE);
        this.welcomeTextView.setVisibility(View.VISIBLE);
        this.orTextView.setVisibility(View.VISIBLE);
        this.accountTextView.setVisibility(View.VISIBLE);*/
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
/*        this.SignIn.setVisibility(View.INVISIBLE);
        this.signUpWithEmailButton.setVisibility(View.INVISIBLE);
        this.facebookButton.setVisibility(View.INVISIBLE);
        this.linkedinButton.setVisibility(View.INVISIBLE);
        this.xingButton.setVisibility(View.INVISIBLE);
        this.welcomeTextView.setVisibility(View.INVISIBLE);
        this.orTextView.setVisibility(View.INVISIBLE);
        this.accountTextView.setVisibility(View.INVISIBLE);*/
        this.linearLayout.setVisibility(View.INVISIBLE);
        this.progressBar.setVisibility(View.VISIBLE);
    }

    private void init(){
        this.doneButton = (Button) findViewById(R.id.SignInButton);
        this.emailEditText = (EditText) findViewById(R.id.EnterEmailEditText);
        this.passwordEditText = (EditText) findViewById(R.id.EnterPasswordEditText);
        this.backButton = (ImageButton) findViewById(R.id.BackButton);
        this.forgotPasswordButton = (TextView) findViewById(R.id.ForgotPasswordButton);
        this.titleView = (TextView)  findViewById(R.id.TitleText);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.progressBar.setVisibility(View.INVISIBLE);
        this.linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        Typeface custom_font_regular = Typeface.createFromAsset(getAssets(), "fonts/Bariol_Regular.otf");
        Typeface custom_font_light = Typeface.createFromAsset(getAssets(), "fonts/Bariol_Light.otf");
        this.emailEditText.setTypeface(custom_font_light);
        this.passwordEditText.setTypeface(custom_font_light);
        this.forgotPasswordButton.setTypeface(custom_font_regular);
        this.titleView.setTypeface(custom_font_regular);
        this.doneButton.setTypeface(custom_font_regular);
        handler = new ApiHandler(this);
//        this.emailEditText.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
//        this.passwordEditText.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
    }

    private void showSignUPScreen(){
        this.forgotPasswordButton.setVisibility(View.INVISIBLE);
        this.titleView.setText("Sign up with email");
    }

    private void showSignINScreen(){
        this.forgotPasswordButton.setVisibility(View.VISIBLE);
        this.titleView.setText("Sign in with email");
    }

    public void backButtonOnClick(View v) {
        this.onBackPressed();
    }

    public void forgotPasswordButtonOnClick(View v){

    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public final static boolean isValidPassword(CharSequence target) {
        return (target.length()>=6);
    }

    public void doneButtonOnClick(View v) {
        if(isValidEmail(this.emailEditText.getText())) {
            if(isValidPassword(this.passwordEditText.getText())) {
                this.linearLayout.setVisibility(View.INVISIBLE);
                this.progressBar.setVisibility(View.VISIBLE);
                StarTrackApplication.soc_network_type = 0;
                StarTrackApplication.currentProfile.setSocNetworkType(0);
                ApiHandler.setSocTypeFromPreferences(this,0);
                StarTrackApplication.internal_account_email = this.emailEditText.getText().toString();
                StarTrackApplication.currentProfile.setEmail(this.emailEditText.getText().toString());
                StarTrackApplication.internal_account_password = this.passwordEditText.getText().toString();
                if (currentMode.equals(SignUpOrSignInEmailActivity.SIGN_UP_EXTRA_VALUE)) {
/*            Intent intent = new Intent(this, CreateOrReviewProfileActivity.class);
            intent.putExtra(CreateOrReviewProfileActivity.CREATE_OR_REVIEW_FLAG_EXTRA_NAME, CreateOrReviewProfileActivity.CREATE_EXTRA_VALUE);
            startActivity(intent);*/
                    handler.profileEditingMode = ApiHandler.PROFILE_EDITING_MODE.CREATE;
                    new APIService.GetSessionIdOperation().execute(SignUpOrSignInEmailActivity.this);
                } else if (currentMode.equals(SignUpOrSignInEmailActivity.SIGN_IN_EXTRA_VALUE)) {
/*            new APIService.GetSessionIdOperation().execute();
            new APIService.GetDictionariesOperation().execute();
            new APIService.GetProfileOperation().execute();
            Intent intent = new Intent(this, CreateOrReviewProfileActivity.class);
            intent.putExtra(CreateOrReviewProfileActivity.CREATE_OR_REVIEW_FLAG_EXTRA_NAME, CreateOrReviewProfileActivity.REVIEW_EXTRA_VALUE);
            startActivity(intent);*/
                    handler.profileEditingMode = ApiHandler.PROFILE_EDITING_MODE.DONE;
                    new APIService.GetSessionIdOperation().execute(SignUpOrSignInEmailActivity.this);
                }
            } else {
                Toast.makeText(getBaseContext(), "Incorrect password. Should be more than 6 chars.",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getBaseContext(), "Incorrect email",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public Context getContext() {
        return this;
    }
}
