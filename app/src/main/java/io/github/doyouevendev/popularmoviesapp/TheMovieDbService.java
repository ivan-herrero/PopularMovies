package io.github.doyouevendev.popularmoviesapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface TheMovieDbService {
    @GET("{sort}")
    Call<TheMovieDbResponse> getMovies(
            @Path("sort") String sort,
            @Query("api_key") String apiKey,
            @Query("language") String language);

    @GET("{id}/videos")
    Call<List<Movie>> videos(
            @Path("id") int id,
            @Query("api_key") String apiKey,
            @Query("language") String language);
}
