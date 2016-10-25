package io.github.doyouevendev.popularmoviesapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


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
        new FetchMovieTask().execute(sortOrder);
    }


    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {
        final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        /* Receives whether to fetch top rated or most popular movies from
        *  user preferences */
        @Override
        protected Movie[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            // Will contain the raw JSON response as a string.
            String movieJsonStr = fetchFromApi(params);

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                mMovieAdapter.clear();
                for (Movie movie : result) {
                    mMovieAdapter.add(movie);
                }
            }
        }

        private String fetchFromApi(String... params) {
            String movieJsonStr;

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                // Construct the URL for the TheMovieDB query
                // https://api.themoviedb.org/3/movie/popular?api_key=[APIKEY]&language=en-US
                // https://api.themoviedb.org/3/movie/top_rated?api_key=[APIKEY]&language=en-US
                final String THEMOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie";
                final String APIKEY_PARAM = "api_key";
                final String LANGUAGE_PARAM = "language";
                // In the future the user might be able to choose language
                String language = "en-US";

                Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                        .appendPath(params[0]) // toprated or popular parameter
                        .appendQueryParameter(APIKEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .appendQueryParameter(LANGUAGE_PARAM, language)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to TheMovieDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return movieJsonStr;
        }

        private Movie[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String THEMOVIEDB_RESULT = "results";
            final String THEMOVIEDB_ID = "id";
            final String THEMOVIEDB_TITLE = "original_title";
            final String THEMOVIEDB_DATE = "release_date";
            final String THEMOVIEDB_SCORE = "vote_average";
            final String THEMOVIEDB_DESCRIPTION = "overview";
            final String THEMOVIEDB_POSTER = "poster_path";
            final String THEMOVIEDB_BACKDROP = "backdrop_path";
            // In the future the user might be able to choose how many movies
            // to show
            int numberOfMovies = 20;

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(THEMOVIEDB_RESULT);

            Movie[] resultMovie = new Movie[numberOfMovies];
            for (int i = 0; i < movieArray.length(); i++) {
                // Get the JSON object representing the movie
                JSONObject movieJsonObject = movieArray.getJSONObject(i);
                Movie movie = new Movie();

                movie.setId(movieJsonObject.getInt(THEMOVIEDB_ID));
                movie.setTitle(movieJsonObject.getString(THEMOVIEDB_TITLE));
                movie.setDate(movieJsonObject.getString(THEMOVIEDB_DATE));
                movie.setScore(movieJsonObject.getDouble(THEMOVIEDB_SCORE));
                movie.setDescription(movieJsonObject.getString(THEMOVIEDB_DESCRIPTION));
                // .substring(1) is used to remove the leading '/'
                movie.setPosterUri(movieJsonObject.getString(THEMOVIEDB_POSTER).substring(1));
                movie.setBackdropUri(movieJsonObject.getString(THEMOVIEDB_BACKDROP).substring(1));

                resultMovie[i] = movie;
            }

            return resultMovie;

        }
    }
}
