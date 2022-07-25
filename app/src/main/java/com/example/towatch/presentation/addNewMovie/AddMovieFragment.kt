package com.example.towatch.presentation.addNewMovie

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.towatch.R
import com.example.towatch.databinding.FragmentAddMovieBinding
import com.example.towatch.domain.model.Movie
import kotlinx.coroutines.launch

class AddMovieFragment : Fragment() {
    private val viewModel by viewModels<AddMovieViewModel>()

    private lateinit var adapter: AddMovieRVAdapter

    private var _binding: FragmentAddMovieBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        setupRecyclerView()

        // return on keyboard clicked -> hide onscreen keyboard
        // and lose focus from the edittext then fetch movies
        binding.etTitle.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                search()
                return@OnKeyListener false
            }
            false
        })

        // button search clicked -> hide onscreen keyboard
        // and lose focus from the edittext then fetch movies
        binding.btnSearch.setOnClickListener { search() }

        binding.cbSeen.setOnCheckedChangeListener { _, isChecked ->
            viewModel.movieWatched.value = isChecked
        }

        binding.btnAddMovie.setOnClickListener {
            addMovie(viewModel.selectedMovie.value)
        }
    }

    private fun setupRecyclerView() {
        // adapter with an item onclick function
        adapter = AddMovieRVAdapter(viewModel) { holder, position ->
            binding.cbSeen.isChecked = false

            // if clicked the selected movie
            if (viewModel.selectedMovie.value == viewModel.moviesList.value?.get(position)) {
                deselectMovie(holder)
                // if clicked a different than the selected movie
            } else {
                selectMovie(holder, position)
            }
        }

        binding.rvMovies.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun hideLoading() {
        binding.llProgress.isVisible = false
    }

    private fun showLoading() {
        binding.llProgress.isVisible = true
    }

    private fun showError() {
        binding.llError.isVisible = true
    }

    private fun hideError(){
        binding.llError.isVisible = false
    }

    private fun popFragmentBackStack() {
        view?.findNavController()?.popBackStack()
    }

    private fun deselectMovie(holder: AddMovieRVAdapter.MovieViewHolder) {
        viewModel.selectedMovie.value = Movie()
        viewModel.isMovieSelected.value = false
        holder.movieCV.background.setTint(
            ContextCompat.getColor(
                holder.movieCV.context,
                R.color.fifth
            )
        )
        viewModel.lastSelectedMoviePosition.value = -1
    }

    private fun selectMovie(holder: AddMovieRVAdapter.MovieViewHolder, position: Int){
        viewModel.selectedMovie.value = viewModel.moviesList.value?.get(position)
        viewModel.isMovieSelected.value = true
        holder.movieCV.background.setTint(
            ContextCompat.getColor(
                holder.movieCV.context,
                R.color.forth
            )
        )
        // change background color of the previously selected movie
        if (viewModel.lastSelectedMoviePosition.value != -1) {
            val old = binding.rvMovies
                .findViewHolderForAdapterPosition(
                    viewModel.lastSelectedMoviePosition.value ?: 0
                ) as AddMovieRVAdapter.MovieViewHolder?
            old?.movieCV?.background?.setTint(
                ContextCompat.getColor(
                    holder.movieCV.context,
                    R.color.fifth
                )
            )
        }
        viewModel.lastSelectedMoviePosition.value = position
    }

    private fun search(){
        binding.etTitle.setText(binding.etTitle.text.toString().trim())
        binding.etTitle.clearFocus()
        hideKeyboard()
        fetchMovies()
    }

    private fun fetchMovies() {
        val title = binding.etTitle.text.toString()
        lifecycleScope.launch {
            viewModel.fetchMoviesByTitle(searchTitle = title)
        }
    }

    private fun addMovie(movie: Movie?) {
        lifecycleScope.launch {
            movie?.let { viewModel.addMovieToDatabase(it) }
        }
    }

    private fun observeViewModel(){
        viewModel.isMovieSelected.observe(this.viewLifecycleOwner) { isMovieSelected ->
            binding.cbSeen.isEnabled = isMovieSelected
            binding.btnAddMovie.isEnabled = isMovieSelected
        }

        viewModel.movieWatched.observe(viewLifecycleOwner) {
            binding.cbSeen.isChecked = it
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                AddMovieViewState.Error -> {
                    hideLoading()
                    showError()
                }
                AddMovieViewState.Loaded -> {
                    hideLoading()
                    if(viewModel.moviesList.value?.isEmpty() == true){
                        showError()
                    }
                }
                AddMovieViewState.Loading -> {
                    hideError()
                    showLoading()
                }
                AddMovieViewState.Started -> hideLoading()
                AddMovieViewState.Finished -> popFragmentBackStack()
            }
        }

        viewModel.moviesList.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }
    }
}

// extension functions to hide the onscreen keyboard

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}