package com.example.towatch.addmovie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.towatch.Movie
import com.example.towatch.MovieDiffUtil
import com.example.towatch.ProgressDrawable
import com.example.towatch.R

class AddMovieRVAdapter(
    private val onMovieClicked: (MovieViewHolder, Int) -> Unit
) : RecyclerView.Adapter<AddMovieRVAdapter.MovieViewHolder>() {

    var oldMovieList: ArrayList<Movie> = arrayListOf()
    // lastIndex for tracking so the enter animation doesn't play more than once
    var lastIndex = -1
    var selectedMovie: Movie = Movie()
    var lastSelected = -1

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val movieCV: CardView = itemView.findViewById(R.id.cv_movie)
        val movieTitleTV: TextView = itemView.findViewById(R.id.tv_title)
        val movieYearTV: TextView = itemView.findViewById(R.id.tv_year)
        val posterIV: ImageView = itemView.findViewById(R.id.iv_poster)
        val movieDescriptionTV: TextView = itemView.findViewById(R.id.tv_description)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_rv_list_item, parent, false)
        return MovieViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {

        val current = oldMovieList[position]
        holder.movieTitleTV.text = current.title
        holder.movieYearTV.text = current.year
        Glide.with(holder.itemView)
            .asBitmap()
            .fitCenter()
            .load(current.poster)
            .placeholder(ProgressDrawable(holder.itemView.context))
            .error(R.drawable.ic_image_not_supported)
            .into(holder.posterIV)
        holder.movieDescriptionTV.text = current.plot
        if(holder.adapterPosition > lastIndex){
            setAnimation(holder.itemView, position)
            lastIndex = holder.adapterPosition
        }
        holder.itemView.setOnClickListener {
            onMovieClicked(holder, position)
        }
        if(oldMovieList[position] == selectedMovie){
            holder.movieCV.background.setTint(ContextCompat.getColor(holder.movieCV.context,
                R.color.forth
            ))
        } else {
            holder.movieCV.background.setTint(ContextCompat.getColor(holder.movieCV.context,
                R.color.fifth
            ))
        }
    }

    private fun setAnimation(itemView: View, position: Int) {
        if(position > lastIndex){
            val animation = AnimationUtils.loadAnimation(itemView.context,
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
        val diffUtil = MovieDiffUtil(oldMovieList, newMovieList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        oldMovieList = newMovieList
        diffResults.dispatchUpdatesTo(this)
    }
}