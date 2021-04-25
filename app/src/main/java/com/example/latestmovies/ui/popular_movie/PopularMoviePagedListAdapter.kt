package com.example.latestmovies.ui.popular_movie

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.latestmovies.R
import com.example.latestmovies.data.api.POSTER_URL
import com.example.latestmovies.data.repository.NetworkState
import com.example.latestmovies.data.vo.Movie
import com.example.latestmovies.ui.single_movie_details.SingleMovie

class PopularMoviePagedListAdapter(public val context: Context) : PagedListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    val MOVIE_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState : NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view : View

        if(viewType == MOVIE_VIEW_TYPE){
            view = layoutInflater.inflate(R.layout.movie_list_item, parent, false)
            return MovieItemViewHolder(view)
        }else{
            view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            return NetworkStateItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == MOVIE_VIEW_TYPE){
            (holder as MovieItemViewHolder).bind(getItem(position), context)
        }else{
            (holder as NetworkStateItemViewHolder).bind(networkState)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if(hasExtraRow() && position == itemCount - 1){
            NETWORK_VIEW_TYPE
        }else{
            MOVIE_VIEW_TYPE
        }
    }

    private fun hasExtraRow() : Boolean{
        return networkState != null && networkState != NetworkState.LOADED
    }

    class MovieDiffCallback: DiffUtil.ItemCallback<Movie>(){
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }

    }

    class MovieItemViewHolder(view : View) : RecyclerView.ViewHolder(view){

        lateinit var textTitle : TextView
        lateinit var textReleaseDate : TextView
        lateinit var imgMoviePoster : ImageView

        fun bind(movie: Movie?, context : Context){
            textTitle = itemView.findViewById<TextView>(R.id.card_text_movie_title)
            textReleaseDate = itemView.findViewById<TextView>(R.id.card_text_release_date)
            imgMoviePoster = itemView.findViewById<ImageView>(R.id.card_img_movie_poster)

            textTitle.text = movie?.title
            textReleaseDate.text = movie?.releaseDate

            val moviePosterURL = POSTER_URL + movie?.posterPath
            Glide.with(itemView.context)
                .load(moviePosterURL)
                .into(imgMoviePoster)

            itemView.setOnClickListener{
                val intent = Intent(context, SingleMovie:: class.java)
                intent.putExtra("id", movie?.id)
                context.startActivity(intent)
            }
        }
    }

    class NetworkStateItemViewHolder(view : View) : RecyclerView.ViewHolder(view){
        lateinit var progressBarItem : ProgressBar
        lateinit var textErrorMessage : TextView

        fun bind(networkState: NetworkState?){
            progressBarItem = itemView.findViewById<ProgressBar>(R.id.progress_bar_item)
            textErrorMessage = itemView.findViewById<TextView>(R.id.text_error_msg_item)

            if(networkState != null && networkState == NetworkState.LOADING){
                progressBarItem.visibility = View.VISIBLE
            }else{
                progressBarItem.visibility = View.GONE
            }

            if(networkState != null && networkState == NetworkState.ERROR){
                textErrorMessage.visibility = View.VISIBLE
                textErrorMessage.text = networkState.msg
            }
            else if(networkState != null && networkState == NetworkState.END_OF_LIST){
                textErrorMessage.visibility = View.VISIBLE
                textErrorMessage.text = networkState.msg
            }
            else{
                textErrorMessage.visibility = View.GONE
            }
        }
    }

    fun setNetworkState(newNetworkState: NetworkState){

        val previousState : NetworkState? = this.networkState
        val hadExtraRow: Boolean = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow: Boolean = hasExtraRow()

        if(hadExtraRow != hasExtraRow){
            if(hadExtraRow){
                notifyItemRemoved(super.getItemCount())
            }else{
                notifyItemInserted(super.getItemCount())
            }
        }else if(hasExtraRow && previousState != newNetworkState){
            notifyItemChanged(itemCount - 1)
        }
    }
}