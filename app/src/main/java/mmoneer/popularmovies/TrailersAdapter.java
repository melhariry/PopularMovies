package mmoneer.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

/**
 * Created by Mohammad Moneer on 9/24/2016.
 */

public class TrailersAdapter extends BaseAdapter {
    private Context mContext;

    String[] trailerPaths;
    private int adapterCount;
    public TrailersAdapter(Context c, List<String> trailers) {

        mContext = c;
        trailerPaths = trailers.toArray(new String[0]);
        adapterCount = trailers.size();
    }

    public int getCount() {
        return adapterCount;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView trailerTextView;

        if (convertView == null) {
            View mListItem = LayoutInflater.from(mContext).inflate(R.layout.trailers_list_item, parent, false);
            trailerTextView = (TextView)mListItem.findViewById(R.id.trailer_textview);
            convertView = mListItem;
        }
        else {
            trailerTextView = (TextView)convertView.findViewById(R.id.trailer_textview);
        }

        trailerTextView.setText(trailerTextView.getText() + " " + ++position);
        return convertView;
    }
}
