package mmoneer.popularmovies.data;


import android.graphics.Bitmap;

import java.sql.Blob;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Movie {

    private int id;

    private String title;

    private String plot;

    private double rating;

    private String releaseDate;

    private int isFavourite;

    private byte[] poster;

    private int imdbId;

    private ArrayList<String> userNames;

    private ArrayList<String> reviewContents;

    private ArrayList<String> trailerList;

    private int runTime;

    public Movie() {
    }

    public Movie( int id, String title, String plot, double rating, String releaseDate, int isFavourite , byte[] poster, int imdbId,  ArrayList<String> userNames, ArrayList<String> reviewContents, ArrayList<String> trailerList  , int runTime) {
        this.poster = poster;
        this.id = id;
        this.title = title;
        this.plot = plot;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.isFavourite = isFavourite;
        this.imdbId = imdbId;
        this.userNames = userNames;
        this.reviewContents = reviewContents;
        this.trailerList = trailerList;
        this.runTime = runTime;
    }

    public Movie(String title, String plot, double rating, String releaseDate, int isFavourite, byte[] poster, int imdbId,ArrayList<String> userNames, ArrayList<String> reviewContents, ArrayList<String> trailerList, int runTime) {
        this.title = title;
        this.plot = plot;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.poster = poster;
        this.isFavourite = isFavourite;
        this.imdbId = imdbId;
        this.userNames = userNames;
        this.reviewContents = reviewContents;
        this.trailerList = trailerList;
		this.runTime = runTime;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPlot() {
        return plot;
    }

    public double getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public int isFavourite() {
        return isFavourite;
    }

    public byte[] getPoster() {
        return poster;
    }

    public ArrayList<String> getUserNames() {
        return userNames;
    }

    public ArrayList<String> getReviewContents() {
        return reviewContents;
    }

    public int getImdbId() {
        return imdbId;
    }

    public ArrayList<String> getTrailerList() {
        return trailerList;
    }
	
	public int getRunTime(){
		return runTime;
	}

    public void setId(int id) {
        this.id = id;
    }


    public void setIsFavourite(int isFavourite) {
        this.isFavourite = isFavourite;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPoster(byte[] poster) {
        this.poster = poster;
    }

    public void setUserNames(ArrayList<String> userNames) {
        this.userNames = userNames;
    }

    public void setReviewContents(ArrayList<String> reviewContents) {
        this.reviewContents = reviewContents;
    }

    public void setTrailerList(ArrayList<String> trailerList) {
        this.trailerList = trailerList;
    }

    public void setImdbId(int imdbId) {
        this.imdbId = imdbId;
    }
	
	public void setRunTime(int runTime){
		this.runTime = runTime;
	}
}
