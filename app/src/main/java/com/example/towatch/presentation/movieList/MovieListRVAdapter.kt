package com.example.towatch.presentation.movieList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.towatch.domain.model.Movie
import com.example.towatch.presentation.ProgressDrawable
import com.example.towatch.R

class MovieListRVAdapter(
    private val viewModel: MovieListViewModel,
    private val onMovieClicked: (MovieViewHolder, Movie, Int) -> Unit
) : RecyclerView.Adapter<MovieListRVAdapter.MovieViewHolder>() {

    private var oldMovieList: ArrayList<Movie> = arrayListOf()
    // lastIndex for tracking so the enter animation doesn't play more than once
    var lastIndex = -1

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val checkIV: ImageView = itemView.findViewById(R.id.iv_check)
        val movieTitleTV: TextView = itemView.findViewById(R.id.tv_title)
        val movieYearTV: TextView = itemView.findViewById(R.id.tv_year)
        val posterIV: ImageView = itemView.findViewById(R.id.iv_poster)
        val movieDescriptionTV: TextView = itemView.findViewById(R.id.tv_description)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.movie_rv_list_item, parent, false)
        return MovieViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        with(oldMovieList[position]){
            holder.movieTitleTV.text = this.title
            holder.movieYearTV.text = this.year

            holder.checkIV.isVisible = this.watched

            Glide.with(holder.itemView)
                .asBitmap()
                .fitCenter()
                .load(this.poster)
                .placeholder(ProgressDrawable(holder.itemView.context))
                .error(R.drawable.ic_image_not_supported)
                .into(holder.posterIV)
            holder.movieDescriptionTV.text = this.plot

            if(holder.adapterPosition > lastIndex){
                setAnimation(holder.itemView, position)
                lastIndex = holder.adapterPosition
            }

            holder.itemView.setOnClickListener {
                onMovieClicked(holder, this, position)
            }
        }
    }

    private fun setAnimation(itemView: View, position: Int) {
        if(position > lastIndex){
            val animation = AnimationUtils.loadAnimation(
                itemView.context,
                R.anim.slide_in_left_fast
            )
            itemView.animation = animation
            lastIndex = position
        }
    }

    override fun getItemCount(): Int {
        return oldMovieList.count()
    }

    fun setData(newMovieList: ArrayList<Movie>){
        val diffUtil = MovieListDiffUtil(oldMovieList, newMovieList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        oldMovieList = newMovieList
        diffResults.dispatchUpdatesTo(this)
    }
}