package com.startrack.android.startrack_android.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.startrack.android.startrack_android.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartTrackingActivity extends AppCompatActivity {

    @Bind(R.id.tv_start_tracking_label1)
    TextView mTvLabel1;
    @Bind(R.id.tv_start_tracking_label2)
    TextView mTvLabel2;
    @Bind(R.id.tv_start_tracking_label3)
    TextView mTvLabel3;
    @Bind(R.id.b_start_tracking)
    Button mBStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_tracking);
        ButterKnife.bind(this);

        setTypefaces();
    }

    private void setTypefaces() {
        Typeface robotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        Typeface robotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        mTvLabel1.setTypeface(robotoLight);
        mTvLabel2.setTypeface(robotoLight);
        mTvLabel3.setTypeface(robotoLight);
        mBStart.setTypeface(robotoRegular);
    }

    @OnClick(R.id.b_start_tracking)
    public void onStartTrackingClick() {
        startActivity(new Intent(StartTrackingActivity.this, SearchActivity.class));
    }
}
