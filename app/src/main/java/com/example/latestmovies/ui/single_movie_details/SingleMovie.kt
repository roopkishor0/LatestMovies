package com.example.latestmovies.ui.single_movie_details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.latestmovies.data.api.POSTER_URL
import com.example.latestmovies.data.api.TheMovieDBClient
import com.example.latestmovies.data.api.TheMovieDBInterface
import com.example.latestmovies.data.repository.NetworkState
import com.example.latestmovies.data.vo.MovieDetails
import com.example.latestmovies.databinding.ActivitySingleMovieBinding

class SingleMovie : AppCompatActivity() {

    private lateinit var viewModel: SingleMovieViewModel
    private lateinit var movieRepository: MovieDetailsRepository
    private lateinit var binding: ActivitySingleMovieBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleMovieBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val movieId : Int = intent.getIntExtra("id", 1)
        val apiService : TheMovieDBInterface = TheMovieDBClient.getClient()

        movieRepository = MovieDetailsRepository(apiService)
        viewModel = getViewModel(movieId)
        viewModel.movieDetails.observe(this, Observer {
            bindUI(it)
        })
        viewModel.networkState.observe(this, Observer {
            binding.progressBar.visibility = if(it == NetworkState.LOADING) View.VISIBLE else View.GONE
            binding.textError.visibility = if(it == NetworkState.ERROR) View.VISIBLE else View.GONE
        })
    }

    private fun bindUI(it: MovieDetails?) {
        binding.textMovieTitle.text = it?.title
        binding.textMovieSubtitle.text = it?.tagline
        binding.textBudget.text = it?.budget.toString()
        binding.textOverview.text = it?.overview
        binding.textRating.text = it?.rating.toString()
        binding.textReleaseDate.text = it?.releaseDate
        binding.textRevenue.text = it?.revenue.toString()
        binding.textRuntime.text = it?.runtime.toString()

        val moviePosterURL = POSTER_URL + it?.posterPath
        Glide.with(this)
            .load(moviePosterURL)
            .into(binding.imgMoviePoster);
    }

    /*private fun getViewModel(movieId : Int) : SingleMovieViewModel{
        viewModel = ViewModelProvider(this).get(SingleMovieViewModel(movieRepository, movieId)::class.java)
        return viewModel
    }*/

    private fun getViewModel(movieId : Int) : SingleMovieViewModel{

        //ViewModelProviders deprecated now
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory{

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SingleMovieViewModel(movieRepository, movieId) as T
            }
        })[SingleMovieViewModel :: class.java]
    }
}