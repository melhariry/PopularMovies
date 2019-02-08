package mmoneer.popularmovies.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    private static MovieDbHelper sInstance;

    //Db name
    private static final String DATABASE_NAME = "MovieDb";

    // movies table name
    private static final String TABLE_MOVIES = "Movies";

    //movie table columns
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE= "title";
    private static final String KEY_PLOT = "plot";
    private static final String KEY_RATING = "rating";
    private static final String KEY_RELEASE_DATE = "releaseDate";
    private static final String KEY_FAVOURITE = "favourite";
    private static final String KEY_POSTER = "poster";
    private static final String KEY_IMDB_ID = "imdbid";
    private static final String KEY_USER_NAMES = "user_names";
    private static final String KEY_REVIEW_CONTENTS = "review_contents";
    private static final String KEY_TRAILE_LIST = "trailers";
	private static final String KEY_RUN_TIME = "run_time";

    private static final String userNamesTilte = "userNamesTiltle";
    private static final String reviewContentsTilte = "reviewContentsTitle";
    private static final String trailerListTitle = "trailersTitle";

    public static synchronized MovieDbHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new MovieDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MOVIES_TABLE = "CREATE TABLE " + TABLE_MOVIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_PLOT + " TEXT,"
                + KEY_RATING + " REAL,"
                + KEY_RELEASE_DATE + " DATE,"
                + KEY_FAVOURITE + " INT,"
                + KEY_POSTER + " BLOB,"
                + KEY_IMDB_ID + " INT,"
                +KEY_USER_NAMES + " TEXT,"
                +KEY_REVIEW_CONTENTS + " TEXT,"
                + KEY_TRAILE_LIST + " TEXT," 
				+ KEY_RUN_TIME  +" INT"
                + ")";

        db.execSQL(CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //drop table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        //create table again
        onCreate(db);
    }

    private String stringListToString(ArrayList<String> list,String title)  {
        try {
            JSONObject json = new JSONObject();
            json.put(title, new JSONArray(list));
            return json.toString();
        }
        catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }


    private ArrayList<String> stringToStringList(String jsonString, String title) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray items = json.optJSONArray(title);
            for (int i = 0; i < items.length(); i++) {
                list.add( items.optString(i));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }
	
    public void addMovie(Movie currentMovie){
        SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(KEY_FAVOURITE, currentMovie.isFavourite());
            values.put(KEY_PLOT, currentMovie.getPlot());
            values.put(KEY_POSTER, currentMovie.getPoster());
            values.put(KEY_RATING, currentMovie.getRating());
            values.put(KEY_RELEASE_DATE,currentMovie.getReleaseDate());
            values.put(KEY_TITLE,currentMovie.getTitle());
            values.put(KEY_IMDB_ID,currentMovie.getImdbId());
            values.put(KEY_USER_NAMES, stringListToString(currentMovie.getUserNames(), this.userNamesTilte));
            values.put(KEY_REVIEW_CONTENTS, stringListToString(currentMovie.getReviewContents(), this.reviewContentsTilte));
            values.put(KEY_TRAILE_LIST, stringListToString(currentMovie.getTrailerList(), this.trailerListTitle));
			values.put(KEY_RUN_TIME, currentMovie.getRunTime());

            db.insert(TABLE_MOVIES, null, values);


        db.close();
    }

    public Movie getMovie(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        String [] columns = {KEY_ID, KEY_TITLE,  KEY_PLOT, KEY_RATING, KEY_RELEASE_DATE, KEY_FAVOURITE , KEY_POSTER,KEY_IMDB_ID,KEY_USER_NAMES,KEY_REVIEW_CONTENTS,KEY_TRAILE_LIST, KEY_RUN_TIME};
        String [] selectionArgs = {String.valueOf(id)};
        String select = KEY_IMDB_ID + "=?";

        Cursor cursor = db.query(TABLE_MOVIES, columns, select, selectionArgs, null, null, null, null );
        Movie movie = null;
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            movie = new Movie(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3) , cursor.getString(4) , cursor.getInt(5), cursor.getBlob(6),cursor.getInt(7),stringToStringList(cursor.getString(8), this.userNamesTilte),stringToStringList(cursor.getString(9), this.reviewContentsTilte), stringToStringList(cursor.getString(10),this.trailerListTitle) , cursor.getInt(11));
        }

        db.close();
        return movie;
    }

    public ArrayList<Movie> getAllMovies(){
        ArrayList<Movie> movies = new ArrayList<Movie>();

        String selectQuery = "SELECT * FROM "+ TABLE_MOVIES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery ,null);

        if(cursor.moveToFirst()){
            do {
                Movie currentMovie = new Movie();
                currentMovie.setId(cursor.getInt(0));
                currentMovie.setTitle(cursor.getString(1));
                currentMovie.setPlot(cursor.getString(2));
                currentMovie.setRating(cursor.getDouble(3));
                currentMovie.setReleaseDate(cursor.getString(4));
                currentMovie.setIsFavourite(cursor.getInt(5));
                currentMovie.setPoster(cursor.getBlob(6));
                currentMovie.setImdbId(cursor.getInt(7));
                currentMovie.setUserNames(stringToStringList(cursor.getString(8), this.userNamesTilte));
                currentMovie.setReviewContents(stringToStringList(cursor.getString(9), this.reviewContentsTilte));
                currentMovie.setTrailerList(stringToStringList(cursor.getString(10), this.trailerListTitle));
				currentMovie.setRunTime(cursor.getInt(11));
				
                movies.add(currentMovie);
            }while(cursor.moveToNext());
        }
        db.close();
        return movies;
    }

    public void addToFavourites(Movie movie){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, movie.getId());
        values.put(KEY_TITLE, movie.getTitle());
        values.put(KEY_RELEASE_DATE,movie.getReleaseDate());
        values.put(KEY_FAVOURITE,1);
        values.put(KEY_RATING,movie.getRating());
        values.put(KEY_PLOT,movie.getPlot());
        values.put(KEY_POSTER,movie.getPoster());
        values.put(KEY_IMDB_ID, movie.getImdbId());
        values.put(KEY_USER_NAMES, stringListToString(movie.getUserNames(), this.userNamesTilte));
        values.put(KEY_REVIEW_CONTENTS, stringListToString(movie.getReviewContents(), this.reviewContentsTilte));
        values.put(KEY_TRAILE_LIST, stringListToString(movie.getTrailerList(), this.trailerListTitle));
		values.put(KEY_RUN_TIME, movie.getRunTime());
		
        db.update(TABLE_MOVIES, values, KEY_ID + "= ? ", new String[]{String.valueOf(movie.getId())});
        db.close();
    }

    public void removeMovie(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_MOVIES, KEY_IMDB_ID + "= ? ", new String[]{String.valueOf(id)});

        db.close();
    }

    public ArrayList<Movie> getFavouriteMovies(){
        ArrayList<Movie> movies = new ArrayList<Movie>();

        String selectQuery = "SELECT * FROM "+ TABLE_MOVIES+" WHERE "+ KEY_FAVOURITE +"=1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery ,null);

        if(cursor.moveToFirst()){
            do {
                Movie currentMovie = new Movie();
                currentMovie.setId(cursor.getInt(0));
                currentMovie.setTitle(cursor.getString(1));
                currentMovie.setPlot(cursor.getString(2));
                currentMovie.setRating(cursor.getDouble(3));
                currentMovie.setReleaseDate(cursor.getString(4));
                currentMovie.setIsFavourite(cursor.getInt(5));
                currentMovie.setPoster(cursor.getBlob(6));
                currentMovie.setImdbId(cursor.getInt(7));
                currentMovie.setUserNames(stringToStringList(cursor.getString(8), this.userNamesTilte));
                currentMovie.setReviewContents(stringToStringList(cursor.getString(9), this.reviewContentsTilte));
                currentMovie.setTrailerList(stringToStringList(cursor.getString(10), this.trailerListTitle));
				currentMovie.setRunTime(cursor.getInt(11));
				
                movies.add(currentMovie);
            }while(cursor.moveToNext());
        }

        db.close();
        return movies;
    }


}
