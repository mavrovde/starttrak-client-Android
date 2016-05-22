package com.startrack.android.startrack_android.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;

import com.startrack.android.startrack_android.R;
import com.startrack.android.startrack_android.activity.IActivityWithHandler;
import com.startrack.android.startrack_android.activity.WelcomeActivity;
import com.startrack.android.startrack_android.api.APIService;
import com.startrack.android.startrack_android.api.ApiHandler;
import com.startrack.android.startrack_android.app.StarTrackApplication;

public class AuthorizationDialog extends Dialog implements android.view.View.OnClickListener {

    private Button closeButton;

    private final Dialog mCurrentDialog = this;

    // FACEBOOK SETTINGS   
    //private static final String FACEBOOK_CLIENT_ID = "631515996986143";
    private static final String FACEBOOK_CLIENT_ID = "659248597511389";
    //private static final String FACEBOOK_CLIENT_SECRET = "639f0585162418a4ad6b261171ee0e74";
    private static final String FACEBOOK_CLIENT_SECRET = "4e8d68b411affc73f0d15b82970b3056";
    private static final String FACEBOOK_SCOPE = "email,public_profile,user_birthday";
    private static final String FACEBOOK_AUTHORIZATION_URL = "https://m.facebook.com/v2.5/dialog/oauth";
    private static final String FACEBOOK_API = "https://graph.facebook.com";
    private static final String FACEBOOK_ACCESS_TOKEN = "v2.5/oauth/access_token";

    // LINKEDIN SETTINGS
    //private static final String LINKEDIN_CLIENT_ID = "77ucdsujtsmgr6";
    private static final String LINKEDIN_CLIENT_ID = "774g998g1hvui6";
    //private static final String LINKEDIN_CLIENT_SECRET = "apshUxdmTeMascgY";
    private static final String LINKEDIN_CLIENT_SECRET = "12WaBJlQxbSndcSI";
    private static final String LINKEDIN_SCOPE = "r_basicprofile,r_emailaddress";
    private static final String LINKEDIN_AUTHORIZATION_URL = "https://www.linkedin.com/uas/oauth2/authorization";
    private static final String LINKEDIN_API = "https://www.linkedin.com";
    private static final String LINKEDIN_ACCESS_TOKEN = "uas/oauth2/accessToken";

    //XING SETTINGS
    public static final String XING_CLIENT_ID = "200498315bfcba1df1dc";
    public static final String XING_CLIENT_SECRET = "c9d77b643643bd940c475ce08deba22c55dd3fa6";
    public static final String XING_SCOPE = ""; //should be empty, because its set up from developer dashboard
    public static final String XING_AUTHORIZATION_URL = "https://api.xing.com/v1/authorize";
    public static final String XING_API = "https://api.xing.com/v1/";
    public static final String XING_ACCESS_TOKEN = "access_token";
    public static final String XING_REQUEST_TOKEN = "request_token";


    public static final String CALLBACK_URL = "http://localhost/li";

    public WebView mWebView;
    public String authorizationUrl;
    private String APIUrl;
    private String mToken;


    private String mLinkedinState;
    private String mAuthorisationCode;

    public static int currentOauthProvider = 0;
    private final int FACEBOOK_ID = 1;
    private final int LINKEDIN_ID = 2;
    private final int XING_ID = 3;

    private WelcomeActivity activity;

