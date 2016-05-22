package com.startrack.android.startrack_android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.startrack.android.startrack_android.R;
import com.startrack.android.startrack_android.adapter.MatchesAdapter;
import com.startrack.android.startrack_android.app.StarTrackApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vrogovskiy on 2/11/16.
 */
public class MatchesActivity extends Activity implements IActivityWithHandler {

    private TextView tvSelectUnselect;
    private ListView listView;
    private RelativeLayout relativeLayout;
    //    private LinearLayout linearLayout;
    private Button meetButton;
    private ProgressBar progressBar;
    private TextView titleText;

    public static List<Integer> checkedItems = new ArrayList<Integer>();
    private boolean isSelected;

    private Handler handler;

    public static String MEET_RESULT_MSG_PROPERTY_NAME = "MeetResultMsgPropertyName";
    public static String MEET_RESULT_MSG_PROPERTY_VALUE_SUCCESSFUL = "MeetResultMsgPropertyValueSuccessful";
    public static String MEET_RESULT_MSG_PROPERTY_VALUE_UNSUCCESSFUL = "MeetResultMsgPropertyValueUnsuccessful";
    private MatchesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        init();
        mAdapter = new MatchesAdapter(this, StarTrackApplication.searchResults);

    }

    @Override
    protected void onResume() {
        MatchesActivity.checkedItems.clear();
        listView.setAdapter(mAdapter);
        hideProgressAndShowViews();
        updateSelectButton();
        super.onResume();
    }

    private void init() {
        this.tvSelectUnselect = (TextView) findViewById(R.id.tv_select_unselect);
        this.listView = (ListView) findViewById(R.id.SearchResultListView);
        this.meetButton = (Button) findViewById(R.id.MeetButton);
        this.relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
//        this.linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Typeface custom_font_regular = Typeface.createFromAsset(getAssets(), "fonts/Bariol_Regular.otf");
        this.titleText = (TextView) findViewById(R.id.TitleText);
        this.titleText.setTypeface(custom_font_regular);
        this.tvSelectUnselect.setTypeface(custom_font_regular);
        this.tvSelectUnselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelected) {
                    MatchesActivity.checkedItems.clear();
                    tvSelectUnselect.setText("Select All");
                } else {
                    for (int i = 0; i < StarTrackApplication.searchResults.size(); i++) {
                        MatchesActivity.checkedItems.add(
                                StarTrackApplication.searchResults.get(i).getUserId());
                    }
                    tvSelectUnselect.setText("Unselect");
                }
                isSelected = !isSelected;
                mAdapter.notifyDataSetChanged();
            }
        });
        initHandler();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int userId = StarTrackApplication.searchResults.get(position).getUserId();
                if (MatchesActivity.checkedItems.contains(userId)) {
                    MatchesActivity.checkedItems.remove((Object) userId);
                } else {
                    MatchesActivity.checkedItems.add(userId);
                }
                updateSelectButton();
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initHandler() {
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (msg.getData().getString(MatchesActivity.MEET_RESULT_MSG_PROPERTY_NAME) != null) {
                    if (msg.getData().getString(MatchesActivity.MEET_RESULT_MSG_PROPERTY_NAME).equals(MatchesActivity.MEET_RESULT_MSG_PROPERTY_VALUE_SUCCESSFUL)) {
                        Toast.makeText(MatchesActivity.this, "Meeting request sent",
                                Toast.LENGTH_LONG).show();
                        hideProgressAndShowViews();
                    } else if (msg.getData().getString(MatchesActivity.MEET_RESULT_MSG_PROPERTY_NAME).equals(MatchesActivity.MEET_RESULT_MSG_PROPERTY_VALUE_UNSUCCESSFUL)) {
                        hideProgressAndShowViews();
                    }
                }
            }

        };
    }

    public void updateSelectButton() {
        if (MatchesActivity.checkedItems.size() > 0) {
            tvSelectUnselect.setText("Unselect");
            isSelected = true;
        } else {
            tvSelectUnselect.setText("Select All");
            isSelected = false;
        }
    }

    public void hideProgressAndShowViews() {
        this.progressBar.setVisibility(View.INVISIBLE);
        this.listView.setVisibility(View.VISIBLE);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showAuthorizarionDialogForXing() {

    }

    private void hideViewsAndShowProgress() {
        this.listView.setVisibility(View.INVISIBLE);
        this.progressBar.setVisibility(View.VISIBLE);
    }

    public void backButtonOnClick(View v) {
        this.onBackPressed();
    }

    public void meetButtonOnClick(View v) {
        if (MatchesActivity.checkedItems.size() > 0) {
            hideViewsAndShowProgress();
/*        if(StarTrackApplication.testModeFlag) {
            StarTrackApplication.meetParameters = new String[4];
            StarTrackApplication.meetParameters[0] = "421";
            StarTrackApplication.meetParameters[1] = "422";
            StarTrackApplication.meetParameters[2] = "423";
            StarTrackApplication.meetParameters[3] = "424";
        } else {

        }*/
//            new APIService.MeetOperation().execute(this);
            startActivity(new Intent(MatchesActivity.this, EditMessageActivity.class));
        } else {
            Toast.makeText(this, "Please tap on a card to send invitation to people you want to meet",
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