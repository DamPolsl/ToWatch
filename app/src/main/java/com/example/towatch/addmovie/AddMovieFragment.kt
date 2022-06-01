package com.example.towatch.addmovie

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.towatch.*
import com.example.towatch.BuildConfig
import com.example.towatch.R
import com.example.towatch.databinding.FragmentAddMovieBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject

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

        binding.etTitle.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                binding.etTitle.setText(binding.etTitle.text.toString().trim())
                binding.etTitle.clearFocus()
                v.hideKeyboard()
                binding.llProgress.visibility = View.VISIBLE
                fetchMovies()
                adapter.setData(moviesArrayList)
                return@OnKeyListener false
            }
            false
        })

        binding.btnSearch.setOnClickListener {
            it.hideKeyboard()
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
        moviesArrayList.clear()
        val searchTitle: String = binding.etTitle.text.toString()
        val url = "https://www.omdbapi.com/?apikey=${BuildConfig.API_KEY}&type=movie&s=${searchTitle}"
        val queue = Volley.newRequestQueue(requireContext())

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val moviesArray = jsonResponse.getJSONArray("Search")
                    (0..moviesArray.length()).forEach { i ->
                        val jsonMovie: JSONObject = moviesArray.getJSONObject(i)
                        val movie = Movie(
                            jsonMovie.getString("Poster"),
                            jsonMovie.getString("Year"),
                            jsonMovie.getString("Title"),
                            plot = "",
                            watched=false,
                            jsonMovie.getString("imdbID")
                        )
                        moviesArrayList.add(movie)
                        adapter.notifyDataSetChanged()
                    }
                } catch (e: JSONException){
                    Log.v(DashboardActivity.TAG, e.toString())
                    e.printStackTrace()
                }
                binding.llProgress.visibility = View.GONE
            },
            {
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    private fun addMovie(movie: Movie){
        getDescription(movie)
    }
    private fun getDescription(movie: Movie){
        val url = "https://www.omdbapi.com/?apikey=${BuildConfig.API_KEY}&type=movie&i=${movie.id}"
        val queue = Volley.newRequestQueue(requireContext())

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    movie.plot = jsonResponse.getString("Plot")
                    moviesRef.child(movie.id).setValue(movie)
                    binding.llProgress.visibility = View.GONE
                    view?.findNavController()?.popBackStack()
                } catch (e: JSONException){
                    Log.v(DashboardActivity.TAG, e.toString())
                    binding.llProgress.visibility = View.GONE
                    e.printStackTrace()
                }
            },
            {
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
                binding.llProgress.visibility = View.GONE
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }
}

fun View.hideKeyboard() = ViewCompat.getWindowInsetsController(this)?.hide(WindowInsetsCompat.Type.ime())