    public AuthorizationDialog(final WelcomeActivity activity, ImageButton facebookButton, ImageButton linkedinButton) {
        this(activity);
        this.activity = activity;
        // set onClick listeners and relevant id
        if(facebookButton != null){
            facebookButton.setOnClickListener(this);
        }
        if(linkedinButton != null){
            linkedinButton.setOnClickListener(this);
        }
/*        if(xingButton != null){
            xingButton.setOnClickListener(this);
        }*/
        setupWebview();
        closeButton = (Button) this.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mCurrentDialog.cancel();
                activity.hideProgressAndShowViews();
            }

        });
    }

    public AuthorizationDialog(final WelcomeActivity activity, Button facebookButton, Button linkedinButton) {
        this(activity);
        this.activity = activity;
        // set onClick listeners and relevant id
        if(facebookButton != null){
            facebookButton.setOnClickListener(this);
        }
        if(linkedinButton != null){
            linkedinButton.setOnClickListener(this);
        }
/*        if(xingButton != null){
            xingButton.setOnClickListener(this);
        }*/
        setupWebview();
        closeButton = (Button) this.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mCurrentDialog.cancel();
                activity.hideProgressAndShowViews();
            }

        });
    }

    private void setupWebview() {
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.clearCache(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setWebViewClient(mWebViewClient);
    }

    public AuthorizationDialog(Context context) {
        super(context);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_authorization);
        this.hide();
    }

    @Override
    public void onClick(View v) {
        //mWebView.loadUrl("about:blank");
        this.activity.handler.profileEditingMode = ApiHandler.PROFILE_EDITING_MODE.UPDATE;
            //this.show();
            this.activity.hideViewsAndShowProgress();

            //String authorizationUrl;
            switch (v.getId()) {
                case R.id.SignInWithFacebookButton:
                    authorizationUrl = FACEBOOK_AUTHORIZATION_URL + "?client_id=" +
                            FACEBOOK_CLIENT_ID + "&redirect_uri=" + CALLBACK_URL + "&response_type=code" + "&scope=email,public_profile,user_birthday";
                    currentOauthProvider = FACEBOOK_ID;
                    StarTrackApplication.soc_network_type = 1;
                    break;
                case R.id.SignInWithLinkedinButton:
                    mLinkedinState = "hbvure48h";
                    authorizationUrl = LINKEDIN_AUTHORIZATION_URL + "?client_id=" +
                            LINKEDIN_CLIENT_ID + "&redirect_uri=" + CALLBACK_URL + "&response_type=code&scope=" +
                            LINKEDIN_SCOPE + "&state=" + mLinkedinState;
                    currentOauthProvider = LINKEDIN_ID;
                    StarTrackApplication.soc_network_type = 2;
                    break;
                case R.id.SignInWithXingButton:
/*                    authorizationUrl = XING_AUTHORIZATION_URL + "?client_id=" +
                            XING_CLIENT_ID + "&redirect_uri=" + CALLBACK_URL + "&response_type=code";*/
                    authorizationUrl = "https://api.xing.com/v1/authorize?oauth_token="+StarTrackApplication.xingOauthRequestToken;
                    currentOauthProvider = XING_ID;
                    StarTrackApplication.soc_network_type = 3;
                    new APIService.GetXingRequestToken().execute(this.activity);
                    break;
                default:
                    throw new RuntimeException();
            }
            //open URL for authorisation
            mWebView.loadUrl(authorizationUrl);
    }

    public static String getRandomString() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(50);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if ((url != null) && (url.startsWith(CALLBACK_URL))) { // Override webview when user came back to CALLBACK_URL
                mCurrentDialog.hide();
                GetAccessTokenOperation getAccessTokenOperation = new GetAccessTokenOperation();
                GetXingAccessToken getXingAccessToken = new GetXingAccessToken();
                String code = null;
                String state = null;
                switch (currentOauthProvider){
                    case FACEBOOK_ID:
                        mWebView.stopLoading();
                        mWebView.setVisibility(View.INVISIBLE); // Hide webview if necessary
                        Uri facebookUri = Uri.parse(url);
                        code = facebookUri.getQueryParameter("code");
                        getAccessTokenOperation.execute(code, FACEBOOK_API + "/" + FACEBOOK_ACCESS_TOKEN,
                                CALLBACK_URL, FACEBOOK_CLIENT_ID, FACEBOOK_CLIENT_SECRET);
                        break;
                    case LINKEDIN_ID:
                        List<NameValuePair> parameters;
                        try {
                            parameters = URLEncodedUtils.parse(new URI(url), "UTF-8");
                        } catch (URISyntaxException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            parameters = null;
                        }
                        for (NameValuePair p : parameters) {
                            if(p.getName().equals("state")){
                                state = p.getValue();
                            }
                            if(p.getName().equals("code")){
                                code = p.getValue();
                            }
                        }
                        if(state.equals(mLinkedinState)){
                            getAccessTokenOperation.execute(code, LINKEDIN_API + "/" + LINKEDIN_ACCESS_TOKEN,
                                    CALLBACK_URL, LINKEDIN_CLIENT_ID, LINKEDIN_CLIENT_SECRET);
                        }
                        break;
                    case XING_ID:
                        mWebView.stopLoading();
                        mWebView.setVisibility(View.INVISIBLE); // Hide webview if necessary
                        Uri xingUri = Uri.parse(url);
                        StarTrackApplication.xingOauthVerifier = xingUri.getQueryParameter("oauth_verifier");
                        getXingAccessToken.execute(AuthorizationDialog.this.activity);
                        break;
                }
            } else {
                super.onPageStarted(view, url, favicon);
            }
        }

        private boolean webviewSuccess = true;


        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            webviewSuccess = false;
            Log.d("WEBVIEW","ON PAGE error");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if(webviewSuccess) {
                switch (StarTrackApplication.soc_network_type) {
                    case 1:
                        if (url.contains("facebook")) {
                            AuthorizationDialog.this.show();
                        }
                        break;
                    case 2:
                        if (url.contains("linkedin")) {
                            AuthorizationDialog.this.show();
                        }
                        break;
                    case 3:
                        if (url.contains("xing")) {
                            AuthorizationDialog.this.show();
                        }
                        break;
                }
            }
        }
    };

    private class GetXingAccessToken extends AsyncTask<IActivityWithHandler, Void, Void>{
        @Override
        protected Void doInBackground(IActivityWithHandler... params) {
            try {
                IActivityWithHandler activity = params[0];
                String url = "https://api.xing.com/v1/access_token";
                HttpPost httppost = new HttpPost(url);
                HttpClient httpclient = new DefaultHttpClient();
                ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("oauth_consumer_key", AuthorizationDialog.XING_CLIENT_ID));
                postParameters.add(new BasicNameValuePair("oauth_token", StarTrackApplication.xingOauthRequestToken));
                postParameters.add(new BasicNameValuePair("oauth_verifier", StarTrackApplication.xingOauthVerifier));
                postParameters.add(new BasicNameValuePair("oauth_signature_method", "PLAINTEXT"));
                //postParameters.add(new BasicNameValuePair("oauth_signature", AuthorizationDialog.XING_CLIENT_SECRET+"="));
                postParameters.add(new BasicNameValuePair("oauth_signature", AuthorizationDialog.XING_CLIENT_SECRET+"&"+StarTrackApplication.xingOauthRequestTokenSecret));
                postParameters.add(new BasicNameValuePair("oauth_version", "1.0"));



                httppost.setEntity(new UrlEncodedFormEntity(postParameters));
                HttpResponse response = httpclient.execute(httppost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String responseBody = "";
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    responseBody = responseBody + line;
                }
                reader.close();
                if(response.getStatusLine().getStatusCode()== HttpStatus.SC_CREATED) {
                    StarTrackApplication.oauthToken = responseBody.substring(12, responseBody.indexOf("&oauth_token_secret="));
                    StarTrackApplication.xingOauthRequestTokenSecret = responseBody.substring(responseBody.indexOf("&oauth_token_secret=") + 20, responseBody.indexOf("&user_id="));
                    mCurrentDialog.dismiss();
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString(ApiHandler.AUTHORIZATION_RESULT_MSG_PROPERTY_NAME, ApiHandler.AUTHORIZATION_RESULT_MSG_PROPERTY_VALUE_SUCCESSFUL);
                    msg.setData(data);
                    activity.getHandler().sendMessage(msg);
                } else {
                    mCurrentDialog.dismiss();
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString(ApiHandler.AUTHORIZATION_RESULT_MSG_PROPERTY_NAME, ApiHandler.AUTHORIZATION_RESULT_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                    msg.setData(data);
                    activity.getHandler().sendMessage(msg);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    public void onBackPressed() {
        mCurrentDialog.cancel();
        activity.hideProgressAndShowViews();
        super.onBackPressed();
    }

    private class GetAccessTokenOperation extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String code = params[0];
            String url = params[1];
            String redirectUrl = params[2];
            String clientId = params[3];
            String clientSecret = params[4];
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            HttpResponse response  = null;
            String json = null;
            //String accessToken =  null;
            //String expiresIn = null;
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
                nameValuePairs.add(new BasicNameValuePair("code", code));
                nameValuePairs.add(new BasicNameValuePair("redirect_uri", redirectUrl));
                nameValuePairs.add(new BasicNameValuePair("client_id", clientId));
                nameValuePairs.add(new BasicNameValuePair("client_secret", clientSecret));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpclient.execute(httppost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                json = reader.readLine();
                reader.close();
                if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
                    JSONObject jObject = new JSONObject(json);
                    StarTrackApplication.oauthToken = jObject.getString("access_token");
                    StarTrackApplication.oauthExpiresIn = jObject.getString("expires_in");
                    mCurrentDialog.dismiss();
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString(ApiHandler.AUTHORIZATION_RESULT_MSG_PROPERTY_NAME, ApiHandler.AUTHORIZATION_RESULT_MSG_PROPERTY_VALUE_SUCCESSFUL);
                    msg.setData(data);
                    activity.getHandler().sendMessage(msg);
                    //APIService.login(accessToken);
                } else {
                    mCurrentDialog.dismiss();
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString(ApiHandler.AUTHORIZATION_RESULT_MSG_PROPERTY_NAME, ApiHandler.AUTHORIZATION_RESULT_MSG_PROPERTY_VALUE_SUCCESSFUL);
                    msg.setData(data);
                    activity.getHandler().sendMessage(msg);
                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;

        }
    }
}
