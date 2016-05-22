package com.startrack.android.startrack_android.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.startrack.android.startrack_android.R;
import com.startrack.android.startrack_android.activity.MatchesActivity;
import com.startrack.android.startrack_android.model.Profile;
import com.startrack.android.startrack_android.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vrogovskiy on 2/11/16.
 */
public class MatchesAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Activity activity;

    private List<Profile> profilesArray = new ArrayList<Profile>();

    public MatchesAdapter(Activity a, List<Profile> profileArray) {
        this.profilesArray = profileArray;
        activity = a;
        inflater = (LayoutInflater) activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return profilesArray.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Profile profile = profilesArray.get(position);

        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {
            Typeface custom_font_regular = Typeface.createFromAsset(activity.getAssets(), "fonts/Bariol_Regular.otf");
            Typeface custom_font_bold = Typeface.createFromAsset(activity.getAssets(), "fonts/Bariol_Bold.otf");
            vi = inflater.inflate(R.layout.matches_item_list, null);
            holder = new ViewHolder();
            holder.image = (ImageView) vi.findViewById(R.id.photo);
            holder.name = (TextView) vi.findViewById(R.id.name);
            holder.name.setTypeface(custom_font_bold);
            holder.title = (TextView) vi.findViewById(R.id.title);
            holder.title.setTypeface(custom_font_regular);
            holder.initials = (TextView) vi.findViewById(R.id.initials);
            holder.initials.setTypeface(custom_font_regular);
            holder.location = (TextView) vi.findViewById(R.id.location);
            holder.location.setTypeface(custom_font_regular);
            holder.source = (TextView) vi.findViewById(R.id.source);
            holder.source.setTypeface(custom_font_regular);
            holder.checkBoxImageView = (ImageView) vi.findViewById(R.id.checkboxImageView);

            if (MatchesActivity.checkedItems.contains(vi.getTag()))
                holder.checkBoxImageView.setImageResource(R.drawable.dot_selected_matches);
            else
                holder.checkBoxImageView.setImageResource(R.drawable.dot_matches);

            holder.checkBoxImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int userId = (Integer) v.getTag();
                    if (MatchesActivity.checkedItems.contains(userId)) {
                        MatchesActivity.checkedItems.remove((Object) userId);
                        ImageView imageView = (ImageView) v;
                        imageView.setImageResource(R.drawable.dot_matches);
                    } else {
                        MatchesActivity.checkedItems.add(userId);
                        ImageView imageView = (ImageView) v;
                        imageView.setImageResource(R.drawable.dot_selected_matches);
                    }
                    ((MatchesActivity) activity).updateSelectButton();
                }
            });
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }


//        if (!profile.getProfilePic().equals("")) {
//            holder.initials.setVisibility(View.INVISIBLE);
//            //holder.image.setVisibility(View.VISIBLE);
////            new DownloadImageTask(activity, holder.image)
////                    .execute(profile.getProfilePic());
//
//        } else {
//        holder.initials.setVisibility(View.VISIBLE);
        //holder.image.setVisibility(View.INVISIBLE);
//        holder.image.setImageResource(R.drawable.no_photo);

        String initials = "NN";
        StringBuilder initialsString = new StringBuilder();
        if (profile.editableProperties.get(0).hasValue()) {
            initialsString.append(profile.editableProperties.get(0).getPropertyValue().charAt(0));
        }
        if (profile.editableProperties.get(1).hasValue()) {
            initialsString.append(profile.editableProperties.get(1).getPropertyValue().charAt(0));
        }

        Log.d("TAG1", "initString = " + initialsString.toString());

        if (!TextUtils.isEmpty(initialsString)) {
            initials = initialsString.toString().toUpperCase();
        }
        Log.d("TAG1", "initials = " + initials);

        holder.initials.setText(initials);

        ImageUtils.setImage(profile.getProfilePic(), holder.image);

//        }
        if (MatchesActivity.checkedItems.contains(profile.getUserId())) {
            holder.checkBoxImageView.setImageResource(R.drawable.dot_selected_matches);
        } else {
            holder.checkBoxImageView.setImageResource(R.drawable.dot_matches);
        }
        holder.checkBoxImageView.setTag(profile.getUserId());

        StringBuilder name = new StringBuilder();
        if (profile.editableProperties.get(0).hasValue()) {
            name.append(profile.editableProperties.get(0).getPropertyValue());
            name.append(" ");
        } else {
            name.append("null ");
        }

        if (profile.editableProperties.get(1).hasValue()) {
            name.append(profile.editableProperties.get(1).getPropertyValue());
        } else {
            name.append("null");
        }
        holder.name.setText(name.toString());

        StringBuilder positionStringBuilder = new StringBuilder();
        if (profile.selectableProperties.get(10).hasValue()) {
            positionStringBuilder.append(profile.selectableProperties.get(10).getPropertyValue());
            if (profile.editableProperties.get(3).hasValue()) {
                positionStringBuilder.append(" at ");
                positionStringBuilder.append(profile.editableProperties.get(3).getPropertyValue());
            } else {
                positionStringBuilder.append(" at Unknown Company");
            }
        } else {
            positionStringBuilder.append("at Unknown Company");
        }
        holder.title.setText(positionStringBuilder.toString());

        boolean commaFlag = false;
        StringBuilder locationStringBuilder = new StringBuilder();
        if (profile.editableProperties.get(11).hasValue()) {
            locationStringBuilder.append("From " + profile.editableProperties.get(11).getPropertyValue());
            commaFlag = true;
        }
        if (profile.selectableProperties.get(8).hasValue()) {
            if (commaFlag) {
                locationStringBuilder.append(", ");
            }
            locationStringBuilder.append(profile.selectableProperties.get(8).getPropertyValue());
        }
        if (locationStringBuilder.length() > 0) {
            holder.location.setText(locationStringBuilder.toString());
        } else {
            holder.location.setText("From Undefined location");
        }
        switch (profile.getSocNetworkType()) {
            case 0:
                holder.source.setText("via StarTTrack");
                break;
            case 1:
                holder.source.setText("via Facebook");
                break;
            case 2:
                holder.source.setText("via LinkedIn");
                break;
            case 3:
                holder.source.setText("via Xing");
        }
        return vi;
    }

    public static int TAG_USER_ID = 3;


    public static class ViewHolder {

        public ImageView image;
        public TextView name;
        public TextView title;
        public TextView location;
        public TextView initials;
        public TextView source;
        public ImageView checkBoxImageView;
        public int userId;

    }

//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//        ImageView bmImage;
//        private Context mContext;
//
//        public DownloadImageTask(Context context, ImageView bmImage) {
//            mContext = context;
//            this.bmImage = bmImage;
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//            return mIcon11;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            if (result != null) {
//                bmImage.setImageBitmap(
//                        RoundedImageView.getCroppedBitmap(result,
//                                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 80,
//                                        mContext.getResources().getDisplayMetrics())));
//            }
//        }
//    }
}
