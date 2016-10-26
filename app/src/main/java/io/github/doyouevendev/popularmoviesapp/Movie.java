package io.github.doyouevendev.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

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
    @SerializedName("release_date")
    private String date;
    @SerializedName("vote_average")
    private double score;
    private String overview;
    @SerializedName("poster_path")
    private String posterUri;
    @SerializedName("backdrop_path")
    private String backdropUri;
    private String trailerUri;

    public Movie () {
        this.title = "NULL";
    }

    public Movie(int id, String title, String date, double score,
                 String overview, String posterUri, String backdropUri,
                 String trailerUri) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.score = score;
        this.overview = overview;
        this.posterUri = posterUri;
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
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
        score = in.readDouble();
        overview = in.readString();
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
        dest.writeDouble(score);
        dest.writeString(overview);
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
