package com.startrack.android.startrack_android.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.startrack.android.startrack_android.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditMessageActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_message);
        ButterKnife.bind(this);
        getExtras();
        setTypefaces();
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

    }
}
