package io.github.doyouevendev.popularmoviesapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface TheMovieDbService {
    @GET("{sort}")
    Call<TheMovieDbResponseMovie> getMovies(
            @Path("sort") String sort,
            @Query("api_key") String apiKey,
            @Query("language") String language);

    @GET("{id}/videos")
    Call<TheMovieDbResponseVideo> getVideos(
            @Path("id") int id,
            @Query("api_key") String apiKey,
            @Query("language") String language);
}
