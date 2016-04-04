package com.startrack.android.startrack_android.api;

import java.util.logging.LogRecord;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.startrack.android.startrack_android.activity.CreateOrReviewProfileActivity;
import com.startrack.android.startrack_android.activity.IActivityWithHandler;
import com.startrack.android.startrack_android.activity.SearchActivity;
import com.startrack.android.startrack_android.app.StarTrackApplication;

/**
 * Created by vrogovskiy on 3/1/16.
 */
public class ApiHandler extends Handler {

    public static final String AUTHORIZATION_RESULT_MSG_PROPERTY_NAME = "AuthorizationResult";
    public static final String AUTHORIZATION_RESULT_MSG_PROPERTY_VALUE_SUCCESSFUL = "AUTHORIZATION_SUCCESSFUL";
    public static final String AUTHORIZATION_RESULT_MSG_PROPERTY_VALUE_UNSUCCESSFUL = "AUTHORIZATION_UNSUCCESSFUL";

    public static final String VALIDATING_SESSION_ID_RESULT_MSG_PROPERTY_NAME = "ValidatingSessionIdResult";
    public static final String VALIDATING_SESSION_ID_MSG_PROPERTY_VALUE_SUCCESSFUL = "VALIDATED_SSESSION_ID_SUCCESSFUL";
    public static final String VALIDATING_SESSION_ID_MSG_PROPERTY_VALUE_UNSUCCESSFUL = "VALIDATED_ID_UNSUCCESSFUL";
    public static final String VALIDATING_SESSION_ID_MSG_PROPERTY_VALUE_UNSUCCESSFUL_TOKEN_DOESNT_EXIST = "VALIDATED_ID_UNSUCCESSFUL_TOKEN_DOESNT_EXIST";
    public static final String VALIDATING_SESSION_ID_MSG_PROPERTY_VALUE_UNSUCCESSFUL_TOKEN_EXPIRED = "VALIDATED_ID_UNSUCCESSFUL_TOKEN_EXPIRED";

    public static final String GETTING_SESSION_ID_RESULT_MSG_PROPERTY_NAME = "GettingSessionIdResult";
    public static final String GETTING_SESSION_ID_MSG_PROPERTY_VALUE_SUCCESSFUL = "GOT_SSESSION_ID_SUCCESSFUL";
    public static final String GETTING_SESSION_ID_MSG_PROPERTY_VALUE_UNSUCCESSFUL = "SSESSION_ID_UNSUCCESSFUL";

    public static final String GETTING_SESSION_ID_RESULT_CODE_MSG_PROPERTY_NAME = "GettingSessionIdCodeResult";

    public static final String GETTING_DICTIONARIES_RESULT_MSG_PROPERTY_NAME = "GettingDictionariesResult";
    public static final String GETTING_DICTIONARIES_MSG_PROPERTY_VALUE_SUCCESSFUL = "GOT_DICTIONARIES_SUCCESSFUL";
    public static final String GETTING_DICTIONARIES_MSG_PROPERTY_VALUE_UNSUCCESSFUL = "DICTIONARIES_UNSUCCESSFUL";


    public static final String GETTING_PROFILE_RESULT_MSG_PROPERTY_NAME = "GettingProfileResult";
    public static final String GETTING_PROFILE_MSG_PROPERTY_VALUE_SUCCESSFUL = "GOT_PROFILE_SUCCESSFUL";
    public static final String GETTING_PROFILE_MSG_PROPERTY_VALUE_UNSUCCESSFUL = "PROFILE_UNSUCCESSFUL";

    //XING
    public static final String GETTING_XING_REQUEST_TOKEN_RESULT_MSG_PROPERTY_NAME = "GettingXingRequestTokenResult";
    public static final String GETTING_XING_REQUEST_TOKEN_MSG_PROPERTY_VALUE_SUCCESSFUL = "GOT_TOKEN_SUCCESSFUL";
    public static final String GETTING_XING_REQUEST_TOKEN_MSG_PROPERTY_VALUE_UNSUCCESSFUL = "TOKEN_UNSUCCESSFUL";


    public static final String SESSION_ID_PREFERESERNCE_NAME = "session_id_preference";
    public static final String SOC_TOKEN_PREFERESERNCE_NAME = "soc_token";
    public static final String SOC_TYPE_PREFERESERNCE_NAME = "soc_type_preference";
    public static final String LOGIN_PREFERESERNCE_NAME = "login_preference";
    public static final String PASSWORD_PREFERESERNCE_NAME = "password_preference";
    public static final String PREFERENCES_NAME = "StarTrackPreferences";

