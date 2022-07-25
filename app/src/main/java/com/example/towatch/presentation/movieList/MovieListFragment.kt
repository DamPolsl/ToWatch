package com.example.towatch.presentation.movieList

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.towatch.R
import com.example.towatch.databinding.FragmentMovieListBinding
import com.example.towatch.presentation.ProgressDrawable
import com.google.android.material.bottomsheet.BottomSheetDialog

class MovieListFragment : Fragment(){

    private val viewModel by viewModels<MovieListViewModel>()
    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MovieListRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAdd.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_addMovieFragment)
        }

        setupRecyclerView()
        observeViewModel()
        viewModel.getMoviesFromDatabase()
    }

    @SuppressLint("InflateParams")
    private fun displayBottomSheet(){
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val layout: View = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        bottomSheetDialog.setContentView(layout)
        bottomSheetDialog.show()

        val selectedMovie = viewModel.selectedMovie.value

        selectedMovie?.let { movie ->
            with(layout){
                Glide.with(this)
                    .asBitmap()
                    .fitCenter()
                    .load(movie.poster)
                    .placeholder(ProgressDrawable(requireContext()))
                    .error(R.drawable.ic_image_not_supported)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(findViewById(R.id.iv_poster))

                findViewById<TextView>(R.id.tv_title).text = movie.title
                findViewById<TextView>(R.id.tv_year).text = movie.year
                findViewById<TextView>(R.id.tv_description).text = movie.plot

                findViewById<CheckBox>(R.id.cb_seen).apply{
                    isChecked = movie.watched
                    setOnCheckedChangeListener { _, isChecked ->
                        viewModel.changeMovieWatchedStatus(movie, isChecked)
                    }
                }


                findViewById<Button>(R.id.btn_delete).setOnClickListener {
                    if(adapter.lastIndex == adapter.itemCount){
                        adapter.lastIndex -= 1
                    }
                    viewModel.removeMovieFromDatabase(movie)
                    bottomSheetDialog.dismiss()
                }
            }
        }
    }

    private fun setupRecyclerView(){
        adapter = MovieListRVAdapter(viewModel){ _, movie, _ ->
            viewModel.changeSelectedMovie(movie)
            displayBottomSheet()
        }

        binding.rvMovies.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.moviesList.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}