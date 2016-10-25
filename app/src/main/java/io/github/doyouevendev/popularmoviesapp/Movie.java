package io.github.doyouevendev.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    // Constants holding info for handling images urls
    final static public String THE_MOVIE_DB_URL = "http://image.tmdb.org/t/p";

    // Possible poster sizes:
    //  "w92", "w154", "w185", "w342", "w500", "w780", or "original".
    final static public String POSTER_SIZE = "w185";
    final static public String BACKDROP_SIZE = "w342";

    // Used as key for intent.putExtra()
    final static public String EXTRA_NAME = "selected_movie";

    private int id;
    private String title;
    private String date;
    private int length;
    private double score;
    private String description;
    private String posterUri;
    private String backdropUri;
    private String trailerUri;

    public Movie () {
        this.title = "NULL";
    }

    public Movie(int id, String title, String year, int length, double score,
                 String description, String posterBigUri, String posterMediumUri,
                 String backdropUri, String trailerUri) {
        this.id = id;
        this.title = title;
        this.date = year;
        this.length = length;
        this.score = score;
        this.description = description;
        this.posterUri = posterBigUri;
        this.backdropUri = backdropUri;
        this.trailerUri = trailerUri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosterUri() {
        return posterUri;
    }

    public void setPosterUri(String posterUri) {
        this.posterUri = posterUri;
    }

    public String getBackdropUri() {
        return backdropUri;
    }

    public void setBackdropUri(String backdropUri) {
        this.backdropUri = backdropUri;
    }

    public String getTrailerUri() {
        return trailerUri;
    }

    public void setTrailerUri(String trailerUri) {
        this.trailerUri = trailerUri;
    }

    /* Parcelable part */

    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        date = in.readString();
        length = in.readInt();
        score = in.readDouble();
        description = in.readString();
        posterUri = in.readString();
        backdropUri = in.readString();
        trailerUri = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(date);
        dest.writeInt(length);
        dest.writeDouble(score);
        dest.writeString(description);
        dest.writeString(posterUri);
        dest.writeString(backdropUri);
        dest.writeString(trailerUri);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}
