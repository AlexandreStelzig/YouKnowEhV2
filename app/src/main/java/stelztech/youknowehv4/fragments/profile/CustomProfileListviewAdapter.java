package stelztech.youknowehv4.fragments.profile;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import stelztech.youknowehv4.R;

/**
 * Created by alex on 12/29/2017.
 */

public class CustomProfileListviewAdapter extends BaseAdapter {

    private Context mContext;
    private Activity mActivity;
    private String[] titles;
    private int[] images;

    public CustomProfileListviewAdapter(Context context, Activity activity, String[] textlist, int[] imageIds) {
        mContext = context;
        mActivity = activity;
        titles = textlist;
        images = imageIds;

    }

    public int getCount() {
        return titles.length;
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = mActivity.getLayoutInflater();
        View row;
        row = inflater.inflate(R.layout.custom_profile_listview, parent, false);
        TextView title;
        ImageView i1;
        i1 = (ImageView) row.findViewById(R.id.imgIcon);
        title = (TextView) row.findViewById(R.id.txtTitle);
        title.setText(titles[position]);
        i1.setImageResource(images[position]);

        return (row);
    }
}