package com.example.latestmovies.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val API_KEY = "4219ca88acfda5677ac89b0fd5c144a2"
const val BASE_URL = "https://api.themoviedb.org/3/"
const val POSTER_URL = "https://image.tmdb.org/t/p/w342"

//https://api.themoviedb.org/3/movie/popular?api_key=4219ca88acfda5677ac89b0fd5c144a2
//https://api.themoviedb.org/3/movie/399566?api_key=4219ca88acfda5677ac89b0fd5c144a2
//https://image.tmdb.org/t/p/w342/pgqgaUx1cJb5oZQQ5v0tNARCeBp.jpg

object TheMovieDBClient {

    fun getClient() : TheMovieDBInterface{
        val requestInterceptor = Interceptor{chain ->
            val  url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("api_key", API_KEY)
                    .build()

            val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()
            return@Interceptor chain.proceed(request)
        }
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TheMovieDBInterface::class.java)
    }

}