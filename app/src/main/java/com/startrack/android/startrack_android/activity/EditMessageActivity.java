package com.startrack.android.startrack_android.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.startrack.android.startrack_android.R;
import com.startrack.android.startrack_android.api.APIService;
import com.startrack.android.startrack_android.app.StarTrackApplication;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditMessageActivity extends AppCompatActivity implements IActivityWithHandler {
    @Bind(R.id.tv_title_edit_message)
    TextView mTvTitle;
    @Bind(R.id.iv_back_edit_mess)
    ImageView mIvBack;
    @Bind(R.id.rl_edit_message)
    RelativeLayout mRlContainer;
    @Bind(R.id.et_edit_message)
    EditText mEtMessage;
    @Bind(R.id.b_send)
    Button mBSend;
    @Bind(R.id.pb_edit_message)
    ProgressBar mProgress;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_message);
        ButterKnife.bind(this);
        initHandler();
        getMessageTemplate();
        getExtras();
        setTypefaces();
    }

    private void initHandler() {
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (msg.getData().getString(MatchesActivity.MEET_RESULT_MSG_PROPERTY_NAME) != null) {
                    if (msg.getData().getString(MatchesActivity.MEET_RESULT_MSG_PROPERTY_NAME).equals(MatchesActivity.MEET_RESULT_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                        Toast.makeText(EditMessageActivity.this, "Meeting request sent",
                                Toast.LENGTH_LONG).show();
                        hideProgressAndShowViews();
                        finish();
                    } else if (msg.getData().getString(MatchesActivity.MEET_RESULT_MSG_PROPERTY_NAME).equals(MatchesActivity.MEET_RESULT_MSG_PROPERTY_VALUE_UNSUCCESSFUL)) {
                        hideProgressAndShowViews();
                    }
                }
            }

        };
    }

    private void getExtras() {

    }

    private void setTypefaces() {
        Typeface custom_font_regular = Typeface.createFromAsset(getAssets(), "fonts/Bariol_Regular.otf");
        mTvTitle.setTypeface(custom_font_regular);
        mEtMessage.setTypeface(custom_font_regular);
        mBSend.setTypeface(custom_font_regular);
    }

    @OnClick(R.id.iv_back_edit_mess)
    public void onBackClick() {
        onBackPressed();
    }

    @OnClick(R.id.rl_edit_message)
    public void onContainerClick() {
        mEtMessage.setFocusable(true);
    }

    @OnClick(R.id.b_send)
    public void onSendClick() {
        String message = mEtMessage.getText().toString();

        if (!TextUtils.isEmpty(message)) {
            new APIService.MeetOperation(message).execute(this);
        } else {
            Toast.makeText(EditMessageActivity.this, "Write something", Toast.LENGTH_SHORT).show();
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

    @Override
    public void hideProgressAndShowViews() {

    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showAuthorizarionDialogForXing() {

    }

    private void getMessageTemplate() {
        Log.d("TAG1", "async");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                mEtMessage.setVisibility(View.GONE);
                mBSend.setVisibility(View.GONE);
                mProgress.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Void... params) {
                Log.d("TAG1", "doin");
                JSONObject req = new JSONObject();
                try {
                    req.put("user_ids", new JSONArray());
                    req.put("send", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String request = req.toString();
                Log.d("TAG1", "jjjson = " + request);
//                String request = "{\n" +
//                        "\"user_ids\": [],\n" +
//                        "\"send\":false\n" +
//                        "}";
                HttpResponse response = null;
                String json = "";
                try {
                    HttpPost httppost = new HttpPost(StarTrackApplication.SERVER_ADRESS + StarTrackApplication.MEET_API);
                    HttpClient httpclient = new DefaultHttpClient();
                    if (StarTrackApplication.sessionId != null) {
                        httppost.addHeader("x-auth-id", StarTrackApplication.sessionId);
                    }
                    Log.d("TAG1", "p1 " + StarTrackApplication.sessionId);
                    HttpEntity entity = new ByteArrayEntity(request.getBytes("UTF-8"));
                    Log.d("TAG1", "p2");
                    httppost.setHeader("Content-Type", "application/json");
                    Log.d("TAG1", "p3");
                    httppost.setEntity(entity);
                    Log.d("TAG1", "p4");
                    response = httpclient.execute(httppost);
                    Log.d("TAG1", "p5");
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    Log.d("TAG1", "p6");
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        Log.d("TAG1", line);
                        json = json + line;
                    }
                    Log.d("TAG1", "p7");
                    reader.close();
                    Log.d("TAG1", "p8");
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("TAG1", "p91 " + json);
                Log.d("TAG1", "p92 " + response);
                if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    Log.d("TAG1", "p10");
                    try {
                        Log.d("TAG1", "p11");
                        JSONObject jObject = new JSONObject(json);
                        Log.d("TAG1", "temp=" + jObject.toString());

                        return jObject.getString("content");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                mEtMessage.setText(s);
                mEtMessage.setVisibility(View.VISIBLE);
                mBSend.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.GONE);
            }
        }.execute();
    }
}
