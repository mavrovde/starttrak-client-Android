package com.startrack.android.startrack_android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.startrack.android.startrack_android.R;
import com.startrack.android.startrack_android.api.APIService;
import com.startrack.android.startrack_android.api.ApiHandler;
import com.startrack.android.startrack_android.app.StarTrackApplication;
import com.startrack.android.startrack_android.oauth.AuthorizationDialog;

public class WelcomeActivity extends Activity implements IActivityWithHandler, View.OnClickListener {

    private AuthorizationDialog authorizationDialog;

    private Button facebookButton;
    private Button linkedinButton;
    private Button xingButton;
    private Button signUpWithEmailButton;
    private TextView welcomeTextView;
    private TextView orTextView;
    private TextView accountTextView;
    private ProgressBar progressBar;
    //    private TextView SignUpWithEmailTextView;
//    private TextView SignInWithLinkedinTextView;
//    private TextView SignInWithXingTextView;
//    private TextView SignInWithFacebookTextView;
    private TextView SignIn;
//    private LinearLayout linearLayout;

    public ApiHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        init();
        handler = new ApiHandler(this);

/*        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if(msg.getData().getString(WelcomeActivity.VALIDATING_SESSION_ID_RESULT_MSG_PROPERTY_NAME) != null){
                    if (msg.getData().getString(WelcomeActivity.VALIDATING_SESSION_ID_RESULT_MSG_PROPERTY_NAME).equals(WelcomeActivity.VALIDATING_SESSION_ID_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                        *//*if(!StarTrackApplication.profileExistsFlag) {*//*
                            new APIService.GetDictionariesOperation().execute(WelcomeActivity.this);
*//*                        } else {
                            Intent intent = new Intent(WelcomeActivity.this, SearchActivity.class);
                            //hideProgressAndShowViews();
                            startActivity(intent);
                        }*//*
                    } else if (msg.getData().getString(WelcomeActivity.VALIDATING_SESSION_ID_RESULT_MSG_PROPERTY_NAME).equals(WelcomeActivity.VALIDATING_SESSION_ID_MSG_PROPERTY_VALUE_UNSUCCESSFUL)){
                        hideProgressAndShowViews();
                        Toast.makeText(WelcomeActivity.this, "Failed to get authorization token",
                                Toast.LENGTH_LONG).show();
                    }
                } else
                if(msg.getData().getString(WelcomeActivity.AUTHORIZATION_RESULT_MSG_PROPERTY_NAME) != null){
                    if (msg.getData().getString(WelcomeActivity.AUTHORIZATION_RESULT_MSG_PROPERTY_NAME).equals(WelcomeActivity.AUTHORIZATION_RESULT_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                        WelcomeActivity.setSocTokenFromPreferences(WelcomeActivity.this, StarTrackApplication.oauthToken);
                        WelcomeActivity.setSocTypeFromPreferences(WelcomeActivity.this, StarTrackApplication.soc_network_type);
                        new APIService.GetSessionIdOperation().execute(WelcomeActivity.this);
                    } else if (msg.getData().getString(WelcomeActivity.AUTHORIZATION_RESULT_MSG_PROPERTY_NAME).equals(WelcomeActivity.AUTHORIZATION_RESULT_MSG_PROPERTY_VALUE_UNSUCCESSFUL)){
                        hideProgressAndShowViews();
                        Toast.makeText(WelcomeActivity.this, "Failed to get authorization token",
                                Toast.LENGTH_LONG).show();
                    }
                } else
                if(msg.getData().getString(WelcomeActivity.GETTING_SESSION_ID_RESULT_MSG_PROPERTY_NAME) != null){
                    if (msg.getData().getString(WelcomeActivity.GETTING_SESSION_ID_RESULT_MSG_PROPERTY_NAME).equals(WelcomeActivity.GETTING_SESSION_ID_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                        WelcomeActivity.setSessionIdFromPreferences(WelcomeActivity.this, StarTrackApplication.sessionId);
                        new APIService.GetDictionariesOperation().execute(WelcomeActivity.this);
                    } else if (msg.getData().getString(WelcomeActivity.GETTING_SESSION_ID_RESULT_MSG_PROPERTY_NAME).equals(WelcomeActivity.GETTING_SESSION_ID_MSG_PROPERTY_VALUE_UNSUCCESSFUL)){
                        int resultCode = msg.getData().getInt(WelcomeActivity.GETTING_SESSION_ID_RESULT_CODE_MSG_PROPERTY_NAME);
                        String message = "Failed to get session id";
                        switch(resultCode){
                            case 1000:
                                message = "UNAUTHORIZED";
                                break;
                            case 1001:
                                message = "user exists (email is already taken)";
                                break;
                            case 1002:
                                message = "username/password is not correct";
                                break;
                            case 1003:
                                message = "Social network token expired";
                                break;
                            case 1004:
                                message = "Profile doesn’t exist";
                                break;
                        }
                        hideProgressAndShowViews();
                        Toast.makeText(WelcomeActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                } else
                if(msg.getData().getString(WelcomeActivity.GETTING_DICTIONARIES_RESULT_MSG_PROPERTY_NAME) != null){
                    if (msg.getData().getString(WelcomeActivity.GETTING_DICTIONARIES_RESULT_MSG_PROPERTY_NAME).equals(WelcomeActivity.GETTING_DICTIONARIES_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                        new APIService.GetProfileOperation().execute(WelcomeActivity.this);
                    } else if (msg.getData().getString(WelcomeActivity.GETTING_DICTIONARIES_RESULT_MSG_PROPERTY_NAME).equals(WelcomeActivity.GETTING_DICTIONARIES_MSG_PROPERTY_VALUE_UNSUCCESSFUL)){
                        hideProgressAndShowViews();
                        Toast.makeText(WelcomeActivity.this, "Failed to get dictionaries",
                                Toast.LENGTH_LONG).show();
                    }
                } else
                if(msg.getData().getString(WelcomeActivity.GETTING_PROFILE_RESULT_MSG_PROPERTY_NAME) != null){
                    if (msg.getData().getString(WelcomeActivity.GETTING_PROFILE_RESULT_MSG_PROPERTY_NAME).equals(WelcomeActivity.GETTING_PROFILE_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                        Intent intent = new Intent(WelcomeActivity.this, CreateOrReviewProfileActivity.class);
                        intent.putExtra(CreateOrReviewProfileActivity.CREATE_OR_REVIEW_FLAG_EXTRA_NAME, CreateOrReviewProfileActivity.REVIEW_EXTRA_VALUE);
                        hideProgressAndShowViews();
                        startActivity(intent);
                    } else if (msg.getData().getString(WelcomeActivity.GETTING_PROFILE_RESULT_MSG_PROPERTY_NAME).equals(WelcomeActivity.GETTING_PROFILE_MSG_PROPERTY_VALUE_UNSUCCESSFUL)){
                        hideProgressAndShowViews();
                        Toast.makeText(WelcomeActivity.this, "Failed to get profile",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

        };*/


