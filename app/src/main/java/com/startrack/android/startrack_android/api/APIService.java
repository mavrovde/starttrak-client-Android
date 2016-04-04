package com.startrack.android.startrack_android.api;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;

import com.startrack.android.startrack_android.activity.CreateOrReviewProfileActivity;
import com.startrack.android.startrack_android.activity.IActivityWithHandler;
import com.startrack.android.startrack_android.activity.MatchesActivity;
import com.startrack.android.startrack_android.activity.SearchActivity;
import com.startrack.android.startrack_android.app.StarTrackApplication;
import com.startrack.android.startrack_android.model.Profile;
import com.startrack.android.startrack_android.oauth.AuthorizationDialog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by vrogovskiy on 2/15/16.
 */
public class APIService {



    // API OPERATION ASYNC TASKS

    public static class GetSessionIdOperation extends AsyncTask<IActivityWithHandler, Void, Void> {
        @Override
        protected Void doInBackground(IActivityWithHandler... params) {
/*            Request Body
            1. Internal
            {soc_network_type:0, email:””, password:””}
            2. Facebook (OAuth2)
            {soc_network_type:1, access_token:””}
            3. LinkedIn (OAuth2)
            {soc_network_type:2, access_token:””}
            4. Xing (OAuth1)
            {soc_network_type:3, oauth_token:””, oauth_token_secret:””}*/
            IActivityWithHandler activity = params[0];
                try {
                    HttpPost httppost = new HttpPost(StarTrackApplication.SERVER_ADRESS + StarTrackApplication.LOGIN_API);
                    HttpClient httpclient = new DefaultHttpClient();
                    String outputJSON = "";
                    if (StarTrackApplication.soc_network_type == 0) {
                        outputJSON = "{\"soc_network_type\":0, \"email\":\"" + StarTrackApplication.internal_account_email + "\", \"password\":\"" + StarTrackApplication.internal_account_password + "\"}";
                    } else if (StarTrackApplication.soc_network_type == 1 || StarTrackApplication.soc_network_type == 2) {
                        outputJSON = "{\"soc_network_type\":" + StarTrackApplication.soc_network_type + ", \"access_token\":\"" + StarTrackApplication.oauthToken + "\"}";
                    } else if (StarTrackApplication.soc_network_type == 3) {
                        outputJSON = "{\"soc_network_type\":" + StarTrackApplication.soc_network_type + ", \"oauth_token\":\"" + StarTrackApplication.oauthToken + "\", " + "\"oauth_token_secret\":\"" + StarTrackApplication.xingOauthRequestTokenSecret + "\"}";
                    }
                    httppost.setHeader("Content-Type", "application/json");
                    HttpEntity entity = new ByteArrayEntity(outputJSON.getBytes("UTF-8"));
                    httppost.setEntity(entity);
                    HttpResponse response = httpclient.execute(httppost);
                    String json = "";
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        json = json + line;
                    }
                    reader.close();
                    if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
                        JSONObject jObject = new JSONObject(json);
                        if (jObject.getInt("code") == 0) {
                            JSONObject contentjObject = jObject.getJSONObject("content");
                            StarTrackApplication.sessionId = contentjObject.getString("session_id");
                            ApiHandler.setSessionIdFromPreferences(activity.getContext(), StarTrackApplication.sessionId);
                            if (contentjObject.has("profile_exists")) {
                                StarTrackApplication.profileExistsFlag = contentjObject.getBoolean("profile_exists");
                            } else {
                                // json doesn't have profile_exists property in the case of social network
                            }
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putString(ApiHandler.GETTING_SESSION_ID_RESULT_MSG_PROPERTY_NAME, ApiHandler.GETTING_SESSION_ID_MSG_PROPERTY_VALUE_SUCCESSFUL);
                            msg.setData(data);
                            activity.getHandler().sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putString(ApiHandler.GETTING_SESSION_ID_RESULT_MSG_PROPERTY_NAME, ApiHandler.GETTING_SESSION_ID_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                            data.putInt(ApiHandler.GETTING_SESSION_ID_RESULT_CODE_MSG_PROPERTY_NAME, jObject.getInt("code"));
                            msg.setData(data);
                            activity.getHandler().sendMessage(msg);
                        }
                    } else {
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString(ApiHandler.GETTING_SESSION_ID_RESULT_MSG_PROPERTY_NAME, ApiHandler.GETTING_SESSION_ID_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                        data.putInt(ApiHandler.GETTING_SESSION_ID_RESULT_CODE_MSG_PROPERTY_NAME, response.getStatusLine().getStatusCode());
                        msg.setData(data);
                        activity.getHandler().sendMessage(msg);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString(ApiHandler.GETTING_SESSION_ID_RESULT_MSG_PROPERTY_NAME, ApiHandler.GETTING_SESSION_ID_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                    data.putInt(ApiHandler.GETTING_SESSION_ID_RESULT_CODE_MSG_PROPERTY_NAME, (-1));
                    msg.setData(data);
                    activity.getHandler().sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString(ApiHandler.GETTING_SESSION_ID_RESULT_MSG_PROPERTY_NAME, ApiHandler.GETTING_SESSION_ID_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                    data.putInt(ApiHandler.GETTING_SESSION_ID_RESULT_CODE_MSG_PROPERTY_NAME, (-2));
                    msg.setData(data);
                    activity.getHandler().sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString(ApiHandler.GETTING_SESSION_ID_RESULT_MSG_PROPERTY_NAME, ApiHandler.GETTING_SESSION_ID_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                    data.putInt(ApiHandler.GETTING_SESSION_ID_RESULT_CODE_MSG_PROPERTY_NAME, (-3));
                    msg.setData(data);
                    activity.getHandler().sendMessage(msg);
                }

            return null;

        }
    }

    public static class ValidateSessionIdOperation extends AsyncTask<IActivityWithHandler, Void, Void> {
        @Override
        protected Void doInBackground(IActivityWithHandler... params) {
            IActivityWithHandler activity = params[0];
            try {
                HttpGet httpGet = new HttpGet(StarTrackApplication.SERVER_ADRESS+StarTrackApplication.VALIDATE_SESSION_TOKEN_API + "?session_id=" + StarTrackApplication.sessionId);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httpGet);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String json = "";
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    json = json + line;
                }
                reader.close();
                if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
                    JSONObject jObject = new JSONObject(json);
                    if (jObject.getInt("code") == 0) {
                        JSONObject contentjObject = jObject.getJSONObject("content");
                        if (contentjObject.has("profile_exists")) {
                            StarTrackApplication.profileExistsFlag = contentjObject.getBoolean("profile_exists");
                        } else {
                            StarTrackApplication.profileExistsFlag = true;
                        }
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString(ApiHandler.VALIDATING_SESSION_ID_RESULT_MSG_PROPERTY_NAME, ApiHandler.VALIDATING_SESSION_ID_MSG_PROPERTY_VALUE_SUCCESSFUL);
                        msg.setData(data);
                        activity.getHandler().sendMessage(msg);
                    } else if (jObject.getInt("code") == 1008) {
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString(ApiHandler.VALIDATING_SESSION_ID_RESULT_MSG_PROPERTY_NAME, ApiHandler.VALIDATING_SESSION_ID_MSG_PROPERTY_VALUE_UNSUCCESSFUL_TOKEN_EXPIRED);
                        msg.setData(data);
                        activity.getHandler().sendMessage(msg);
                    } else if (jObject.getInt("code") == 1007) {
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString(ApiHandler.VALIDATING_SESSION_ID_RESULT_MSG_PROPERTY_NAME, ApiHandler.VALIDATING_SESSION_ID_MSG_PROPERTY_VALUE_UNSUCCESSFUL_TOKEN_DOESNT_EXIST);
                        msg.setData(data);
                        activity.getHandler().sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString(ApiHandler.VALIDATING_SESSION_ID_RESULT_MSG_PROPERTY_NAME, ApiHandler.VALIDATING_SESSION_ID_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                        msg.setData(data);
                        activity.getHandler().sendMessage(msg);
                    }
                } else {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString(ApiHandler.VALIDATING_SESSION_ID_RESULT_MSG_PROPERTY_NAME, ApiHandler.VALIDATING_SESSION_ID_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                    msg.setData(data);
                    activity.getHandler().sendMessage(msg);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }
    }

    public static class GetDictionariesOperation extends AsyncTask<IActivityWithHandler, Void, Void> {
        @Override
        protected Void doInBackground(IActivityWithHandler... params) {
            try {
                IActivityWithHandler activity = params[0];
                    HttpGet httpGet = new HttpGet(StarTrackApplication.SERVER_ADRESS + StarTrackApplication.GET_DICTIONARIES_API);
                    addSessionId(httpGet);
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse response = httpclient.execute(httpGet);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    String json = "";
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        json = json + line;
                    }
                    reader.close();
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        JSONObject jObject = new JSONObject(json);
                        if (jObject.getInt("code") == 0) {
                            JSONObject contentJSONObject = jObject.getJSONObject("content");
                            parseDictionaries(contentJSONObject);
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putString(ApiHandler.GETTING_DICTIONARIES_RESULT_MSG_PROPERTY_NAME, ApiHandler.GETTING_DICTIONARIES_MSG_PROPERTY_VALUE_SUCCESSFUL);
                            msg.setData(data);
                            activity.getHandler().sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putString(ApiHandler.GETTING_DICTIONARIES_RESULT_MSG_PROPERTY_NAME, ApiHandler.GETTING_DICTIONARIES_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                            msg.setData(data);
                            activity.getHandler().sendMessage(msg);
                        }
                    } else {
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString(ApiHandler.GETTING_DICTIONARIES_RESULT_MSG_PROPERTY_NAME, ApiHandler.GETTING_DICTIONARIES_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                        msg.setData(data);
                        activity.getHandler().sendMessage(msg);
                    }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }
    }

    public static class CreateProfileOperation extends AsyncTask<IActivityWithHandler, Void, Void> {
        @Override
        protected Void doInBackground(IActivityWithHandler... params) {
/*            Mandatory fields:
            email

            Optional fields:
            first_name
            last_name
            phone_number
            company_name
            title_id (from dictionaries)
            seniority_id
            industry_id
            country_id
            state_id (only for USA)
            size_id (company sizes)*/
            IActivityWithHandler activity = params[0];

            try {
                HttpPost httppost = new HttpPost(StarTrackApplication.SERVER_ADRESS+StarTrackApplication.PROFILE_API);
                HttpClient httpclient = new DefaultHttpClient();
                addSessionId(httppost);
                //StarTrackApplication.currentProfile = createDummyProfile();
                String outputJSON = createOutputJSONForCreateProfile(true);
                HttpEntity entity = new ByteArrayEntity(outputJSON .getBytes("UTF-8"));
                httppost.setHeader("Content-Type", "application/json");
                httppost.setEntity(entity);
                HttpResponse response = httpclient.execute(httppost);
                String json = "";
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    json = json + line;
                }
                reader.close();
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    JSONObject jObject = new JSONObject(json);
                    if (jObject.getInt("code") == 0) {
                        JSONObject contentJSONObject = jObject.getJSONObject("content");
                        StarTrackApplication.currentProfile = new Profile(contentJSONObject);
                        //parceProfileObject(contentJSONObject);
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString(CreateOrReviewProfileActivity.CREATING_PROFILE_RESULT_MSG_PROPERTY_NAME, CreateOrReviewProfileActivity.CREATING_PROFILE_MSG_PROPERTY_VALUE_SUCCESSFUL);
                        msg.setData(data);
                        activity.getHandler().sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString(CreateOrReviewProfileActivity.CREATING_PROFILE_RESULT_MSG_PROPERTY_NAME, CreateOrReviewProfileActivity.CREATING_PROFILE_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                        msg.setData(data);
                        activity.getHandler().sendMessage(msg);
                    }
                } else {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString(CreateOrReviewProfileActivity.CREATING_PROFILE_RESULT_MSG_PROPERTY_NAME, CreateOrReviewProfileActivity.CREATING_PROFILE_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                    msg.setData(data);
                    activity.getHandler().sendMessage(msg);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
               e.printStackTrace();
        }

        return null;

        }
    }

    public static class GetProfileOperation extends AsyncTask<IActivityWithHandler, Void, Void> {
        @Override
        protected Void doInBackground(IActivityWithHandler... params) {
            try {
                IActivityWithHandler activity = params[0];
                    String json = "";
                    HttpGet httpGet = new HttpGet(StarTrackApplication.SERVER_ADRESS + StarTrackApplication.PROFILE_API);
                    addSessionId(httpGet);
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse response = httpclient.execute(httpGet);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        json = json + line;
                    }
                    reader.close();
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    JSONObject jObject = new JSONObject(json);
                    if (jObject.getInt("code") == 0) {
                        JSONObject contentJSONObject = jObject.getJSONObject("content");
                        StarTrackApplication.currentProfile = new Profile(contentJSONObject);
                        //parceProfileObject(contentJSONObject);
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString(ApiHandler.GETTING_PROFILE_RESULT_MSG_PROPERTY_NAME, ApiHandler.GETTING_PROFILE_MSG_PROPERTY_VALUE_SUCCESSFUL);
                        msg.setData(data);
                        activity.getHandler().sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString(ApiHandler.GETTING_PROFILE_RESULT_MSG_PROPERTY_NAME, ApiHandler.GETTING_PROFILE_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                        msg.setData(data);
                        activity.getHandler().sendMessage(msg);
                    }
                } else {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString(ApiHandler.GETTING_PROFILE_RESULT_MSG_PROPERTY_NAME, ApiHandler.GETTING_PROFILE_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                    msg.setData(data);
                    activity.getHandler().sendMessage(msg);
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }
    }

    public static class UpdateProfileOperation extends AsyncTask<IActivityWithHandler, Void, Void> {
        @Override
        protected Void doInBackground(IActivityWithHandler... params) {
/*            Mandatory fields:
            email

            Optional fields:
            first_name
            last_name
            phone_number
            company_name
            title_id (from dictionaries)
            seniority_id
            industry_id
            country_id
            state_id (only for USA)
            size_id (company sizes)*/

            IActivityWithHandler activity = params[0];

            try {
                HttpPut httpput = new HttpPut(StarTrackApplication.SERVER_ADRESS + StarTrackApplication.PROFILE_API);
                HttpClient httpclient = new DefaultHttpClient();
                addSessionId(httpput);
                //StarTrackApplication.currentProfile = createDummyProfile(0);
                String outputJSON = createOutputJSONForCreateProfile(true);
                HttpEntity entity = new ByteArrayEntity(outputJSON.getBytes("UTF-8"));
                httpput.setHeader("Content-Type", "application/json");
                httpput.setEntity(entity);
                HttpResponse response = httpclient.execute(httpput);
                String json = "";
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    json = json + line;
                }
                reader.close();
                if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
                    JSONObject jObject = new JSONObject(json);
                    if (jObject.getInt("code") == 0) {
                        JSONObject contentJSONObject = jObject.getJSONObject("content");
                        StarTrackApplication.currentProfile = new Profile(contentJSONObject);
                        //parceProfileObject(contentJSONObject);
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString(CreateOrReviewProfileActivity.UPDATING_PROFILE_RESULT_MSG_PROPERTY_NAME, CreateOrReviewProfileActivity.UPDATING_PROFILE_MSG_PROPERTY_VALUE_SUCCESSFUL);
                        msg.setData(data);
                        activity.getHandler().sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString(CreateOrReviewProfileActivity.UPDATING_PROFILE_RESULT_MSG_PROPERTY_NAME, CreateOrReviewProfileActivity.UPDATING_PROFILE_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                        msg.setData(data);
                        activity.getHandler().sendMessage(msg);
                    }
                } else {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString(CreateOrReviewProfileActivity.UPDATING_PROFILE_RESULT_MSG_PROPERTY_NAME, CreateOrReviewProfileActivity.UPDATING_PROFILE_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                    msg.setData(data);
                    activity.getHandler().sendMessage(msg);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }
    }

/*    public static class DeleteProfileOperation {

    }*/

    // XING Authorisation


/*
    curl --request POST --verbose "https://api.xing.com/v1/request_token" -d "oauth_callback=oob" -d "oauth_version=1.0" -d "oauth_signature_method=PLAINTEXT"  -d "oauth_consumer_key=4cbc7615d6bfcb8d597e" -d "oauth_signature=817043f7325e2cf287ad462b6d93431ce8ee231b%26"
*/

    public static class GetXingRequestToken extends AsyncTask<IActivityWithHandler, Void, Void>{
        @Override
        protected Void doInBackground(IActivityWithHandler... params) {
            try {
                IActivityWithHandler activity = params[0];
                String url = "https://api.xing.com/v1/request_token";
                HttpPost httppost = new HttpPost(url);
                HttpClient httpclient = new DefaultHttpClient();
                ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("oauth_consumer_key", AuthorizationDialog.XING_CLIENT_ID));
                postParameters.add(new BasicNameValuePair("oauth_version", "1.0"));
                postParameters.add(new BasicNameValuePair("oauth_callback", AuthorizationDialog. CALLBACK_URL));
                postParameters.add(new BasicNameValuePair("oauth_signature_method", "PLAINTEXT"));
                postParameters.add(new BasicNameValuePair("oauth_signature", AuthorizationDialog.XING_CLIENT_SECRET+"&"));
                httppost.setEntity(new UrlEncodedFormEntity(postParameters));
                HttpResponse response = httpclient.execute(httppost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String responseBody = "";
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    responseBody = responseBody + line;
                }
                reader.close();
                if(response.getStatusLine().getStatusCode()== HttpStatus.SC_CREATED) {
                    StarTrackApplication.xingOauthRequestToken = responseBody.substring(12, responseBody.indexOf("&oauth_token_secret="));
                    StarTrackApplication.xingOauthRequestTokenSecret = responseBody.substring(responseBody.indexOf("&oauth_token_secret=") + 20, responseBody.indexOf("&oauth_callback_confirmed="));
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString(ApiHandler.GETTING_XING_REQUEST_TOKEN_RESULT_MSG_PROPERTY_NAME, ApiHandler.GETTING_XING_REQUEST_TOKEN_MSG_PROPERTY_VALUE_SUCCESSFUL);
                    msg.setData(data);
                    activity.getHandler().sendMessage(msg);
                } else {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString(ApiHandler.GETTING_XING_REQUEST_TOKEN_RESULT_MSG_PROPERTY_NAME, ApiHandler.GETTING_XING_REQUEST_TOKEN_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                    msg.setData(data);
                    activity.getHandler().sendMessage(msg);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

/*    public static class GetXingAccessToken extends AsyncTask<IActivityWithHandler, Void, Void>{
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
                postParameters.add(new BasicNameValuePair("oauth_signature", AuthorizationDialog.XING_CLIENT_SECRET+"&"));




                httppost.setEntity(new UrlEncodedFormEntity(postParameters));
                HttpResponse response = httpclient.execute(httppost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String responseBody = "";
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    responseBody = responseBody + line;
                }
                reader.close();
                if(response.getStatusLine().getStatusCode()== HttpStatus.SC_CREATED) {
                    StarTrackApplication.xingOauthRequestToken = responseBody.substring(12, responseBody.indexOf("&oauth_token_secret="));
                    StarTrackApplication.xingOauthRequestTokenSecret = responseBody.substring(responseBody.indexOf("&oauth_token_secret=") + 20, responseBody.indexOf("&oauth_callback_confirmed="));
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString(ApiHandler.GETTING_XING_REQUEST_TOKEN_RESULT_MSG_PROPERTY_NAME, ApiHandler.GETTING_XING_REQUEST_TOKEN_MSG_PROPERTY_VALUE_SUCCESSFUL);
                    msg.setData(data);
                    activity.getHandler().sendMessage(msg);
                } else {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString(ApiHandler.GETTING_XING_REQUEST_TOKEN_RESULT_MSG_PROPERTY_NAME, ApiHandler.GETTING_XING_REQUEST_TOKEN_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                    msg.setData(data);
                    activity.getHandler().sendMessage(msg);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }*/


    public static class SearchOperation  extends AsyncTask<IActivityWithHandler, Void, Void> {
        @Override
        protected Void doInBackground(IActivityWithHandler... params) {
/*            Mandatory fields:
            email

            Optional fields:
            first_name
            last_name
            phone_number
            company_name
            title_id (from dictionaries)
            seniority_id
            industry_id
            country_id
            state_id (only for USA)
            size_id (company sizes)*/
            IActivityWithHandler activity = params[0];
            try {
                HttpPost httppost = new HttpPost(StarTrackApplication.SERVER_ADRESS + StarTrackApplication.SEARCH_API);
                HttpClient httpclient = new DefaultHttpClient();
                addSessionId(httppost);
/*
                {
                    search_text : “something”,
                    size_id:<...>,
                    seniority_id:<...>,
                    industry_id:<...>,
                    country_id:<...>,
                    position_id:<...>,
                }*/
                String outputJSON = createOutputJSONForSearchOperation();
                HttpEntity entity = new ByteArrayEntity(outputJSON.getBytes("UTF-8"));
                httppost.setHeader("Content-Type", "application/json");
                httppost.setEntity(entity);
                HttpResponse response = httpclient.execute(httppost);
                String json = "";
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    json = json + line;
                }
                reader.close();
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    JSONObject jObject = new JSONObject(json);
                    if (jObject.getInt("code") == 0) {
                        JSONArray contentJSONArray = jObject.getJSONArray("content");
                        for (int i = 0; i < contentJSONArray.length(); i++) {
                            StarTrackApplication.searchResults.add(new Profile(contentJSONArray.getJSONObject(i)));
                        }
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString(SearchActivity.SEARCHING_RESULT_MSG_PROPERTY_NAME, SearchActivity.SEARCHING_MSG_PROPERTY_VALUE_SUCCESSFUL);
                        msg.setData(data);
                        activity.getHandler().sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString(SearchActivity.SEARCHING_RESULT_MSG_PROPERTY_NAME, SearchActivity.SEARCHING_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                        msg.setData(data);
                        activity.getHandler().sendMessage(msg);
                    }
                } else {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString(SearchActivity.SEARCHING_RESULT_MSG_PROPERTY_NAME, SearchActivity.SEARCHING_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                    msg.setData(data);
                    activity.getHandler().sendMessage(msg);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }
    }

    public static class MeetOperation  extends AsyncTask<IActivityWithHandler, Void, Void> {
        @Override
        protected Void doInBackground(IActivityWithHandler... params) {
/*           Request Body :
{
user_ids: [] - array of user ids
}
Response:
{
"status":1,"msg":"bla-bla", “content” : {}
}*/

            try {
                IActivityWithHandler activity = params[0];
                    HttpPost httppost = new HttpPost(StarTrackApplication.SERVER_ADRESS + StarTrackApplication.MEET_API);
                    HttpClient httpclient = new DefaultHttpClient();
                    addSessionId(httppost);
/*
                {
                    search_text : “something”,
                    size_id:<...>,
                    seniority_id:<...>,
                    industry_id:<...>,
                    country_id:<...>,
                    position_id:<...>,
                }*/
                    String outputJSON = createOutputJSONForMeet();
                    HttpEntity entity = new ByteArrayEntity(outputJSON.getBytes("UTF-8"));
                    httppost.setHeader("Content-Type", "application/json");
                    httppost.setEntity(entity);
                    HttpResponse response = httpclient.execute(httppost);
                    String json = "";
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        json = json + line;
                    }
                    reader.close();
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    JSONObject jObject = new JSONObject(json);
                    if (jObject.getInt("code") == 0) {
                        //JSONObject contentJSONObject = jObject.getJSONObject("content");
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString(MatchesActivity.MEET_RESULT_MSG_PROPERTY_NAME, MatchesActivity.MEET_RESULT_MSG_PROPERTY_VALUE_SUCCESSFUL);
                        msg.setData(data);
                        activity.getHandler().sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString(MatchesActivity.MEET_RESULT_MSG_PROPERTY_NAME, MatchesActivity.MEET_RESULT_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                        msg.setData(data);
                        activity.getHandler().sendMessage(msg);
                    }
                } else {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString(MatchesActivity.MEET_RESULT_MSG_PROPERTY_NAME, MatchesActivity.MEET_RESULT_MSG_PROPERTY_VALUE_UNSUCCESSFUL);
                    msg.setData(data);
                    activity.getHandler().sendMessage(msg);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }
    }

    //TEST METHODS
 /*   private static void fillProfileWithTestData(){

    }

    private static void fillDictionariesWithTestData(){

    }*/

    private static String DICTIONARIES_TEST_CONTENT_JSON_STRING = "{\"sizes\":[{\"id\":1,\"label\":\"Self-employed\",\"minValue\":0,\"maxValue\":1},{\"id\":2,\"label\":\"1-10 employees\",\"minValue\":1,\"maxValue\":10},{\"id\":3,\"label\":\"11-50 employees\",\"minValue\":11,\"maxValue\":50},{\"id\":4,\"label\":\"51-200 employees\",\"minValue\":51,\"maxValue\":200},{\"id\":5,\"label\":\"201-500 employees\",\"minValue\":201,\"maxValue\":500},{\"id\":6,\"label\":\"501-1000 employees\",\"minValue\":501,\"maxValue\":1000},{\"id\":7,\"label\":\"1001-5000 employees\",\"minValue\":1001,\"maxValue\":5000},{\"id\":8,\"label\":\"5001-10000 employees\",\"minValue\":5001,\"maxValue\":10000},{\"id\":9,\"label\":\"10001+ employees\",\"minValue\":10001,\"maxValue\":200000}],\"industries\":[{\"id\":47,\"label\":\"Accounting\"},{\"id\":94,\"label\":\"Airlines\\/Aviation\"},{\"id\":120,\"label\":\"Alternative Dispute Resolution\"},{\"id\":125,\"label\":\"Alternative Medicine\"},{\"id\":127,\"label\":\"Animation\"},{\"id\":19,\"label\":\"Apparel & Fashion\"},{\"id\":50,\"label\":\"Architecture & Planning\"},{\"id\":111,\"label\":\"Arts and Crafts\"},{\"id\":53,\"label\":\"Automotive\"},{\"id\":52,\"label\":\"Aviation & Aerospace\"},{\"id\":41,\"label\":\"Banking\"},{\"id\":12,\"label\":\"Biotechnology\"},{\"id\":36,\"label\":\"Broadcast Media\"},{\"id\":49,\"label\":\"Building Materials\"},{\"id\":138,\"label\":\"Business Supplies and Equipment\"},{\"id\":129,\"label\":\"Capital Markets\"},{\"id\":54,\"label\":\"Chemicals\"},{\"id\":90,\"label\":\"Civic & Social Organization\"},{\"id\":51,\"label\":\"Civil Engineering\"},{\"id\":128,\"label\":\"Commercial Real Estate\"},{\"id\":118,\"label\":\"Computer & Network Security\"},{\"id\":109,\"label\":\"Computer Games\"},{\"id\":3,\"label\":\"Computer Hardware\"},{\"id\":5,\"label\":\"Computer Networking\"},{\"id\":4,\"label\":\"Computer Software\"},{\"id\":48,\"label\":\"Construction\"},{\"id\":24,\"label\":\"Consumer Electronics\"},{\"id\":25,\"label\":\"Consumer Goods\"},{\"id\":91,\"label\":\"Consumer Services\"},{\"id\":18,\"label\":\"Cosmetics\"},{\"id\":65,\"label\":\"Dairy\"},{\"id\":1,\"label\":\"Defense & Space\"},{\"id\":99,\"label\":\"Design\"},{\"id\":132,\"label\":\"E-Learning\"},{\"id\":69,\"label\":\"Education Management\"},{\"id\":112,\"label\":\"Electrical\\/Electronic Manufacturing\"},{\"id\":28,\"label\":\"Entertainment\"},{\"id\":86,\"label\":\"Environmental Services\"},{\"id\":110,\"label\":\"Events Services\"},{\"id\":76,\"label\":\"Executive Office\"},{\"id\":122,\"label\":\"Facilities Services\"},{\"id\":63,\"label\":\"Farming\"},{\"id\":43,\"label\":\"Financial Services\"},{\"id\":38,\"label\":\"Fine Art\"},{\"id\":66,\"label\":\"Fishery\"},{\"id\":34,\"label\":\"Food & Beverages\"},{\"id\":23,\"label\":\"Food Production\"},{\"id\":101,\"label\":\"Fund-Raising\"},{\"id\":26,\"label\":\"Furniture\"},{\"id\":29,\"label\":\"Gambling & Casinos\"},{\"id\":145,\"label\":\"Glass, Ceramics & Concrete\"},{\"id\":75,\"label\":\"Government Administration\"},{\"id\":148,\"label\":\"Government Relations\"},{\"id\":140,\"label\":\"Graphic Design\"},{\"id\":124,\"label\":\"Health, Wellness and Fitness\"},{\"id\":68,\"label\":\"Higher Education\"},{\"id\":14,\"label\":\"Hospital & Health Care\"},{\"id\":31,\"label\":\"Hospitality\"},{\"id\":137,\"label\":\"Human Resources\"},{\"id\":134,\"label\":\"Import and Export\"},{\"id\":88,\"label\":\"Individual & Family Services\"},{\"id\":147,\"label\":\"Industrial Automation\"},{\"id\":84,\"label\":\"Information Services\"},{\"id\":96,\"label\":\"Information Technology and Services\"},{\"id\":42,\"label\":\"Insurance\"},{\"id\":74,\"label\":\"International Affairs\"},{\"id\":141,\"label\":\"International Trade and Development\"},{\"id\":6,\"label\":\"Internet\"},{\"id\":45,\"label\":\"Investment Banking\"},{\"id\":46,\"label\":\"Investment Management\"},{\"id\":73,\"label\":\"Judiciary\"},{\"id\":77,\"label\":\"Law Enforcement\"},{\"id\":9,\"label\":\"Law Practice\"},{\"id\":10,\"label\":\"Legal Services\"},{\"id\":72,\"label\":\"Legislative Office\"},{\"id\":30,\"label\":\"Leisure, Travel & Tourism\"},{\"id\":85,\"label\":\"Libraries\"},{\"id\":116,\"label\":\"Logistics and Supply Chain\"},{\"id\":143,\"label\":\"Luxury Goods & Jewelry\"},{\"id\":55,\"label\":\"Machinery\"},{\"id\":11,\"label\":\"Management Consulting\"},{\"id\":95,\"label\":\"Maritime\"},{\"id\":97,\"label\":\"Market Research\"},{\"id\":80,\"label\":\"Marketing and Advertising\"},{\"id\":135,\"label\":\"Mechanical or Industrial Engineering\"},{\"id\":126,\"label\":\"Media Production\"},{\"id\":17,\"label\":\"Medical Devices\"},{\"id\":13,\"label\":\"Medical Practice\"},{\"id\":139,\"label\":\"Mental Health Care\"},{\"id\":71,\"label\":\"Military\"},{\"id\":56,\"label\":\"Mining & Metals\"},{\"id\":35,\"label\":\"Motion Pictures and Film\"},{\"id\":37,\"label\":\"Museums and Institutions\"},{\"id\":115,\"label\":\"Music\"},{\"id\":114,\"label\":\"Nanotechnology\"},{\"id\":81,\"label\":\"Newspapers\"},{\"id\":100,\"label\":\"Non-Profit Organization Management\"},{\"id\":57,\"label\":\"Oil & Energy\"},{\"id\":113,\"label\":\"Online Media\"},{\"id\":123,\"label\":\"Outsourcing\\/Offshoring\"},{\"id\":87,\"label\":\"Package\\/Freight Delivery\"},{\"id\":146,\"label\":\"Packaging and Containers\"},{\"id\":61,\"label\":\"Paper & Forest Products\"},{\"id\":39,\"label\":\"Performing Arts\"},{\"id\":15,\"label\":\"Pharmaceuticals\"},{\"id\":131,\"label\":\"Philanthropy\"},{\"id\":136,\"label\":\"Photography\"},{\"id\":117,\"label\":\"Plastics\"},{\"id\":107,\"label\":\"Political Organization\"},{\"id\":67,\"label\":\"Primary\\/Secondary Education\"},{\"id\":83,\"label\":\"Printing\"},{\"id\":105,\"label\":\"Professional Training & Coaching\"},{\"id\":102,\"label\":\"Program Development\"},{\"id\":79,\"label\":\"Public Policy\"},{\"id\":98,\"label\":\"Public Relations and Communications\"},{\"id\":78,\"label\":\"Public Safety\"},{\"id\":82,\"label\":\"Publishing\"},{\"id\":62,\"label\":\"Railroad Manufacture\"},{\"id\":64,\"label\":\"Ranching\"},{\"id\":44,\"label\":\"Real Estate\"},{\"id\":40,\"label\":\"Recreational Facilities and Services\"},{\"id\":89,\"label\":\"Religious Institutions\"},{\"id\":144,\"label\":\"Renewables & Environment\"},{\"id\":70,\"label\":\"Research\"},{\"id\":32,\"label\":\"Restaurants\"},{\"id\":27,\"label\":\"Retail\"},{\"id\":121,\"label\":\"Security and Investigations\"},{\"id\":7,\"label\":\"Semiconductors\"},{\"id\":58,\"label\":\"Shipbuilding\"},{\"id\":20,\"label\":\"Sporting Goods\"},{\"id\":33,\"label\":\"Sports\"},{\"id\":104,\"label\":\"Staffing and Recruiting\"},{\"id\":22,\"label\":\"Supermarkets\"},{\"id\":8,\"label\":\"Telecommunications\"},{\"id\":60,\"label\":\"Textiles\"},{\"id\":130,\"label\":\"Think Tanks\"},{\"id\":21,\"label\":\"Tobacco\"},{\"id\":108,\"label\":\"Translation and Localization\"},{\"id\":92,\"label\":\"Transportation\\/Trucking\\/Railroad\"},{\"id\":59,\"label\":\"Utilities\"},{\"id\":106,\"label\":\"Venture Capital & Private Equity\"},{\"id\":16,\"label\":\"Veterinary\"},{\"id\":93,\"label\":\"Warehousing\"},{\"id\":133,\"label\":\"Wholesale\"},{\"id\":142,\"label\":\"Wine and Spirits\"},{\"id\":119,\"label\":\"Wireless\"},{\"id\":103,\"label\":\"Writing and Editing\"}], \"positions\":[{\"id\":1,\"label\":\"Accountant\"},{\"id\":2,\"label\":\"Accountant Systems\"},{\"id\":3,\"label\":\"Acquisition Management Intern\"},{\"id\":4,\"label\":\"Actuarial Analyst\"},{\"id\":5,\"label\":\"Actuary\"},{\"id\":6,\"label\":\"Administrative Generalist\\/Specialist\"},{\"id\":7,\"label\":\"Affordable Housing Specialist\"},{\"id\":8,\"label\":\"Analyst\"},{\"id\":9,\"label\":\"Appraiser\"},{\"id\":10,\"label\":\"Archaeologist\"},{\"id\":11,\"label\":\"Area Systems Coordinator\"},{\"id\":12,\"label\":\"Assistant\"},{\"id\":13,\"label\":\"Asylum or Immigration Officer\"},{\"id\":14,\"label\":\"Attorney\\/Law Clerk\"},{\"id\":15,\"label\":\"Audience Analyst\"},{\"id\":16,\"label\":\"Audit Resolution Follow Up\"},{\"id\":17,\"label\":\"Auditor\"},{\"id\":18,\"label\":\"Behavioral Scientist\"},{\"id\":19,\"label\":\"Biologist, Fishery\"},{\"id\":20,\"label\":\"Biologist, Marine\"},{\"id\":21,\"label\":\"Biologist, Wildlife\"},{\"id\":22,\"label\":\"Budget Analyst\"},{\"id\":23,\"label\":\"Budget Specialist\"},{\"id\":24,\"label\":\"Business Administration Officer\"},{\"id\":25,\"label\":\"Chemical Engineer\"},{\"id\":26,\"label\":\"Chemist\"},{\"id\":27,\"label\":\"Citizen Services Specialist\"},{\"id\":28,\"label\":\"Civil Engineer\"},{\"id\":29,\"label\":\"Civil Penalties Specialist\"},{\"id\":30,\"label\":\"Civil\\/Mechanical\\/Structural\"},{\"id\":31,\"label\":\"Classification Specialist\"},{\"id\":32,\"label\":\"Communications Specialist\"},{\"id\":34,\"label\":\"Community Planner\"},{\"id\":35,\"label\":\"Community Planning\\/Development\"},{\"id\":36,\"label\":\"Community Services Program\"},{\"id\":33,\"label\":\"Community and Intergovernmental\"},{\"id\":37,\"label\":\"Compliance Specialist\"},{\"id\":38,\"label\":\"Computer Aided\"},{\"id\":39,\"label\":\"Computer Engineer\"},{\"id\":40,\"label\":\"Computer Programmer\\/Analyst\"},{\"id\":41,\"label\":\"Computer Scientist\"},{\"id\":42,\"label\":\"Computer Specialist\"},{\"id\":43,\"label\":\"Consultant\"},{\"id\":44,\"label\":\"Consumer Safety Officer\"},{\"id\":45,\"label\":\"Contract Specialist\"},{\"id\":46,\"label\":\"Contract Specialist\\/Grants\"},{\"id\":47,\"label\":\"Corporate Management Analyst\"},{\"id\":48,\"label\":\"Cost Account\"},{\"id\":49,\"label\":\"Criminal Enforcement Analyst\"},{\"id\":50,\"label\":\"Criminal Investigator\"},{\"id\":51,\"label\":\"Customer Account Manager\"},{\"id\":52,\"label\":\"Customer Account ManagerSpecialist\"},{\"id\":53,\"label\":\"Democracy Specialist\"},{\"id\":54,\"label\":\"Desk Officer\"},{\"id\":55,\"label\":\"Development Specialist\"},{\"id\":56,\"label\":\"Director\"},{\"id\":57,\"label\":\"Disaster Operations Specialist\"},{\"id\":58,\"label\":\"Disbursing Specialist\"},{\"id\":59,\"label\":\"Ecologist\"},{\"id\":60,\"label\":\"Economist\"},{\"id\":61,\"label\":\"Economist, Financial\"},{\"id\":62,\"label\":\"Education Specialist\"},{\"id\":63,\"label\":\"Electrical Engineer\"},{\"id\":64,\"label\":\"Electronic Database C\"},{\"id\":65,\"label\":\"Electronics Engineer\"},{\"id\":66,\"label\":\"Emergency Management Specialist\"},{\"id\":68,\"label\":\"Employee Development Specialist\"},{\"id\":69,\"label\":\"Employee Relations Specialist\"},{\"id\":67,\"label\":\"Employee and Management\"},{\"id\":71,\"label\":\"Energy Program Specialist\"},{\"id\":70,\"label\":\"Energy and Environmental Policy\"},{\"id\":72,\"label\":\"Engineer\"},{\"id\":73,\"label\":\"Engineer (General)\"},{\"id\":74,\"label\":\"Environmental Engineer\"},{\"id\":75,\"label\":\"Environmental Planning and Policy\"},{\"id\":76,\"label\":\"Environmental Protection Specialist\"},{\"id\":77,\"label\":\"Environmental Specialist\"},{\"id\":78,\"label\":\"Epidemiologist\"},{\"id\":79,\"label\":\"Equal Employment Opportunity\"},{\"id\":80,\"label\":\"Equal Opportunity Specialist\"},{\"id\":81,\"label\":\"Ethics Program Specialist\"},{\"id\":82,\"label\":\"Evaluation and Technical Services\"},{\"id\":83,\"label\":\"Evaluator\"},{\"id\":84,\"label\":\"Executive Analyst\"},{\"id\":85,\"label\":\"Facilities Analyst\"},{\"id\":86,\"label\":\"Facility Specialist\"},{\"id\":87,\"label\":\"Federal Retirement Benefits\"},{\"id\":88,\"label\":\"Field Management Assistant\"},{\"id\":89,\"label\":\"Field Office Supervisor\"},{\"id\":90,\"label\":\"Financial Legislative Specialist\"},{\"id\":91,\"label\":\"Financial Management Specialist\"},{\"id\":92,\"label\":\"Financial Specialist\"},{\"id\":93,\"label\":\"Financial Systems Analyst\"},{\"id\":94,\"label\":\"Financial Transactions Examination\"},{\"id\":95,\"label\":\"Food Safety Coordinator\"},{\"id\":96,\"label\":\"Food Technologist\"},{\"id\":97,\"label\":\"Foreign Affairs Officer\"},{\"id\":98,\"label\":\"Foreign Affairs Specialist\"},{\"id\":99,\"label\":\"Foreign Assets Control Intelligence\"},{\"id\":100,\"label\":\"Foreign Assets Control Terrorist\"},{\"id\":101,\"label\":\"Functional Area Analyst\"},{\"id\":102,\"label\":\"General Engineer\"},{\"id\":103,\"label\":\"Generalist\"},{\"id\":104,\"label\":\"Geographer\"},{\"id\":105,\"label\":\"Geographical Information Systems\"},{\"id\":106,\"label\":\"Geophysicist\"},{\"id\":107,\"label\":\"Grants Program Specialist\"},{\"id\":108,\"label\":\"Grants Specialist\"},{\"id\":109,\"label\":\"Hazard Mitigation Specialist\"},{\"id\":110,\"label\":\"Hazardous Waste Generator\"},{\"id\":111,\"label\":\"Health Communications Specialist\"},{\"id\":112,\"label\":\"Health Educator\"},{\"id\":113,\"label\":\"Health Insurance Specialist\"},{\"id\":114,\"label\":\"Health Scientist\"},{\"id\":115,\"label\":\"Health Systems Specialist\"},{\"id\":116,\"label\":\"Hospital Finance Associate\"},{\"id\":117,\"label\":\"Housing Program Specialist\"},{\"id\":118,\"label\":\"Housing Project Manager\"},{\"id\":119,\"label\":\"Human Resources Advisor\"},{\"id\":120,\"label\":\"Human Resources Consultant\"},{\"id\":121,\"label\":\"Human Resources Development\"},{\"id\":122,\"label\":\"Human Resources Evaluator\"},{\"id\":123,\"label\":\"Human Resources Representative\"},{\"id\":124,\"label\":\"Human Resources Specialist\"},{\"id\":125,\"label\":\"Hydraulic Engineer\"},{\"id\":126,\"label\":\"Immigration Officer\"},{\"id\":127,\"label\":\"Import Policy Analyst\"},{\"id\":128,\"label\":\"Industrial Hygienist\"},{\"id\":129,\"label\":\"Information Management Specialist\"},{\"id\":130,\"label\":\"Information Office\"},{\"id\":131,\"label\":\"Information Research Specialist\"},{\"id\":132,\"label\":\"Information Resource Management\"},{\"id\":133,\"label\":\"Information Technology Policy\"},{\"id\":134,\"label\":\"Information Technology Security\"},{\"id\":135,\"label\":\"Information Technology Specialist\"},{\"id\":136,\"label\":\"Initiative Specialist\"},{\"id\":137,\"label\":\"Inspector\"},{\"id\":138,\"label\":\"Instructional Systems Design\"},{\"id\":139,\"label\":\"Instructions Methods Specialist\"},{\"id\":140,\"label\":\"Insurance Marketing Specialist\"},{\"id\":141,\"label\":\"Insurance Specialist\"},{\"id\":142,\"label\":\"Intelligence Analyst\"},{\"id\":143,\"label\":\"Intelligence Operations Specialist\"},{\"id\":144,\"label\":\"Intelligence Research Specialist\"},{\"id\":145,\"label\":\"Intelligence Specialist\"},{\"id\":146,\"label\":\"Internal Program Specialist\"},{\"id\":147,\"label\":\"Internal Revenue Agent\"},{\"id\":148,\"label\":\"International Affairs Specialist\"},{\"id\":149,\"label\":\"International Aviation Operations\"},{\"id\":150,\"label\":\"International Cooperation Specialist\"},{\"id\":151,\"label\":\"International Economist\"},{\"id\":152,\"label\":\"International Project Manager\"},{\"id\":153,\"label\":\"International Relations Specialist\"},{\"id\":154,\"label\":\"International Trade Litigation\"},{\"id\":155,\"label\":\"International Trade Specialist\"},{\"id\":156,\"label\":\"International Transportation\"},{\"id\":157,\"label\":\"Investigator\"},{\"id\":158,\"label\":\"Junior Foreign Affairs Officer\"},{\"id\":159,\"label\":\"Labor Relations Specialist\"},{\"id\":160,\"label\":\"Learning Specialist\"},{\"id\":161,\"label\":\"Legislative Analyst\"},{\"id\":162,\"label\":\"Legislative Assistant\"},{\"id\":163,\"label\":\"Legislative Specialist\"},{\"id\":164,\"label\":\"Lender Approval Analyst\"},{\"id\":165,\"label\":\"Lender Monitoring Analyst\"},{\"id\":166,\"label\":\"Licensing Examining Specialist\\/Offices\"},{\"id\":167,\"label\":\"Logistics Management Specialist\"},{\"id\":168,\"label\":\"Managed Care Specialist\"},{\"id\":169,\"label\":\"Management Analyst\"},{\"id\":172,\"label\":\"Management Intern\"},{\"id\":173,\"label\":\"Management Specialist\"},{\"id\":174,\"label\":\"Management Support Analyst\"},{\"id\":175,\"label\":\"Management Support Specialist\"},{\"id\":170,\"label\":\"Management and Budget Analyst\"},{\"id\":171,\"label\":\"Management and Program Analyst\"},{\"id\":176,\"label\":\"Manager\"},{\"id\":177,\"label\":\"Manpower Analyst\"},{\"id\":178,\"label\":\"Manpower Development Specialist\"},{\"id\":179,\"label\":\"Marketing Analyst\"},{\"id\":180,\"label\":\"Marketing Specialist\"},{\"id\":181,\"label\":\"Mass Communications Producer\"},{\"id\":182,\"label\":\"Mathematical Statistician\"},{\"id\":183,\"label\":\"Media Relations Assistant\"},{\"id\":184,\"label\":\"Meteorologist\"},{\"id\":185,\"label\":\"Microbiologist\"},{\"id\":186,\"label\":\"Mitigation Program Specialist\"},{\"id\":187,\"label\":\"National Security\"},{\"id\":188,\"label\":\"National Security Training Technology\"},{\"id\":189,\"label\":\"Natural Resources Specialist\"},{\"id\":190,\"label\":\"Naval Architect\"},{\"id\":191,\"label\":\"Officer\"},{\"id\":192,\"label\":\"Operations Officer\"},{\"id\":193,\"label\":\"Operations Planner\"},{\"id\":194,\"label\":\"Operations Research Analyst\"},{\"id\":195,\"label\":\"Operations Supervisor\"},{\"id\":196,\"label\":\"Other\"},{\"id\":197,\"label\":\"Outdoor Recreation Planner\"},{\"id\":198,\"label\":\"Paralegal Specialist\"},{\"id\":199,\"label\":\"Passport\\/Visa Specialist\"},{\"id\":200,\"label\":\"Personnel Management Specialist\"},{\"id\":201,\"label\":\"Personnel Staffing and\"},{\"id\":202,\"label\":\"Petroleum Engineer\"},{\"id\":203,\"label\":\"Physical Science Officer\"},{\"id\":204,\"label\":\"Physical Scientist, General\"},{\"id\":205,\"label\":\"Physical Security Specialist\"},{\"id\":206,\"label\":\"Policy Advisor to the Director\"},{\"id\":207,\"label\":\"Policy Analyst\"},{\"id\":210,\"label\":\"Policy Coordinator\"},{\"id\":208,\"label\":\"Policy and Procedure Analyst\"},{\"id\":209,\"label\":\"Policy and Regulatory Analyst\"},{\"id\":211,\"label\":\"Policy\\/Program Analyst\"},{\"id\":212,\"label\":\"Population\\/Family Planning\"},{\"id\":213,\"label\":\"Position Classification Specialist\"},{\"id\":214,\"label\":\"Presidential Management Fellow\"},{\"id\":215,\"label\":\"Procurement Analyst\"},{\"id\":216,\"label\":\"Procurement Specialist\"},{\"id\":217,\"label\":\"Professional Relations Outreach\"},{\"id\":218,\"label\":\"Program Administrator\"},{\"id\":219,\"label\":\"Program Analyst\"},{\"id\":221,\"label\":\"Program Evaluation and Risk\"},{\"id\":222,\"label\":\"Program Evolution Team Leader\"},{\"id\":223,\"label\":\"Program Examiner\"},{\"id\":224,\"label\":\"Program Manager\"},{\"id\":225,\"label\":\"Program Operations Specialist\"},{\"id\":226,\"label\":\"Program Specialist\"},{\"id\":227,\"label\":\"Program Support Specialist\"},{\"id\":220,\"label\":\"Program and Policy Analyst\"},{\"id\":228,\"label\":\"Program\\/Public Health Analyst\"},{\"id\":229,\"label\":\"Project Analyst\"},{\"id\":230,\"label\":\"Project Manager\"},{\"id\":231,\"label\":\"Prototype Activities Coordinator\"},{\"id\":232,\"label\":\"Psychologist (General (\"},{\"id\":233,\"label\":\"Public Affairs Assistant\"},{\"id\":234,\"label\":\"Public Affairs Intern\"},{\"id\":235,\"label\":\"Public Affairs Specialist\"},{\"id\":236,\"label\":\"Public Health Advisor\"},{\"id\":237,\"label\":\"Public Health Analyst\"},{\"id\":238,\"label\":\"Public Health Specialist\"},{\"id\":239,\"label\":\"Public Liaison\\/Outreach Specialist\"},{\"id\":240,\"label\":\"Public Policy Analyst\"},{\"id\":241,\"label\":\"Quantitative Analyst\"},{\"id\":242,\"label\":\"Real Estate Appraiser\"},{\"id\":243,\"label\":\"Realty Specialist\"},{\"id\":244,\"label\":\"Regional Management Analyst\"},{\"id\":245,\"label\":\"Regional Technician\"},{\"id\":246,\"label\":\"Regulatory Analyst\"},{\"id\":247,\"label\":\"Regulatory Specialist\"},{\"id\":248,\"label\":\"Research Analyst\"},{\"id\":249,\"label\":\"Restructuring Analyst\"},{\"id\":250,\"label\":\"Risk Analyst\"},{\"id\":252,\"label\":\"Safety Engineer\\/Industrial Hygienist\"},{\"id\":251,\"label\":\"Safety and Occupational Health\"},{\"id\":253,\"label\":\"Science Program Analyst\"},{\"id\":254,\"label\":\"Securities Compliance Examiner\"},{\"id\":255,\"label\":\"Security Specialist\"},{\"id\":256,\"label\":\"Senior Management Information\"},{\"id\":257,\"label\":\"Social Insurance Analyst\"},{\"id\":258,\"label\":\"Social Insurance Policy Specialist\"},{\"id\":259,\"label\":\"Social Insurance Specialist\"},{\"id\":260,\"label\":\"Social Science Analyst\"},{\"id\":261,\"label\":\"Social Science Research Analyst\"},{\"id\":262,\"label\":\"Social Scientist\"},{\"id\":263,\"label\":\"South Asia Desk Officer\"},{\"id\":264,\"label\":\"Special Assistant\"},{\"id\":265,\"label\":\"Special Assistant for Foreign Policy\"},{\"id\":266,\"label\":\"Special Assistant to the Associate\"},{\"id\":267,\"label\":\"Special Assistant to the Chief\"},{\"id\":268,\"label\":\"Special Assistant to the Chief, FBI\"},{\"id\":269,\"label\":\"Special Assistant to the Director\"},{\"id\":270,\"label\":\"Special Emphasis Program Manager\"},{\"id\":271,\"label\":\"Special Projects Analyst\"},{\"id\":272,\"label\":\"Specialist\"},{\"id\":273,\"label\":\"Staff Associate\"},{\"id\":274,\"label\":\"Statistician\"},{\"id\":275,\"label\":\"Strategy\"},{\"id\":276,\"label\":\"Supply Systems Analyst\"},{\"id\":278,\"label\":\"Survey Statistician\"},{\"id\":277,\"label\":\"Survey or Mathematical Statistician\"},{\"id\":279,\"label\":\"Systems Accountant\"},{\"id\":280,\"label\":\"Systems Analyst\"},{\"id\":281,\"label\":\"Tax Law Specialist\"},{\"id\":282,\"label\":\"Team Leader\"},{\"id\":283,\"label\":\"Technical Writer\\/Editor\"},{\"id\":284,\"label\":\"Telecommunications Policy Analyst\"},{\"id\":285,\"label\":\"Telecommunications Specialist\"},{\"id\":286,\"label\":\"Traffic Management Specialist\"},{\"id\":288,\"label\":\"Training Specialist\"},{\"id\":287,\"label\":\"Training and Technical Assistant\"},{\"id\":289,\"label\":\"Transportation Analyst\"},{\"id\":290,\"label\":\"Transportation Industry Analyst\"},{\"id\":291,\"label\":\"Transportation Program Specialist\"},{\"id\":292,\"label\":\"Urban Development Specialist\"},{\"id\":293,\"label\":\"Usability Researcher\"},{\"id\":294,\"label\":\"Veterans  \\/Employment Specialist\"},{\"id\":295,\"label\":\"Video Production Specialist\"},{\"id\":296,\"label\":\"Visa Specialist\"},{\"id\":297,\"label\":\"Work Incentives Coordinator\"},{\"id\":298,\"label\":\"Worker  \\/Compensation Specialist\"},{\"id\":299,\"label\":\"Workforce Development Specialist\"},{\"id\":300,\"label\":\"Worklife Wellness Specialist\"},{\"id\":301,\"label\":\"Writer\"},{\"id\":302,\"label\":\"Writer\\/Editor\"}],\"countries\":[{\"id\":1,\"code\":\"AF\",\"label\":\"Afghanistan\"},{\"id\":2,\"code\":\"AL\",\"label\":\"Albania\"},{\"id\":3,\"code\":\"DZ\",\"label\":\"Algeria\"},{\"id\":4,\"code\":\"DS\",\"label\":\"American Samoa\"},{\"id\":5,\"code\":\"AD\",\"label\":\"Andorra\"},{\"id\":6,\"code\":\"AO\",\"label\":\"Angola\"},{\"id\":7,\"code\":\"AI\",\"label\":\"Anguilla\"},{\"id\":8,\"code\":\"AQ\",\"label\":\"Antarctica\"},{\"id\":9,\"code\":\"AG\",\"label\":\"Antigua and Barbuda\"},{\"id\":10,\"code\":\"AR\",\"label\":\"Argentina\"},{\"id\":11,\"code\":\"AM\",\"label\":\"Armenia\"},{\"id\":12,\"code\":\"AW\",\"label\":\"Aruba\"},{\"id\":13,\"code\":\"AU\",\"label\":\"Australia\"},{\"id\":14,\"code\":\"AT\",\"label\":\"Austria\"},{\"id\":15,\"code\":\"AZ\",\"label\":\"Azerbaijan\"},{\"id\":16,\"code\":\"BS\",\"label\":\"Bahamas\"},{\"id\":17,\"code\":\"BH\",\"label\":\"Bahrain\"},{\"id\":18,\"code\":\"BD\",\"label\":\"Bangladesh\"},{\"id\":19,\"code\":\"BB\",\"label\":\"Barbados\"},{\"id\":20,\"code\":\"BY\",\"label\":\"Belarus\"},{\"id\":21,\"code\":\"BE\",\"label\":\"Belgium\"},{\"id\":22,\"code\":\"BZ\",\"label\":\"Belize\"},{\"id\":23,\"code\":\"BJ\",\"label\":\"Benin\"},{\"id\":24,\"code\":\"BM\",\"label\":\"Bermuda\"},{\"id\":25,\"code\":\"BT\",\"label\":\"Bhutan\"},{\"id\":26,\"code\":\"BO\",\"label\":\"Bolivia\"},{\"id\":27,\"code\":\"BA\",\"label\":\"Bosnia and Herzegovina\"},{\"id\":28,\"code\":\"BW\",\"label\":\"Botswana\"},{\"id\":29,\"code\":\"BV\",\"label\":\"Bouvet Island\"},{\"id\":30,\"code\":\"BR\",\"label\":\"Brazil\"},{\"id\":31,\"code\":\"IO\",\"label\":\"British Indian Ocean Territory\"},{\"id\":32,\"code\":\"BN\",\"label\":\"Brunei Darussalam\"},{\"id\":33,\"code\":\"BG\",\"label\":\"Bulgaria\"},{\"id\":34,\"code\":\"BF\",\"label\":\"Burkina Faso\"},{\"id\":35,\"code\":\"BI\",\"label\":\"Burundi\"},{\"id\":36,\"code\":\"KH\",\"label\":\"Cambodia\"},{\"id\":37,\"code\":\"CM\",\"label\":\"Cameroon\"},{\"id\":38,\"code\":\"CA\",\"label\":\"Canada\"},{\"id\":39,\"code\":\"CV\",\"label\":\"Cape Verde\"},{\"id\":40,\"code\":\"KY\",\"label\":\"Cayman Islands\"},{\"id\":41,\"code\":\"CF\",\"label\":\"Central African Republic\"},{\"id\":42,\"code\":\"TD\",\"label\":\"Chad\"},{\"id\":43,\"code\":\"CL\",\"label\":\"Chile\"},{\"id\":44,\"code\":\"CN\",\"label\":\"China\"},{\"id\":45,\"code\":\"CX\",\"label\":\"Christmas Island\"},{\"id\":46,\"code\":\"CC\",\"label\":\"Cocos (Keeling) Islands\"},{\"id\":47,\"code\":\"CO\",\"label\":\"Colombia\"},{\"id\":48,\"code\":\"KM\",\"label\":\"Comoros\"},{\"id\":49,\"code\":\"CG\",\"label\":\"Congo\"},{\"id\":50,\"code\":\"CK\",\"label\":\"Cook Islands\"},{\"id\":51,\"code\":\"CR\",\"label\":\"Costa Rica\"},{\"id\":52,\"code\":\"HR\",\"label\":\"Croatia (Hrvatska)\"},{\"id\":53,\"code\":\"CU\",\"label\":\"Cuba\"},{\"id\":54,\"code\":\"CY\",\"label\":\"Cyprus\"},{\"id\":55,\"code\":\"CZ\",\"label\":\"Czech Republic\"},{\"id\":56,\"code\":\"DK\",\"label\":\"Denmark\"},{\"id\":57,\"code\":\"DJ\",\"label\":\"Djibouti\"},{\"id\":58,\"code\":\"DM\",\"label\":\"Dominica\"},{\"id\":59,\"code\":\"DO\",\"label\":\"Dominican Republic\"},{\"id\":60,\"code\":\"TP\",\"label\":\"East Timor\"},{\"id\":61,\"code\":\"EC\",\"label\":\"Ecuador\"},{\"id\":62,\"code\":\"EG\",\"label\":\"Egypt\"},{\"id\":63,\"code\":\"SV\",\"label\":\"El Salvador\"},{\"id\":64,\"code\":\"GQ\",\"label\":\"Equatorial Guinea\"},{\"id\":65,\"code\":\"ER\",\"label\":\"Eritrea\"},{\"id\":66,\"code\":\"EE\",\"label\":\"Estonia\"},{\"id\":67,\"code\":\"ET\",\"label\":\"Ethiopia\"},{\"id\":68,\"code\":\"FK\",\"label\":\"Falkland Islands (Malvinas)\"},{\"id\":69,\"code\":\"FO\",\"label\":\"Faroe Islands\"},{\"id\":70,\"code\":\"FJ\",\"label\":\"Fiji\"},{\"id\":71,\"code\":\"FI\",\"label\":\"Finland\"},{\"id\":72,\"code\":\"FR\",\"label\":\"France\"},{\"id\":73,\"code\":\"FX\",\"label\":\"France, Metropolitan\"},{\"id\":74,\"code\":\"GF\",\"label\":\"French Guiana\"},{\"id\":75,\"code\":\"PF\",\"label\":\"French Polynesia\"},{\"id\":76,\"code\":\"TF\",\"label\":\"French Southern Territories\"},{\"id\":77,\"code\":\"GA\",\"label\":\"Gabon\"},{\"id\":78,\"code\":\"GM\",\"label\":\"Gambia\"},{\"id\":79,\"code\":\"GE\",\"label\":\"Georgia\"},{\"id\":80,\"code\":\"DE\",\"label\":\"Germany\"},{\"id\":81,\"code\":\"GH\",\"label\":\"Ghana\"},{\"id\":82,\"code\":\"GI\",\"label\":\"Gibraltar\"},{\"id\":84,\"code\":\"GR\",\"label\":\"Greece\"},{\"id\":85,\"code\":\"GL\",\"label\":\"Greenland\"},{\"id\":86,\"code\":\"GD\",\"label\":\"Grenada\"},{\"id\":87,\"code\":\"GP\",\"label\":\"Guadeloupe\"},{\"id\":88,\"code\":\"GU\",\"label\":\"Guam\"},{\"id\":89,\"code\":\"GT\",\"label\":\"Guatemala\"},{\"id\":83,\"code\":\"GK\",\"label\":\"Guernsey\"},{\"id\":90,\"code\":\"GN\",\"label\":\"Guinea\"},{\"id\":91,\"code\":\"GW\",\"label\":\"Guinea-Bissau\"},{\"id\":92,\"code\":\"GY\",\"label\":\"Guyana\"},{\"id\":93,\"code\":\"HT\",\"label\":\"Haiti\"},{\"id\":94,\"code\":\"HM\",\"label\":\"Heard and Mc Donald Islands\"},{\"id\":95,\"code\":\"HN\",\"label\":\"Honduras\"},{\"id\":96,\"code\":\"HK\",\"label\":\"Hong Kong\"},{\"id\":97,\"code\":\"HU\",\"label\":\"Hungary\"},{\"id\":98,\"code\":\"IS\",\"label\":\"Iceland\"},{\"id\":99,\"code\":\"IN\",\"label\":\"India\"},{\"id\":101,\"code\":\"ID\",\"label\":\"Indonesia\"},{\"id\":102,\"code\":\"IR\",\"label\":\"Iran (Islamic Republic of)\"},{\"id\":103,\"code\":\"IQ\",\"label\":\"Iraq\"},{\"id\":104,\"code\":\"IE\",\"label\":\"Ireland\"},{\"id\":100,\"code\":\"IM\",\"label\":\"Isle of Man\"},{\"id\":105,\"code\":\"IL\",\"label\":\"Israel\"},{\"id\":106,\"code\":\"IT\",\"label\":\"Italy\"},{\"id\":107,\"code\":\"CI\",\"label\":\"Ivory Coast\"},{\"id\":109,\"code\":\"JM\",\"label\":\"Jamaica\"},{\"id\":110,\"code\":\"JP\",\"label\":\"Japan\"},{\"id\":108,\"code\":\"JE\",\"label\":\"Jersey\"},{\"id\":111,\"code\":\"JO\",\"label\":\"Jordan\"},{\"id\":112,\"code\":\"KZ\",\"label\":\"Kazakhstan\"},{\"id\":113,\"code\":\"KE\",\"label\":\"Kenya\"},{\"id\":114,\"code\":\"KI\",\"label\":\"Kiribati\"},{\"id\":115,\"code\":\"KP\",\"label\":\"Korea, Democratic People's Republic of\"},{\"id\":116,\"code\":\"KR\",\"label\":\"Korea, Republic of\"},{\"id\":117,\"code\":\"XK\",\"label\":\"Kosovo\"},{\"id\":118,\"code\":\"KW\",\"label\":\"Kuwait\"},{\"id\":119,\"code\":\"KG\",\"label\":\"Kyrgyzstan\"},{\"id\":120,\"code\":\"LA\",\"label\":\"Lao People's Democratic Republic\"},{\"id\":121,\"code\":\"LV\",\"label\":\"Latvia\"},{\"id\":122,\"code\":\"LB\",\"label\":\"Lebanon\"},{\"id\":123,\"code\":\"LS\",\"label\":\"Lesotho\"},{\"id\":124,\"code\":\"LR\",\"label\":\"Liberia\"},{\"id\":125,\"code\":\"LY\",\"label\":\"Libyan Arab Jamahiriya\"},{\"id\":126,\"code\":\"LI\",\"label\":\"Liechtenstein\"},{\"id\":127,\"code\":\"LT\",\"label\":\"Lithuania\"},{\"id\":128,\"code\":\"LU\",\"label\":\"Luxembourg\"},{\"id\":129,\"code\":\"MO\",\"label\":\"Macau\"},{\"id\":130,\"code\":\"MK\",\"label\":\"Macedonia\"},{\"id\":131,\"code\":\"MG\",\"label\":\"Madagascar\"},{\"id\":132,\"code\":\"MW\",\"label\":\"Malawi\"},{\"id\":133,\"code\":\"MY\",\"label\":\"Malaysia\"},{\"id\":134,\"code\":\"MV\",\"label\":\"Maldives\"},{\"id\":135,\"code\":\"ML\",\"label\":\"Mali\"},{\"id\":136,\"code\":\"MT\",\"label\":\"Malta\"},{\"id\":137,\"code\":\"MH\",\"label\":\"Marshall Islands\"},{\"id\":138,\"code\":\"MQ\",\"label\":\"Martinique\"},{\"id\":139,\"code\":\"MR\",\"label\":\"Mauritania\"},{\"id\":140,\"code\":\"MU\",\"label\":\"Mauritius\"},{\"id\":141,\"code\":\"TY\",\"label\":\"Mayotte\"},{\"id\":142,\"code\":\"MX\",\"label\":\"Mexico\"},{\"id\":143,\"code\":\"FM\",\"label\":\"Micronesia, Federated States of\"},{\"id\":144,\"code\":\"MD\",\"label\":\"Moldova, Republic of\"},{\"id\":145,\"code\":\"MC\",\"label\":\"Monaco\"},{\"id\":146,\"code\":\"MN\",\"label\":\"Mongolia\"},{\"id\":147,\"code\":\"ME\",\"label\":\"Montenegro\"},{\"id\":148,\"code\":\"MS\",\"label\":\"Montserrat\"},{\"id\":149,\"code\":\"MA\",\"label\":\"Morocco\"},{\"id\":150,\"code\":\"MZ\",\"label\":\"Mozambique\"},{\"id\":151,\"code\":\"MM\",\"label\":\"Myanmar\"},{\"id\":152,\"code\":\"NA\",\"label\":\"Namibia\"},{\"id\":153,\"code\":\"NR\",\"label\":\"Nauru\"},{\"id\":154,\"code\":\"NP\",\"label\":\"Nepal\"},{\"id\":155,\"code\":\"NL\",\"label\":\"Netherlands\"},{\"id\":156,\"code\":\"AN\",\"label\":\"Netherlands Antilles\"},{\"id\":157,\"code\":\"NC\",\"label\":\"New Caledonia\"},{\"id\":158,\"code\":\"NZ\",\"label\":\"New Zealand\"},{\"id\":159,\"code\":\"NI\",\"label\":\"Nicaragua\"},{\"id\":160,\"code\":\"NE\",\"label\":\"Niger\"},{\"id\":161,\"code\":\"NG\",\"label\":\"Nigeria\"},{\"id\":162,\"code\":\"NU\",\"label\":\"Niue\"},{\"id\":163,\"code\":\"NF\",\"label\":\"Norfolk Island\"},{\"id\":164,\"code\":\"MP\",\"label\":\"Northern Mariana Islands\"},{\"id\":165,\"code\":\"NO\",\"label\":\"Norway\"},{\"id\":166,\"code\":\"OM\",\"label\":\"Oman\"},{\"id\":167,\"code\":\"PK\",\"label\":\"Pakistan\"},{\"id\":168,\"code\":\"PW\",\"label\":\"Palau\"},{\"id\":169,\"code\":\"PS\",\"label\":\"Palestine\"},{\"id\":170,\"code\":\"PA\",\"label\":\"Panama\"},{\"id\":171,\"code\":\"PG\",\"label\":\"Papua New Guinea\"},{\"id\":172,\"code\":\"PY\",\"label\":\"Paraguay\"},{\"id\":173,\"code\":\"PE\",\"label\":\"Peru\"},{\"id\":174,\"code\":\"PH\",\"label\":\"Philippines\"},{\"id\":175,\"code\":\"PN\",\"label\":\"Pitcairn\"},{\"id\":176,\"code\":\"PL\",\"label\":\"Poland\"},{\"id\":177,\"code\":\"PT\",\"label\":\"Portugal\"},{\"id\":178,\"code\":\"PR\",\"label\":\"Puerto Rico\"},{\"id\":179,\"code\":\"QA\",\"label\":\"Qatar\"},{\"id\":180,\"code\":\"RE\",\"label\":\"Reunion\"},{\"id\":181,\"code\":\"RO\",\"label\":\"Romania\"},{\"id\":182,\"code\":\"RU\",\"label\":\"Russian Federation\"},{\"id\":183,\"code\":\"RW\",\"label\":\"Rwanda\"},{\"id\":184,\"code\":\"KN\",\"label\":\"Saint Kitts and Nevis\"},{\"id\":185,\"code\":\"LC\",\"label\":\"Saint Lucia\"},{\"id\":186,\"code\":\"VC\",\"label\":\"Saint Vincent and the Grenadines\"},{\"id\":187,\"code\":\"WS\",\"label\":\"Samoa\"},{\"id\":188,\"code\":\"SM\",\"label\":\"San Marino\"},{\"id\":189,\"code\":\"ST\",\"label\":\"Sao Tome and Principe\"},{\"id\":190,\"code\":\"SA\",\"label\":\"Saudi Arabia\"},{\"id\":191,\"code\":\"SN\",\"label\":\"Senegal\"},{\"id\":192,\"code\":\"RS\",\"label\":\"Serbia\"},{\"id\":193,\"code\":\"SC\",\"label\":\"Seychelles\"},{\"id\":194,\"code\":\"SL\",\"label\":\"Sierra Leone\"},{\"id\":195,\"code\":\"SG\",\"label\":\"Singapore\"},{\"id\":196,\"code\":\"SK\",\"label\":\"Slovakia\"},{\"id\":197,\"code\":\"SI\",\"label\":\"Slovenia\"},{\"id\":198,\"code\":\"SB\",\"label\":\"Solomon Islands\"},{\"id\":199,\"code\":\"SO\",\"label\":\"Somalia\"},{\"id\":200,\"code\":\"ZA\",\"label\":\"South Africa\"},{\"id\":201,\"code\":\"GS\",\"label\":\"South Georgia South Sandwich Islands\"},{\"id\":202,\"code\":\"ES\",\"label\":\"Spain\"},{\"id\":203,\"code\":\"LK\",\"label\":\"Sri Lanka\"},{\"id\":204,\"code\":\"SH\",\"label\":\"St. Helena\"},{\"id\":205,\"code\":\"PM\",\"label\":\"St. Pierre and Miquelon\"},{\"id\":206,\"code\":\"SD\",\"label\":\"Sudan\"},{\"id\":207,\"code\":\"SR\",\"label\":\"Suriname\"},{\"id\":208,\"code\":\"SJ\",\"label\":\"Svalbard and Jan Mayen Islands\"},{\"id\":209,\"code\":\"SZ\",\"label\":\"Swaziland\"},{\"id\":210,\"code\":\"SE\",\"label\":\"Sweden\"},{\"id\":211,\"code\":\"CH\",\"label\":\"Switzerland\"},{\"id\":212,\"code\":\"SY\",\"label\":\"Syrian Arab Republic\"},{\"id\":213,\"code\":\"TW\",\"label\":\"Taiwan\"},{\"id\":214,\"code\":\"TJ\",\"label\":\"Tajikistan\"},{\"id\":215,\"code\":\"TZ\",\"label\":\"Tanzania, United Republic of\"},{\"id\":216,\"code\":\"TH\",\"label\":\"Thailand\"},{\"id\":217,\"code\":\"TG\",\"label\":\"Togo\"},{\"id\":218,\"code\":\"TK\",\"label\":\"Tokelau\"},{\"id\":219,\"code\":\"TO\",\"label\":\"Tonga\"},{\"id\":220,\"code\":\"TT\",\"label\":\"Trinidad and Tobago\"},{\"id\":221,\"code\":\"TN\",\"label\":\"Tunisia\"},{\"id\":222,\"code\":\"TR\",\"label\":\"Turkey\"},{\"id\":223,\"code\":\"TM\",\"label\":\"Turkmenistan\"},{\"id\":224,\"code\":\"TC\",\"label\":\"Turks and Caicos Islands\"},{\"id\":225,\"code\":\"TV\",\"label\":\"Tuvalu\"},{\"id\":226,\"code\":\"UG\",\"label\":\"Uganda\"},{\"id\":227,\"code\":\"UA\",\"label\":\"Ukraine\"},{\"id\":228,\"code\":\"AE\",\"label\":\"United Arab Emirates\"},{\"id\":229,\"code\":\"GB\",\"label\":\"United Kingdom\"},{\"id\":230,\"code\":\"US\",\"label\":\"United States\"},{\"id\":231,\"code\":\"UM\",\"label\":\"United States minor outlying islands\"},{\"id\":232,\"code\":\"UY\",\"label\":\"Uruguay\"},{\"id\":233,\"code\":\"UZ\",\"label\":\"Uzbekistan\"},{\"id\":234,\"code\":\"VU\",\"label\":\"Vanuatu\"},{\"id\":235,\"code\":\"VA\",\"label\":\"Vatican City State\"},{\"id\":236,\"code\":\"VE\",\"label\":\"Venezuela\"},{\"id\":237,\"code\":\"VN\",\"label\":\"Vietnam\"},{\"id\":238,\"code\":\"VG\",\"label\":\"Virgin Islands (British)\"},{\"id\":239,\"code\":\"VI\",\"label\":\"Virgin Islands (U.S.)\"},{\"id\":240,\"code\":\"WF\",\"label\":\"Wallis and Futuna Islands\"},{\"id\":241,\"code\":\"EH\",\"label\":\"Western Sahara\"},{\"id\":242,\"code\":\"YE\",\"label\":\"Yemen\"},{\"id\":243,\"code\":\"YU\",\"label\":\"Yugoslavia\"},{\"id\":244,\"code\":\"ZR\",\"label\":\"Zaire\"},{\"id\":245,\"code\":\"ZM\",\"label\":\"Zambia\"},{\"id\":246,\"code\":\"ZW\",\"label\":\"Zimbabwe\"}],\"titles\":[{\"id\":4,\"label\":\"Dr.\"},{\"id\":1,\"label\":\"Mr.\"},{\"id\":3,\"label\":\"Mrs.\"},{\"id\":2,\"label\":\"Ms.\"}],\"seniority\":[{\"id\":8,\"code\":\"c\",\"label\":\"Chief X Officer (CxO)\"},{\"id\":6,\"code\":\"d\",\"label\":\"Director\"},{\"id\":3,\"code\":\"en\",\"label\":\"Entry-level\"},{\"id\":5,\"code\":\"m\",\"label\":\"Manager\"},{\"id\":10,\"code\":\"o\",\"label\":\"Owner\"},{\"id\":9,\"code\":\"p\",\"label\":\"Partner\"},{\"id\":4,\"code\":\"ic\",\"label\":\"Senior\"},{\"id\":2,\"code\":\"tr\",\"label\":\"Training\"},{\"id\":1,\"code\":\"up\",\"label\":\"Unpaid\"},{\"id\":7,\"code\":\"vp\",\"label\":\"Vice President (VP)\"}],\"states\":[{\"id\":1,\"label\":\"Alabama\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":2,\"label\":\"Alaska\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":3,\"label\":\"Arizona\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":4,\"label\":\"Arkansas\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":5,\"label\":\"California\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":6,\"label\":\"Colorado\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":7,\"label\":\"Connecticut\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":8,\"label\":\"Delaware\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":9,\"label\":\"District of Columbia\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":10,\"label\":\"Florida\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":11,\"label\":\"Georgia\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":12,\"label\":\"Hawaii\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":13,\"label\":\"Idaho\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":14,\"label\":\"Illinois\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":15,\"label\":\"Indiana\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":16,\"label\":\"Iowa\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":17,\"label\":\"Kansas\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":18,\"label\":\"Kentucky\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":19,\"label\":\"Louisiana\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":20,\"label\":\"Maine\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":21,\"label\":\"Maryland\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":22,\"label\":\"Massachusetts\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":23,\"label\":\"Michigan\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":24,\"label\":\"Minnesota\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":25,\"label\":\"Mississippi\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":26,\"label\":\"Missouri\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":27,\"label\":\"Montana\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":28,\"label\":\"Nebraska\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":29,\"label\":\"Nevada\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":30,\"label\":\"New Hampshire\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":31,\"label\":\"New Jersey\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":32,\"label\":\"New Mexico\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":33,\"label\":\"New York\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":34,\"label\":\"North Carolina\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":35,\"label\":\"North Dakota\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":36,\"label\":\"Ohio\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":37,\"label\":\"Oklahoma\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":38,\"label\":\"Oregon\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":39,\"label\":\"Pennsylvania\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":40,\"label\":\"Puerto Rico\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":41,\"label\":\"Rhode Island\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":42,\"label\":\"South Carolina\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":43,\"label\":\"South Dakota\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":44,\"label\":\"Tennessee\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":45,\"label\":\"Texas\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":46,\"label\":\"Utah\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":47,\"label\":\"Vermont\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":48,\"label\":\"Virginia\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":49,\"label\":\"Washington\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":50,\"label\":\"West Virginia\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":51,\"label\":\"Wisconsin\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}},{\"id\":52,\"label\":\"Wyoming\",\"country\":{\"id\":230,\"code\":\"US\",\"label\":\"United States\"}}]}";

    private static String PROFILE_TEST_CONTENT_JSON_STRING = "{\"email\":\"valentinrogovskiy@gmail.com\",\"user_id\":371,\"title_id\":1,\"first_name\":\"Valentin\",\"last_name\":\"Rogovskiy\",\"phone_number\":\"\",\"country_id\":null,\"state_id\":null,\"city_name\":\"\",\"company_name\":\"B2B\",\"position_id\":null,\"industry_id\":null,\"seniority_id\":5,\"size_id\":null,\"soc_network_type\":2,\"profile_pic\":null}";

    //COMMON METHODS
    public static void addSessionId(HttpRequestBase httpRequest){
        if(StarTrackApplication.sessionId!=null){
            httpRequest.addHeader("x-auth-id", StarTrackApplication.sessionId);
        }
    }

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

    private static String createOutputJSONForCreateProfile(boolean createOrUpdateFlag){
        Profile currentProfile = StarTrackApplication.currentProfile;
        StringBuffer outputJSON = new StringBuffer();
        if(currentProfile != null) {
            outputJSON.append("{");
/*            if(currentProfile.getEmail()!=null&&createOrUpdateFlag) {
                outputJSON.append("\"email\":\"" + currentProfile.getEmail() + "\"");
                outputJSON.append(",");
            }*/
            boolean commaFlag = false;
                if(currentProfile.editableProperties.get(0)!=null){
                    if(!currentProfile.editableProperties.get(0).getPropertyValue().equals("")) {
                        outputJSON.append("\"first_name\":\"" + currentProfile.editableProperties.get(0).getPropertyValue() + "\"");
                        commaFlag = true;
                    }
                }
                if(currentProfile.editableProperties.get(1)!= null){
                    if(!currentProfile.editableProperties.get(1).getPropertyValue().equals("")) {
                        if(commaFlag) {
                            outputJSON.append(",\"");
                        }
                        outputJSON.append("last_name\":\"" + currentProfile.editableProperties.get(1).getPropertyValue() + "\"");
                        commaFlag = true;
                    }
                }

                if(currentProfile.editableProperties.get(2)!= null){
                    if(!currentProfile.editableProperties.get(2).getPropertyValue().equals("")) {
                        if(commaFlag) {
                            outputJSON.append(",\"");
                        }
                        outputJSON.append("phone_number\":\"" + currentProfile.editableProperties.get(2).getPropertyValue() + "\"");
                        commaFlag = true;
                    }
                }
                if(currentProfile.editableProperties.get(3)!= null){
                    if(!currentProfile.editableProperties.get(3).getPropertyValue().equals("")) {
                        if (commaFlag) {
                            outputJSON.append(",\"");
                        }
                        outputJSON.append("company_name\":\"" + currentProfile.editableProperties.get(3).getPropertyValue() + "\"");
                        commaFlag = true;
                    }
                }
                if(currentProfile.selectableProperties.get(4)!=null){
                    if(currentProfile.selectableProperties.get(4).getValueId()!=(-1)) {
                        if (commaFlag) {
                            outputJSON.append(",\"");
                        }
                        outputJSON.append("size_id\":" + currentProfile.selectableProperties.get(4).getValueId());
                        commaFlag = true;
                    }
                }
                if(currentProfile.selectableProperties.get(5)!= null){
                    if(currentProfile.selectableProperties.get(5).getValueId()!=(-1)) {
                        if (commaFlag) {
                            outputJSON.append(",\"");
                        }
                        outputJSON.append("title_id\":" + currentProfile.selectableProperties.get(5).getValueId());
                        commaFlag = true;
                    }
                }
                if(currentProfile.selectableProperties.get(6)!= null){
                    if(currentProfile.selectableProperties.get(6).getValueId()!=(-1)) {
                        if (commaFlag) {
                            outputJSON.append(",\"");
                        }
                        outputJSON.append("seniority_id\":" + currentProfile.selectableProperties.get(6).getValueId());
                        commaFlag = true;
                    }
                }
                if(currentProfile.selectableProperties.get(7)!= null){
                    if(currentProfile.selectableProperties.get(7).getValueId()!=(-1)) {
                        if (commaFlag) {
                            outputJSON.append(",\"");
                        }
                        outputJSON.append("industry_id\":" + currentProfile.selectableProperties.get(7).getValueId());
                        commaFlag = true;
                    }
                }
                if(currentProfile.selectableProperties.get(8)!= null){
                    if(currentProfile.selectableProperties.get(8).getValueId()!=(-1)) {
                        if (commaFlag) {
                            outputJSON.append(",\"");
                        }
                        outputJSON.append("country_id\":" + currentProfile.selectableProperties.get(8).getValueId());
                        commaFlag = true;
                    }
                }
/*                if(currentProfile.selectableProperties.get(9)!= null){
                    outputJSON.append(",\"");
                    outputJSON.append("state_id\":" + currentProfile.selectableProperties.get(9).getValueId());
                }*/
                outputJSON.append("}");
                return outputJSON.toString();
        }
        return "{}";
    }

/*    Request Body:
    {
        search_text : “something”,
        size_id:<...>,
        seniority_id:<...>,
        industry_id:<...>,
        country_id:<...>,
        position_id:<...>,
    }*/

    private static String createOutputJSONForSearchOperation(){
        StringBuffer outputJSON = new StringBuffer();
        outputJSON.append("{");
        if(StarTrackApplication.searchText.length()>0){
            outputJSON.append("\"search_text\" : \"" + StarTrackApplication.searchText + "\",");
        } else {
            outputJSON.append("\"search_text\" : null,");
        }
        if(StarTrackApplication.titleId>(-1)){
            outputJSON.append("\"title_id\" : \"" + StarTrackApplication.titleId + "\",");
        } else {
            outputJSON.append("\"title_id\" : null,");
        }
        if(StarTrackApplication.seniorityId>(-1)){
            outputJSON.append("\"seniority_id\" : \"" + StarTrackApplication.seniorityId + "\",");
        } else {
            outputJSON.append("\"seniority_id\" : null,");
        }
        if(StarTrackApplication.industryId>(-1)){
            outputJSON.append("\"industry_id\" : \"" + StarTrackApplication.industryId + "\",");
        } else {
            outputJSON.append("\"industry_id\" : null,");
        }
        if(StarTrackApplication.countryId>(-1)){
            outputJSON.append("\"country_id\" : \"" + StarTrackApplication.countryId + "\",");
        } else {
            outputJSON.append("\"country_id\" : null,");
        }
        if(StarTrackApplication.positionId>(-1)){
            outputJSON.append("\"position_id\" : \"" + StarTrackApplication.positionId + "\",");
        } else {
            outputJSON.append("\"position_id\" : null,");
        }
        if(StarTrackApplication.companysizeId>(-1)){
            outputJSON.append("\"size_id\" : \"" + StarTrackApplication.companysizeId + "\"");
        } else {
            outputJSON.append("\"size_id\" : null");
        }
        outputJSON.append("}");
        return outputJSON.toString();
    }

    private static String createOutputJSONForMeet(){
        StringBuffer outputJSON = new StringBuffer();
        outputJSON.append("{\"user_ids\": [");
       for(int i = 0; i<MatchesActivity.checkedItems.size(); i++){
            outputJSON.append(MatchesActivity.checkedItems.get(i));
            if((i+1)<MatchesActivity.checkedItems.size()){
                outputJSON.append(",");
            }
       }
        outputJSON.append("]}");
        return outputJSON.toString();
    }

/*
    public static Profile createDummyProfile(int id){
        Profile profile = new Profile("test1@test1.com");
        profile.setProfilePic("");
        profile.setSocNetworkType(1);
        profile.setUserId(id);
        profile.editableProperties.get(0).setPropertyValue("test name");
        profile.editableProperties.get(1).setPropertyValue("test surname");
        profile.editableProperties.get(2).setPropertyValue("test phone number");
        profile.editableProperties.get(3).setPropertyValue("test company name");
        profile.selectableProperties.get(4).setPropertyValue("1");
        profile.selectableProperties.get(5).setPropertyValue("1");
        profile.selectableProperties.get(6).setPropertyValue("1");
        profile.selectableProperties.get(7).setPropertyValue("1");
        profile.selectableProperties.get(8).setPropertyValue("1");
        profile.selectableProperties.get(9).setPropertyValue("1");
        return profile;
    }
*/

    private static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    //PARSERS
    public static void parseDictionaries(JSONObject dictionariesJsonObject){
        Map<Integer,Map> dictionaries = StarTrackApplication.dictionaries;
        try {
/*    4 - company size
*     5 - title
*     6 - seniority
*     7 - industry
*     8 - country
*     9 - state
*     10 - position
* */
            //8 - country
            JSONArray countriesJSONArray =  dictionariesJsonObject.getJSONArray("countries");
            HashMap<Integer, String> countriesMap = new HashMap<Integer, String>();
            for(int i =0; i<countriesJSONArray.length(); i++){
                JSONObject jsonObject = (JSONObject) countriesJSONArray.get(i);
                int id = jsonObject.getInt("id");
                String label = jsonObject.getString("label");
                countriesMap.put(id, label);
            }
            dictionaries.put(8, sortByValues(countriesMap));

            //10 - position
            JSONArray positionsJSONArray =  dictionariesJsonObject.getJSONArray("positions");
            HashMap<Integer, String> positionsMap = new HashMap<Integer, String>();
            for(int i =0; i<positionsJSONArray.length(); i++){
                JSONObject jsonObject = (JSONObject) positionsJSONArray.get(i);
                int id = jsonObject.getInt("id");
                String label = jsonObject.getString("label");
                positionsMap.put(id,label);
            }
            dictionaries.put(10, sortByValues(positionsMap));

            //4 - company size
            JSONArray sizesJSONArray =  dictionariesJsonObject.getJSONArray("sizes");
            Map<Integer, Map<Integer, String>> sizesMap = new TreeMap<Integer, Map<Integer, String> >();
            for(int i =0; i<sizesJSONArray.length(); i++){
                JSONObject jsonObject = (JSONObject) sizesJSONArray.get(i);
                int minValue = jsonObject.getInt("minValue");
                int id = jsonObject.getInt("id");
                String label = jsonObject.getString("label");
                Map<Integer, String> tempMap = new HashMap<Integer, String>();
                tempMap.put(id,label);
                sizesMap.put(minValue,tempMap);
            }
            dictionaries.put(4, sizesMap);

            //7 - industry
            JSONArray industryJSONArray =  dictionariesJsonObject.getJSONArray("industries");
            HashMap<Integer, String> industryMap = new HashMap<Integer, String>();
            for(int i =0; i<industryJSONArray.length(); i++){
                JSONObject jsonObject = (JSONObject) industryJSONArray.get(i);
                int id = jsonObject.getInt("id");
                String label = jsonObject.getString("label");
                industryMap.put(id,label);
            }
            dictionaries.put(7, sortByValues(industryMap));

            //5 - title
            JSONArray titleJSONArray =  dictionariesJsonObject.getJSONArray("titles");
            HashMap<Integer, String> titleMap = new HashMap<Integer, String>();
            for(int i =0; i<titleJSONArray.length(); i++){
                JSONObject jsonObject = (JSONObject) titleJSONArray.get(i);
                int id = jsonObject.getInt("id");
                String label = jsonObject.getString("label");
                titleMap.put(id,label);
            }
            dictionaries.put(5, sortByValues(titleMap));

            //6 - seniority
            JSONArray seniorityJSONArray =  dictionariesJsonObject.getJSONArray("seniority");
            HashMap<Integer, String> seniorityMap = new HashMap<Integer, String>();
            for(int i =0; i<seniorityJSONArray.length(); i++){
                JSONObject jsonObject = (JSONObject) seniorityJSONArray.get(i);
                int id = jsonObject.getInt("id");
                String label = jsonObject.getString("label");
                seniorityMap.put(id,label);
            }
            dictionaries.put(6, sortByValues(seniorityMap));

            //9 - state
            JSONArray stateJSONArray =  dictionariesJsonObject.getJSONArray("states");
            HashMap<Integer, String> stateMap = new HashMap<Integer, String>();
            for(int i =0; i<stateJSONArray.length(); i++){
                JSONObject jsonObject = (JSONObject) stateJSONArray.get(i);
                int id = jsonObject.getInt("id");
                String label = jsonObject.getString("label");
                stateMap.put(id,label);
            }
            dictionaries.put(9, sortByValues(stateMap));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

/*
    public static void parceProfileObject(JSONObject jsonObject) throws JSONException {
        Profile profile = null;

            profile = new Profile(jsonObject.getString("email"));
            Map<Integer, SelectableProfileProperty> selectableProperties = profile.selectableProperties;
            Map<Integer, EditableProfileProperty> editableProperties = profile.editableProperties;
*/
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
*     11 - city_name
* *//*

            if(jsonObject.has("first_name")&&!jsonObject.isNull("first_name")){
                if(!jsonObject.getString("first_name").equals("null")) {
                    editableProperties.put(0, new EditableProfileProperty(0, jsonObject.getString("first_name")));
                }
            }
            if(jsonObject.has("last_name")&&!jsonObject.isNull("last_name")) {
                if(!jsonObject.getString("last_name").equals("null")) {
                    editableProperties.put(1, new EditableProfileProperty(1, jsonObject.getString("last_name")));
                }
            }
            if(jsonObject.has("phone_number")&&!jsonObject.isNull("phone_number")) {
                if(!jsonObject.getString("phone_number").equals("null")) {
                    editableProperties.put(2, new EditableProfileProperty(2, jsonObject.getString("phone_number")));
                }
            }
            if(jsonObject.has("company_name")&&!jsonObject.isNull("company_name")) {
                if(!jsonObject.getString("company_name").equals("null")) {
                    editableProperties.put(3, new EditableProfileProperty(3, jsonObject.getString("company_name")));
                }
            }
            if(jsonObject.has("size_id")&&!jsonObject.isNull("size_id")) {
                selectableProperties.put(4, new SelectableProfileProperty(4, jsonObject.getInt("size_id")));
            }
            if(jsonObject.has("title_id")&&!jsonObject.isNull("title_id")) {
                selectableProperties.put(5, new SelectableProfileProperty(5, jsonObject.getInt("title_id")));
            }
            if(jsonObject.has("seniority_id")&&!jsonObject.isNull("seniority_id")) {
                selectableProperties.put(6, new SelectableProfileProperty(6, jsonObject.getInt("seniority_id")));
            }
            if(jsonObject.has("industry_id")&&!jsonObject.isNull("industry_id")) {
                selectableProperties.put(7, new SelectableProfileProperty(7, jsonObject.getInt("industry_id")));
            }
            if(jsonObject.has("country_id")&&!jsonObject.isNull("country_id")) {
                selectableProperties.put(8, new SelectableProfileProperty(8, jsonObject.getInt("country_id")));
            }
            if(jsonObject.has("state_id")&&!jsonObject.isNull("state_id")) {
                selectableProperties.put(9, new SelectableProfileProperty(9, jsonObject.getInt("state_id")));
            }
            if(jsonObject.has("position_id")&&!jsonObject.isNull("position_id")) {
                selectableProperties.put(10, new SelectableProfileProperty(10, jsonObject.getInt("position_id")));
            }
            if(jsonObject.has("city_name")&&!jsonObject.isNull("city_name")) {
                if(!jsonObject.getString("city_name").equals("null")) {
                    editableProperties.put(11, new EditableProfileProperty(11, jsonObject.getString("city_name")));
                }
            }
            if(jsonObject.has("user_id")&&!jsonObject.isNull("user_id")) {
                profile.setUserId(jsonObject.getInt("user_id"));
            }
            if(jsonObject.has("soc_network_type")&&!jsonObject.isNull("soc_network_type")) {
                profile.setSocNetworkType(jsonObject.getInt("soc_network_type"));
            }
            if(jsonObject.has("profile_pic")&&!jsonObject.isNull("profile_pic")) {
                if (jsonObject.getString("first_name").equals("null")) {

                    profile.setProfilePic(jsonObject.getString("profile_pic"));
                }
            }
            StarTrackApplication.currentProfile = profile;
    }
*/



/*    public static String login(String token){

        new GetSessionIdOperation().execute(new String());

*//*        String json = null;
        HttpPost httppost = new HttpPost(SERVER_ADRESS+LOGIN_API);
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response  = null;
        String status;
        String message;
        String content = null;
        //List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        //nameValuePairs.add(new BasicNameValuePair("soc_network_type", "2"));
        //nameValuePairs.add(new BasicNameValuePair("access_token", token));
        try {

            String xml = "{\"soc_network_type\":\"2\", \"access_token\":\""  + token + "\"}";
            HttpEntity entity = new ByteArrayEntity(xml.getBytes("UTF-8"));
            httppost.setEntity(entity);
            //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        response = httpclient.execute(httppost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            json = reader.readLine();
            reader.close();
            JSONObject jObject = new JSONObject(json);
            status = jObject.getString("status");
            message = jObject.getString("message");
            content = jObject.getString("content");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }*//*
        return null;
    }*/


/*    public static Profile getProfile(){
        Profile profile = new Profile("someone@somewhere.com");

        profile.getEditableProperties().get(0).setPropertyValue("Vasiliy Pupkin");
        profile.getSelectableProperties().get(1).setValueId(1);
        profile.getSelectableProperties().get(4).setValueId(1);
        return profile;
    }

    public static Profile getNewProfile(){

        return null;
    }

    public static List<Profile> getSearchResults() {
        return new ArrayList<Profile>();
    }

    public static void search(){

    }*/
}
