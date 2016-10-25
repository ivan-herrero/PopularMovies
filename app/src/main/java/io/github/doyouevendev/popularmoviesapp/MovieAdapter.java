package io.github.doyouevendev.popularmoviesapp;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {

    private final String LOG_TAG = MovieAdapter.class.getSimpleName();
    final LayoutInflater mInflater;

    public MovieAdapter(Context context, int layoutRes, int imageViewRes, List<Movie> movies) {
        super(context, layoutRes, imageViewRes, movies);
        mInflater = LayoutInflater.from(getContext());
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView view = (ImageView) convertView;

        if (view == null) {
            view = (ImageView) mInflater.inflate(R.layout.grid_item_movie, parent, false);
        }

        Movie movie = getItem(position);

        // If the movie has no poster, load a placeholder img
        if (movie.getPosterUri() == null) {
            Picasso.with(getContext()).load(R.drawable.missing_poster).into(view);
        } else {
            // Build the poster img URL with the corresponding size
            Uri posterUri = Uri.parse(Movie.THE_MOVIE_DB_URL).buildUpon()
                    .appendPath(Movie.POSTER_SIZE)
                    .appendPath(movie.getPosterUri())
                    .build();
            Picasso.with(getContext()).load(posterUri.toString()).into(view);
        }

        return view;
    }
}
