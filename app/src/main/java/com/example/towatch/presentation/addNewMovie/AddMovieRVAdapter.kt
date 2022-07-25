package com.example.towatch.presentation.addNewMovie

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
import com.example.towatch.domain.model.Movie
import com.example.towatch.presentation.movieList.MovieListDiffUtil
import com.example.towatch.presentation.ProgressDrawable
import com.example.towatch.R

class AddMovieRVAdapter(
    private val viewModel: AddMovieViewModel,
    private val onMovieClicked: (MovieViewHolder, Int) -> Unit
) : RecyclerView.Adapter<AddMovieRVAdapter.MovieViewHolder>() {

    private var oldMovieList: ArrayList<Movie> = arrayListOf()

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

        if(holder.adapterPosition > (viewModel.lastShownMoviePosition.value ?: -1)){
            setAnimation(holder.itemView)
            viewModel.lastShownMoviePosition.value = holder.adapterPosition
        }

        holder.itemView.setOnClickListener {
            onMovieClicked(holder, position)
        }

        if(oldMovieList[position] == viewModel.selectedMovie.value){
            holder.movieCV.background.setTint(ContextCompat.getColor(holder.movieCV.context,
                R.color.forth
            ))
        } else {
            holder.movieCV.background.setTint(ContextCompat.getColor(holder.movieCV.context,
                R.color.fifth
            ))
        }
    }

    private fun setAnimation(itemView: View) {
        val animation = AnimationUtils.loadAnimation(itemView.context,
            R.anim.slide_in_left_fast
        )
        itemView.animation = animation
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