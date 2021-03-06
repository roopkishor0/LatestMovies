package com.example.latestmovies.data.api

import com.example.latestmovies.data.vo.MovieDetails
import com.example.latestmovies.data.vo.MovieResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDBInterface {

    //https://api.themoviedb.org/3/movie/popular?api_key=4219ca88acfda5677ac89b0fd5c144a2
    //https://api.themoviedb.org/3/movie/399566?api_key=4219ca88acfda5677ac89b0fd5c144a2
    //https://api.themoviedb.org/3/

    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id")id : Int) : Single<MovieDetails>

    @GET("movie/popular")
    fun getPopularMovies(@Query("page")page : Int) : Single<MovieResponse>


}