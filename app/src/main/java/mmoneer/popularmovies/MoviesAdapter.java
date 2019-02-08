package mmoneer.popularmovies;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Mohammad Moneer on 9/23/2016.
 */

public class MoviesAdapter extends BaseAdapter {
    private Context mContext;

    public final static String BASE_URI_185 = "http://image.tmdb.org/t/p/w185";
    public final static String BASE_URI_342 = "http://image.tmdb.org/t/p/w342";
    String[] posterPaths;
    List<byte[]> posterImages;
    private int adapterCount;
    private int selectSource;
    public MoviesAdapter(Context c, List<String> paths, List<byte[]> images, int select) {

        selectSource = select;
        mContext = c;
        if (paths != null)
            posterPaths = paths.toArray(new String[0]);
        if (images != null)
            posterImages = images;
        if (select == 0)
            adapterCount = paths.size();
        else
            adapterCount = images.size();
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
        ImageView imageView;

        if (convertView == null) {
            View mGridItem = LayoutInflater.from(mContext).inflate(R.layout.movies_grid_item, parent, false);
            imageView = (ImageView) mGridItem.findViewById(R.id.movies_grid_image);
            convertView = mGridItem;
        }
        else {
            imageView = (ImageView) convertView.findViewById(R.id.movies_grid_image);
        }
        if (selectSource == 0)
            Picasso.with(mContext).load(BASE_URI_185 + posterPaths[position]).into(imageView);
        else {
            Bitmap bitmap = BitmapFactory.decodeByteArray(posterImages.get(position), 0, posterImages.get(position).length);
            imageView.setImageBitmap(bitmap);
        }
        return convertView;
    }

}