    public enum PROFILE_EDITING_MODE {CREATE, UPDATE, DONE};

    public PROFILE_EDITING_MODE profileEditingMode;

    // methods to read or write settings from or to shared preferences
    public static String getLoginFromPreferences(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedpreferences.getString(LOGIN_PREFERESERNCE_NAME, "null");
    }

    public static void setLoginFromPreferences(Context context, String login){
        SharedPreferences sharedpreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(LOGIN_PREFERESERNCE_NAME , login);
        editor.commit();
    }

    public static String getPasswordFromPreferences(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedpreferences.getString(PASSWORD_PREFERESERNCE_NAME, "null");
    }

    public static void setPasswordFromPreferences(Context context, String password){
        SharedPreferences sharedpreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PASSWORD_PREFERESERNCE_NAME , password);
        editor.commit();
    }

    public static String getSessionIdFromPreferences(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedpreferences.getString(SESSION_ID_PREFERESERNCE_NAME, "null");
    }

    public static void setSessionIdFromPreferences(Context context, String sessionId){
        SharedPreferences sharedpreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(SESSION_ID_PREFERESERNCE_NAME, sessionId);
        editor.commit();
    }

    public static int getSocTypeFromPreferences(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedpreferences.getInt(SOC_TYPE_PREFERESERNCE_NAME, -1);
    }

    public static void setSocTypeFromPreferences(Context context, int socType){
        SharedPreferences sharedpreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(SOC_TYPE_PREFERESERNCE_NAME, socType);
        editor.commit();
    }

    public static String getSocTokenFromPreferences(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedpreferences.getString(SOC_TOKEN_PREFERESERNCE_NAME, "null");
    }

    public static void setSocTokenFromPreferences(Context context, String socToken){
        SharedPreferences sharedpreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(SOC_TOKEN_PREFERESERNCE_NAME, socToken);
        editor.commit();
    }


    private IActivityWithHandler activity;


    public ApiHandler(IActivityWithHandler activity){
        super();
        this.activity = activity;
    }




    @Override
    public void handleMessage(Message msg) {
        //VALIDATE SESSION
        if(msg.getData().getString(VALIDATING_SESSION_ID_RESULT_MSG_PROPERTY_NAME) != null){
            if (msg.getData().getString(VALIDATING_SESSION_ID_RESULT_MSG_PROPERTY_NAME).equals(VALIDATING_SESSION_ID_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                profileEditingMode = ApiHandler.PROFILE_EDITING_MODE.DONE;
                new APIService.GetDictionariesOperation().execute(this.activity);
            } else if (msg.getData().getString(VALIDATING_SESSION_ID_RESULT_MSG_PROPERTY_NAME).equals(VALIDATING_SESSION_ID_MSG_PROPERTY_VALUE_UNSUCCESSFUL)){
                this.activity.hideProgressAndShowViews();
                Toast.makeText(this.activity.getActivity(), "Failed to get authorization token",
                        Toast.LENGTH_LONG).show();
            }
        } else

        //AUTHORISATION FROM SOCIAL NETWORK
        if(msg.getData().getString(AUTHORIZATION_RESULT_MSG_PROPERTY_NAME) != null){
            if (msg.getData().getString(AUTHORIZATION_RESULT_MSG_PROPERTY_NAME).equals(AUTHORIZATION_RESULT_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                ApiHandler.setSocTokenFromPreferences(this.activity.getActivity(), StarTrackApplication.oauthToken);
                ApiHandler.setSocTypeFromPreferences(this.activity.getActivity(), StarTrackApplication.soc_network_type);
                new APIService.GetSessionIdOperation().execute(this.activity);
            } else if (msg.getData().getString(AUTHORIZATION_RESULT_MSG_PROPERTY_NAME).equals(AUTHORIZATION_RESULT_MSG_PROPERTY_VALUE_UNSUCCESSFUL)){
                this.activity.hideProgressAndShowViews();
                Toast.makeText(this.activity.getActivity(), "Failed to get authorization token",
                        Toast.LENGTH_LONG).show();
            }
        } else
        //GETTING SESSION ID
        if(msg.getData().getString(GETTING_SESSION_ID_RESULT_MSG_PROPERTY_NAME) != null){
            if (msg.getData().getString(GETTING_SESSION_ID_RESULT_MSG_PROPERTY_NAME).equals(GETTING_SESSION_ID_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                ApiHandler.setSessionIdFromPreferences(this.activity.getActivity(), StarTrackApplication.sessionId);
                new APIService.GetDictionariesOperation().execute(this.activity);
            } else if (msg.getData().getString(GETTING_SESSION_ID_RESULT_MSG_PROPERTY_NAME).equals(GETTING_SESSION_ID_MSG_PROPERTY_VALUE_UNSUCCESSFUL)){
                int resultCode = msg.getData().getInt(GETTING_SESSION_ID_RESULT_CODE_MSG_PROPERTY_NAME);
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
                this.activity.hideProgressAndShowViews();
                Toast.makeText(this.activity.getActivity(), message, Toast.LENGTH_LONG).show();
            }
        } else
        // GETTING DICTIONARIES
        if(msg.getData().getString(GETTING_DICTIONARIES_RESULT_MSG_PROPERTY_NAME) != null){
            if (msg.getData().getString(GETTING_DICTIONARIES_RESULT_MSG_PROPERTY_NAME).equals(GETTING_DICTIONARIES_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                if (StarTrackApplication.soc_network_type>0&&(profileEditingMode == PROFILE_EDITING_MODE.UPDATE)) {
                    new APIService.GetProfileOperation().execute(this.activity);
                } else if(profileEditingMode == PROFILE_EDITING_MODE.CREATE){
                    Intent intent = new Intent(this.activity.getActivity(), CreateOrReviewProfileActivity.class);
                    intent.putExtra(CreateOrReviewProfileActivity.CREATE_OR_REVIEW_FLAG_EXTRA_NAME, CreateOrReviewProfileActivity.CREATE_EXTRA_VALUE);
                    //this.activity.hideProgressAndShowViews();
                    this.activity.getActivity().startActivity(intent);
                    this.activity.getActivity().finish();
                } else {
                    Intent intent = new Intent(this.activity.getActivity(), SearchActivity.class);
                    //this.activity.hideProgressAndShowViews();
                    this.activity.getActivity().startActivity(intent);
                    this.activity.getActivity().finish();
                }

            } else if (msg.getData().getString(GETTING_DICTIONARIES_RESULT_MSG_PROPERTY_NAME).equals(GETTING_DICTIONARIES_MSG_PROPERTY_VALUE_UNSUCCESSFUL)){
                this.activity.hideProgressAndShowViews();
                Toast.makeText(this.activity.getActivity(), "Failed to get dictionaries",
                        Toast.LENGTH_LONG).show();
            }
        } else
        //GETTING PROFILE
        if(msg.getData().getString(GETTING_PROFILE_RESULT_MSG_PROPERTY_NAME) != null){
            if (msg.getData().getString(GETTING_PROFILE_RESULT_MSG_PROPERTY_NAME).equals(GETTING_PROFILE_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                Intent intent = new Intent(this.activity.getActivity(), CreateOrReviewProfileActivity.class);
                intent.putExtra(CreateOrReviewProfileActivity.CREATE_OR_REVIEW_FLAG_EXTRA_NAME, CreateOrReviewProfileActivity.REVIEW_EXTRA_VALUE);
                //this.activity.hideProgressAndShowViews();
                this.activity.getActivity().startActivity(intent);
                if(StarTrackApplication.soc_network_type==0) {
                    this.activity.getActivity().finish();
                }
            } else if (msg.getData().getString(GETTING_PROFILE_RESULT_MSG_PROPERTY_NAME).equals(GETTING_PROFILE_MSG_PROPERTY_VALUE_UNSUCCESSFUL)){
                this.activity.hideProgressAndShowViews();
                Toast.makeText(this.activity.getActivity(), "Failed to get profile",
                        Toast.LENGTH_LONG).show();
            }
        } else if(msg.getData().getString(GETTING_XING_REQUEST_TOKEN_RESULT_MSG_PROPERTY_NAME) != null) {
            if (msg.getData().getString(GETTING_XING_REQUEST_TOKEN_RESULT_MSG_PROPERTY_NAME).equals(GETTING_XING_REQUEST_TOKEN_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                this.activity.showAuthorizarionDialogForXing();
            } else if (msg.getData().getString(GETTING_XING_REQUEST_TOKEN_RESULT_MSG_PROPERTY_NAME).equals(GETTING_XING_REQUEST_TOKEN_MSG_PROPERTY_VALUE_UNSUCCESSFUL)){
                this.activity.hideProgressAndShowViews();
                Toast.makeText(this.activity.getActivity(), "Failed to get xing request token",
                        Toast.LENGTH_LONG).show();
            }

        }
    }


}
