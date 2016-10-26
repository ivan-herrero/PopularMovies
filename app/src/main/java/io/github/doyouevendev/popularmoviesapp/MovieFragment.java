package io.github.doyouevendev.popularmoviesapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MovieFragment extends Fragment {

    private MovieAdapter mMovieAdapter;

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        // Custom ArrayAdapter to supply ImageViews to the GridView
        mMovieAdapter = new MovieAdapter(
                getActivity(), R.layout.grid_item_movie,
                R.id.movie_image, new ArrayList<Movie>());

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridView.setAdapter(mMovieAdapter);

        // On click launches MovieDetailActivity and sends the selected movie as
        // a parcelable
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) parent.getAdapter().getItem(position);
                Intent detailIntent = new Intent(getActivity(), MovieDetailActivity.class)
                        .putExtra(Movie.EXTRA_NAME, (Parcelable) movie);
                startActivity(detailIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(getString(R.string.pref_general_sortorder_key),
                getString(R.string.pref_general_sortorder_default));

        // In the future, the user might be able to choose the language
        final String language = "en-US";
        getMovieData(sortOrder, language);
    }

    private void getMovieData(String sortOrder, String language) {
        final String THEMOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(THEMOVIEDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TheMovieDbService theMovieDbService = retrofit.create(TheMovieDbService.class);

        Call<TheMovieDbResponseMovie> call = theMovieDbService.getMovies(
                sortOrder,
                BuildConfig.THE_MOVIE_DB_API_KEY,
                language);

        call.enqueue(new Callback<TheMovieDbResponseMovie>() {
                         @Override
                         public void onResponse(Call<TheMovieDbResponseMovie> call, Response<TheMovieDbResponseMovie> response) {
                             if (response != null) {
                                 List<Movie> moviesList = response.body().getResults();
                                 mMovieAdapter.clear();
                                 for (Movie movie : moviesList) {
                                     mMovieAdapter.add(movie);
                                 }
                             }
                         }

                         @Override
                         public void onFailure(Call<TheMovieDbResponseMovie> call, Throwable t) {
                             final String message = "Couldn't load movies, " +
                                     "check your internet connection";
                             Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                         }
                     }
        );
    }
}
