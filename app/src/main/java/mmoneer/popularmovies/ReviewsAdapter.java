package mmoneer.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import info.movito.themoviedbapi.model.Reviews;

/**
 * Created by Mohammad Moneer on 9/24/2016.
 */

public class ReviewsAdapter extends BaseAdapter {
    private Context mContext;
    private List<Reviews> movieReviews;
    private List<String> stringAuthors;
    private List<String> stringContents;
    private int adapterCount;
    private int selectSource;
    public ReviewsAdapter(Context c,  List<Reviews> r, List<String> authors, List<String> contents, int select) {

        mContext = c;
        if (r!= null)
            movieReviews = r;
        if (authors != null)
            stringAuthors = authors;
        if (contents != null)
            stringContents = contents;
        if (select == 0)
            adapterCount = r.size();
        else
            adapterCount = authors.size();

        selectSource = select;
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
        TextView headerTextView, bodyTextView;

        if (convertView == null) {
            View mListItem = LayoutInflater.from(mContext).inflate(R.layout.reviews_list_item, parent, false);
            headerTextView = (TextView)mListItem.findViewById(R.id.review_header_textview);
            bodyTextView = (TextView)mListItem.findViewById(R.id.review_body_textview);
            convertView = mListItem;
        }
        else {
            headerTextView = (TextView) convertView.findViewById(R.id.review_header_textview);
            bodyTextView = (TextView) convertView.findViewById(R.id.review_body_textview);
        }

        if (selectSource == 0) {
            headerTextView.setText(movieReviews.get(position).getAuthor());
            bodyTextView.setText(movieReviews.get(position).getContent());
        }
        else {
            headerTextView.setText(stringAuthors.get(position));
            bodyTextView.setText(stringContents.get(position));
        }
        return convertView;
    }
}
