package io.github.doyouevendev.popularmoviesapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MovieDetailFragment extends Fragment {

    final String RATING_STRING = "Average rating: ";

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // Gets the movie data
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Movie.EXTRA_NAME)) {
            final Movie selectedMovie = (Movie) intent.getParcelableExtra(Movie.EXTRA_NAME);

            /* Trailer */
            ImageView trailerImgView = ((ImageView) rootView.findViewById(R.id.movie_detail_trailer_img));
            // If the movie has no backdrop, load a placeholder img
            if (selectedMovie.getBackdropUri() == null) {
                Picasso.with(getContext()).load(R.drawable.missing_poster).into(trailerImgView);
            }
            else {
                // Build the backdrop img URL with the corresponding size
                Uri trailerImgUri = Uri.parse(Movie.THE_MOVIE_DB_URL).buildUpon()
                        .appendPath(Movie.BACKDROP_SIZE)
                        .appendPath(selectedMovie.getBackdropUri())
                        .build();
                Picasso.with(getContext()).load(trailerImgUri.toString()).into(trailerImgView);
            }

            // TODO: Trailer video
            // Sets listener to launch implicit intent to watch the trailer when
            // the play button is clicked
/*            ImageView playButtonView = ((ImageView) rootView.findViewById(R.id.movie_detail_play_button));
            playButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String trailerUri = selectedMovie.getTrailerUri();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(trailer);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });*/

            ImageView posterView = ((ImageView) rootView.findViewById(R.id.movie_detail_poster));
            // If the movie has no poster, load a placeholder img
            if (selectedMovie.getPosterUri() == null) {
                Picasso.with(getContext()).load(R.drawable.missing_poster).into(posterView);
            }
            else {
                // Build the poster img URL with the corresponding size
                Uri posterUri = Uri.parse(Movie.THE_MOVIE_DB_URL).buildUpon()
                        .appendPath(Movie.POSTER_SIZE)
                        .appendPath(selectedMovie.getPosterUri())
                        .build();
                Picasso.with(getContext()).load(posterUri.toString()).into(posterView);
            }

            /* Details */
            ((TextView) rootView.findViewById(R.id.movie_detail_title))
                    .setText(selectedMovie.getTitle());

            String date = parseDate(selectedMovie.getDate());
            ((TextView) rootView.findViewById(R.id.movie_detail_date))
                    .setText(date);

            ((TextView) rootView.findViewById(R.id.movie_detail_rating))
                    .setText(RATING_STRING +
                            Double.toString(selectedMovie.getScore()));

            /* Overview */
            ((TextView) rootView.findViewById(R.id.movie_detail_description))
                    .setText(selectedMovie.getDescription());
        }

        return rootView;
    }

    private String parseDate(String inputDate) {
        final String INPUT_FORMAT = "yyyy-MM-dd";
        final String OUTPUT_FORMAT = "MMMM yyyy";
        String outputDate = "";

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(INPUT_FORMAT);
            Date dateObject = inputFormat.parse(inputDate);
            SimpleDateFormat outputFormat = new SimpleDateFormat(OUTPUT_FORMAT);
            outputDate = outputFormat.format(dateObject);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return outputDate;
    }
}