        //StarTrackApplication.oauthToken = "AQU44QMicY_FY_gmLO0l4Ta2CE8zlHhwOjXwpcFvU8GDl1K8dE66aXvewoVs61T4tAprXCQy8f6yerXVIVButoSiPXKwPwLhjzzQuoXWoWoCaP1Jd5Fsakq_ysE-qlSA77A-Zc6ZKJIe7FgGlwPHlVgN88MlPSqk8X9avMdb6D6jUCNkSBo";
        //StarTrackApplication.soc_network_type = 2;
        //new APIService.GetSessionIdOperation().execute();
        //hideViewsAndShowProgress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        authorizationDialog = new AuthorizationDialog(this, facebookButton, linkedinButton);
        hideProgressAndShowViews();
        String sessionId = ApiHandler.getSessionIdFromPreferences(this);
        int socType = ApiHandler.getSocTypeFromPreferences(this);
        String login = ApiHandler.getLoginFromPreferences(this);
        String password = ApiHandler.getPasswordFromPreferences(this);
        String socToken = ApiHandler.getSocTokenFromPreferences(this);
        if (!StarTrackApplication.allScreensAlwaysForTest) {
            if (!sessionId.equals("null") && socType != (-1)) {
                StarTrackApplication.sessionId = sessionId;
                StarTrackApplication.soc_network_type = socType;
                StarTrackApplication.internal_account_email = login;
                StarTrackApplication.internal_account_password = password;
                StarTrackApplication.oauthToken = socToken;
                hideViewsAndShowProgress();
                new APIService.ValidateSessionIdOperation().execute(WelcomeActivity.this);


/*            if(socType==0&&!login.equals("null")&&!password.equals("null")){
                new APIService.GetSessionIdOperation().execute(WelcomeActivity.this);
            } else if((socType==1 || socType==2 || socType==3)&&!socToken.equals("null")){
                switch(socType){
                    case 1:
                        facebookButton.callOnClick();
                        break;
                    case 2:
                        linkedinButton.callOnClick();
                        break;
                    case 3:
                        xingButton.callOnClick();
                        break;
                }
            }*/
            }
        }
    }

    private void init() {
        this.signUpWithEmailButton = (Button) findViewById(R.id.SignUpWithEmailButton);
        this.facebookButton = (Button) findViewById(R.id.SignInWithFacebookButton);
        this.linkedinButton = (Button) findViewById(R.id.SignInWithLinkedinButton);
        this.xingButton = (Button) findViewById(R.id.SignInWithXingButton);
        this.xingButton.setOnClickListener(this);
        this.welcomeTextView = (TextView) findViewById(R.id.WelcomeTextView);
        this.orTextView = (TextView) findViewById(R.id.OrTextView);
        this.accountTextView = (TextView) findViewById(R.id.accountTextView);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        this.SignUpWithEmailTextView = (TextView) findViewById(R.id.SignUpWithEmailTextView);
//        this.SignInWithLinkedinTextView = (TextView) findViewById(R.id.SignInWithLinkedinTextView);
//        this.SignInWithXingTextView = (TextView) findViewById(R.id.SignInWithXingTextView);
//        this.SignInWithFacebookTextView = (TextView) findViewById(R.id.SignInWithFacebookTextView);
//        this.linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        this.SignIn = (TextView) findViewById(R.id.SignIn);
        this.progressBar.setVisibility(View.INVISIBLE);
        Typeface custom_font_regular = Typeface.createFromAsset(getAssets(), "fonts/Bariol_Regular.otf");
        Typeface custom_font_bold = Typeface.createFromAsset(getAssets(), "fonts/Bariol_Bold.otf");
        this.welcomeTextView.setTypeface(custom_font_regular);
        this.orTextView.setTypeface(custom_font_regular);
        this.accountTextView.setTypeface(custom_font_regular);
        this.signUpWithEmailButton.setTypeface(custom_font_regular);
        this.facebookButton.setTypeface(custom_font_regular);
        this.linkedinButton.setTypeface(custom_font_regular);
        this.xingButton.setTypeface(custom_font_regular);
        this.SignIn.setTypeface(custom_font_bold);
        setupSignInButton();
        setupFbButton();
        setupLnButton();
        setupXingButton();
    }

    private void setupSignInButton() {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_email);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        signUpWithEmailButton.setCompoundDrawables(drawable, null, null, null);
        signUpWithEmailButton.setPadding(
                getResources().getDimensionPixelSize(R.dimen.fb_margin_override_lr), 0, 0, 0);
    }

    private void setupFbButton() {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_fb);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        facebookButton.setCompoundDrawables(drawable, null, null, null);
        facebookButton.setPadding(
                getResources().getDimensionPixelSize(R.dimen.fb_margin_override_lr), 0, 0, 0);
    }

    private void setupLnButton() {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_ln);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        linkedinButton.setCompoundDrawables(drawable, null, null, null);
        linkedinButton.setPadding(
                getResources().getDimensionPixelSize(R.dimen.fb_margin_override_lr), 0, 0, 0);
    }

    private void setupXingButton() {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_xing);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        xingButton.setCompoundDrawables(drawable, null, null, null);
        xingButton.setPadding(
                getResources().getDimensionPixelSize(R.dimen.fb_margin_override_lr), 0, 0, 0);
    }

    public void hideProgressAndShowViews() {
/*        this.SignIn.setVisibility(View.VISIBLE);
        this.signUpWithEmailButton.setVisibility(View.VISIBLE);
        this.facebookButton.setVisibility(View.VISIBLE);
        this.linkedinButton.setVisibility(View.VISIBLE);
        this.xingButton.setVisibility(View.VISIBLE);
        this.welcomeTextView.setVisibility(View.VISIBLE);
        this.orTextView.setVisibility(View.VISIBLE);
        this.accountTextView.setVisibility(View.VISIBLE);*/
//        this.linearLayout.setVisibility(View.VISIBLE);
        this.progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showAuthorizarionDialogForXing() {
        handler.profileEditingMode = ApiHandler.PROFILE_EDITING_MODE.UPDATE;
        authorizationDialog.authorizationUrl = "https://api.xing.com/v1/authorize?oauth_token=" + StarTrackApplication.xingOauthRequestToken;
        StarTrackApplication.soc_network_type = 3;
        authorizationDialog.mWebView.loadUrl(authorizationDialog.authorizationUrl);
        //authorizationDialog.show();
    }

    public void hideViewsAndShowProgress() {
/*        this.SignIn.setVisibility(View.INVISIBLE);
        this.signUpWithEmailButton.setVisibility(View.INVISIBLE);
        this.facebookButton.setVisibility(View.INVISIBLE);
        this.linkedinButton.setVisibility(View.INVISIBLE);
        this.xingButton.setVisibility(View.INVISIBLE);
        this.welcomeTextView.setVisibility(View.INVISIBLE);
        this.orTextView.setVisibility(View.INVISIBLE);
        this.accountTextView.setVisibility(View.INVISIBLE);*/
//        this.linearLayout.setVisibility(View.INVISIBLE);
        this.progressBar.setVisibility(View.VISIBLE);
    }

    public void signUpOnClick(View v) {
        handler.profileEditingMode = ApiHandler.PROFILE_EDITING_MODE.CREATE;
        //hideViewsAndShowProgress();
        //APIService.login("AQWj2GQ0h7qTmB0eDY4_oFdQ0CgTkZ3uxZWwvXLQb12r2_GwWGTYEj69GFG7qLRdHZuRbfEUxN98vyKPtTY2hXd0GXSJvNdEYcm-veBr3o3fvYRtdtWHPzqm8DIt0obgBGWNX7vzqx-kbJCYlTga2RAx7khkw-FZeMHg3zdk_wIlRWOWhPc");
        //new APIService.GetSessionIdOperation().execute(new String());
        Intent intent = new Intent(this, SignUpOrSignInEmailActivity.class);
        intent.putExtra(SignUpOrSignInEmailActivity.SIGN_UP_OR_SIGN_IN_FLAG_EXTRA_NAME, SignUpOrSignInEmailActivity.SIGN_UP_EXTRA_VALUE);
        startActivity(intent);
    }

    public void signInOnClick(View v) {
        handler.profileEditingMode = ApiHandler.PROFILE_EDITING_MODE.DONE;
        //hideViewsAndShowProgress();
        //APIService.login("AQWj2GQ0h7qTmB0eDY4_oFdQ0CgTkZ3uxZWwvXLQb12r2_GwWGTYEj69GFG7qLRdHZuRbfEUxN98vyKPtTY2hXd0GXSJvNdEYcm-veBr3o3fvYRtdtWHPzqm8DIt0obgBGWNX7vzqx-kbJCYlTga2RAx7khkw-FZeMHg3zdk_wIlRWOWhPc");
        //new APIService.GetSessionIdOperation().execute(new String());
        Intent intent = new Intent(this, SignUpOrSignInEmailActivity.class);
        intent.putExtra(SignUpOrSignInEmailActivity.SIGN_UP_OR_SIGN_IN_FLAG_EXTRA_NAME, SignUpOrSignInEmailActivity.SIGN_IN_EXTRA_VALUE);
        startActivity(intent);
    }

/*    public void xingOnClick(View v) {
        hideViewsAndShowProgress();
        new APIService.GetXingRequestToken().execute(this);
    }*/

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onClick(View v) {
        //authorizationDialog.mWebView.loadUrl("about:blank");
        hideViewsAndShowProgress();
        AuthorizationDialog.currentOauthProvider = 3;
        new APIService.GetXingRequestToken().execute(this);
    }
}
