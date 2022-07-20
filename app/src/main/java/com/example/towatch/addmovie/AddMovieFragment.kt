package com.example.towatch.addmovie

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.towatch.*
import com.example.towatch.BuildConfig
import com.example.towatch.R
import com.example.towatch.databinding.FragmentAddMovieBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AddMovieFragment : Fragment(){
    private lateinit var viewModel: AddMovieViewModel

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var moviesRef: DatabaseReference

    private lateinit var adapter: AddMovieRVAdapter

    private lateinit var moviesArrayList: ArrayList<Movie>

    private var _binding: FragmentAddMovieBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMovieBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[AddMovieViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        database = Firebase.database(BuildConfig.DB_URL)
        moviesRef = database.getReference("users").child(mAuth.currentUser?.uid.toString()).child("movies")

        moviesArrayList = ArrayList()

        adapter = AddMovieRVAdapter{ holder, position ->
            binding.cbSeen.isChecked = false
            adapter.selectedMovie.watched = false
            if(adapter.selectedMovie == moviesArrayList[position]){
                viewModel.isMovieSelected.value = false
                holder.movieCV.background.setTint(ContextCompat.getColor(holder.movieCV.context, R.color.fifth))
                adapter.selectedMovie = Movie()
                adapter.lastSelected = -1
            } else {
                viewModel.isMovieSelected.value = true
                holder.movieCV.background.setTint(ContextCompat.getColor(holder.movieCV.context, R.color.forth))
                if(adapter.lastSelected != -1){
                    val old = binding.rvMovies.findViewHolderForAdapterPosition(adapter.lastSelected) as AddMovieRVAdapter.MovieViewHolder?
                    old?.movieCV?.background?.setTint(ContextCompat.getColor(holder.movieCV.context, R.color.fifth))
                }
                adapter.selectedMovie = moviesArrayList[position]
                adapter.lastSelected = position
            }
        }

        viewModel.isMovieSelected.observe(this.viewLifecycleOwner) { isMovieSelected ->
            binding.cbSeen.isEnabled = isMovieSelected
            binding.btnAddMovie.isEnabled = isMovieSelected
        }

        binding.rvMovies.adapter = adapter
        binding.rvMovies.hasFixedSize()
        binding.rvMovies.layoutManager = LinearLayoutManager(requireContext())

        binding.etTitle.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                binding.etTitle.setText(binding.etTitle.text.toString().trim())
                binding.etTitle.clearFocus()
                hideKeyboard()
                binding.llProgress.visibility = View.VISIBLE
                fetchMovies()
                adapter.setData(moviesArrayList)
                return@OnKeyListener false
            }
            false
        })

        binding.btnSearch.setOnClickListener {
            hideKeyboard()
            binding.etTitle.clearFocus()
            binding.llProgress.visibility = View.VISIBLE
            fetchMovies()
            adapter.setData(moviesArrayList)
        }

        binding.cbSeen.setOnCheckedChangeListener { _, isChecked ->
            adapter.selectedMovie.watched = isChecked
        }

        binding.btnAddMovie.setOnClickListener {
            binding.llProgress.visibility = View.VISIBLE
            addMovie(adapter.selectedMovie)
        }
    }

    private fun fetchMovies(){
        adapter.lastIndex = -1
        adapter.lastSelected = -1
        adapter.selectedMovie = Movie()
        moviesArrayList.clear()
        val title = binding.etTitle.text.toString().trim()

        lifecycleScope.launch{
            val response = try {
                RetrofitInstance.api.getMoviesByTitle(title)
            } catch (e: IOException){
                Log.e("AddMovieFragment", "IOException: ${e.localizedMessage}")
                binding.llProgress.visibility = View.GONE
                return@launch
            } catch (e: HttpException){
                Log.e("AddMovieFragment", "HttpException: ${e.localizedMessage}")
                binding.llProgress.visibility = View.GONE
                return@launch
            }
            if(response.isSuccessful){
                response.body()?.movieList?.let {
                    moviesArrayList = ArrayList(it)
                    adapter.setData(moviesArrayList)
                    adapter.notifyDataSetChanged()
                }
            } else {
                Log.e("AddMovieFragment", "Response not successful")
            }
            binding.llProgress.visibility = View.GONE
        }
    }

    private fun addMovie(movie: Movie){
        getDescription(movie)
    }
    private fun getDescription(movie: Movie){
        lifecycleScope.launch{
            val response = try {
                RetrofitInstance.api.getMovieWithDetailsById(movie.id)
            } catch (e: IOException){
                Log.e("AddMovieFragment", "IOException: ${e.localizedMessage}")
                binding.llProgress.visibility = View.GONE
                return@launch
            } catch (e: HttpException){
                Log.e("AddMovieFragment", "HttpException: ${e.localizedMessage}")
                binding.llProgress.visibility = View.GONE
                return@launch
            }
            if (response.isSuccessful){
                response.body()?.let {
                    Log.e("AddMovieFragment", it.toString())
                    movie.plot = it.plot
                    moviesRef.child(movie.id).setValue(movie)
                    view?.findNavController()?.popBackStack()
                }
            } else {
                Log.e("AddMovieFragment", "Response not successful")
            }
            binding.llProgress.visibility = View.GONE
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