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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MovieDetailFragment extends Fragment {

    final String RATING_STRING = "Average rating: ";
    View rootView;

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
        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

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
                        // .substring(1) is needed to remove the leading '/'
                        .appendPath(selectedMovie.getBackdropUri().substring(1))
                        .build();
                Picasso.with(getContext()).load(trailerImgUri.toString()).into(trailerImgView);
            }
            // Asynchronously handles fetching the trailer and setting a click
            // listener on the play button
            setTrailer(selectedMovie);

            ImageView posterView = ((ImageView) rootView.findViewById(R.id.movie_detail_poster));
            // If the movie has no poster, load a placeholder img
            if (selectedMovie.getPosterUri() == null) {
                Picasso.with(getContext()).load(R.drawable.missing_poster).into(posterView);
            }
            else {
                // Build the poster img URL with the corresponding size
                Uri posterUri = Uri.parse(Movie.THE_MOVIE_DB_URL).buildUpon()
                        .appendPath(Movie.POSTER_SIZE)
                        // .substring(1) is needed to remove the leading '/'
                        .appendPath(selectedMovie.getPosterUri().substring(1))
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
                    .setText(selectedMovie.getOverview());
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


    private void setTrailer(Movie movie) {
        // In the future, the user might be able to choose the language
        final String language = "en-US";
        final String THEMOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(THEMOVIEDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TheMovieDbService theMovieDbService = retrofit.create(TheMovieDbService.class);

        Call<TheMovieDbResponseVideo> call = theMovieDbService.getVideos(
                movie.getId(),
                BuildConfig.THE_MOVIE_DB_API_KEY,
                language);

        call.enqueue(new Callback<TheMovieDbResponseVideo>() {
                         @Override
                         public void onResponse(Call<TheMovieDbResponseVideo> call, Response<TheMovieDbResponseVideo> response) {
                             if (response.body().getResults().size() > 0) {
                                 setPlayButtonListener(response.body().getResults());
                             }
                             else {
                                 setNotAvailableListener();
                             }
                         }

                         @Override
                         public void onFailure(Call<TheMovieDbResponseVideo> call, Throwable t) {
                             final String message = "Couldn't load trailer, check your internet connection";
                             Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                         }
                     }
        );
    }

    /* Sets listener to launch implicit intent to watch the trailer when
    the play button is clicked */
    private void setPlayButtonListener(List<Video> videos) {
        final Uri trailerUri = buildTrailerUri(videos.get(0));

        ImageView playButtonView = ((ImageView) rootView.findViewById(R.id.movie_detail_play_button));
        playButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(trailerUri);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    /* Sets listener to show the user a message when the selected trailer is not
        available */
    private void setNotAvailableListener() {
        ImageView playButtonView = ((ImageView) rootView.findViewById(R.id.movie_detail_play_button));
        playButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = "Trailer not available";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Uri buildTrailerUri(Video video) {
        final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch";
        final String QUERY_PARAMETER = "v";
        return Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAMETER, video.getKey())
                .build();
    }
}
