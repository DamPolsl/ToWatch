package com.example.towatch.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.towatch.*
import com.example.towatch.BuildConfig
import com.example.towatch.R
import com.example.towatch.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(){

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var moviesRef: DatabaseReference

    private lateinit var adapter: MovieRVAdapter

    private lateinit var moviesArrayList: ArrayList<Movie>
    private lateinit var moviesSeenList: ArrayList<Movie>
    private lateinit var moviesNotSeenList: ArrayList<Movie>

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private var clickedPosition = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAdd.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_addMovieFragment)
        }

        moviesArrayList = ArrayList()

        moviesSeenList = ArrayList()
        moviesNotSeenList = ArrayList()

        mAuth = FirebaseAuth.getInstance()
        database = Firebase.database(BuildConfig.DB_URL)
        moviesRef = database.getReference("users").child(mAuth.currentUser?.uid.toString()).child("movies")
        moviesRef.keepSynced(true)

        adapter = MovieRVAdapter{_, movie, position ->
            clickedPosition = position
            adapter.selectedMovie = movie
            displayBottomSheet(movie)
        }

        binding.rvMovies.adapter = adapter
        binding.rvMovies.setHasFixedSize(true)
        binding.rvMovies.layoutManager = LinearLayoutManager(requireContext())
        moviesArrayList.clear()
        getMovies()
    }

    private fun getMovies() {
        moviesRef.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val value: Movie = snapshot.getValue(Movie::class.java)!!
                if (value !in moviesArrayList){
                    moviesArrayList.add(value)
                } else {
                    moviesArrayList.remove(value)
                    moviesArrayList.add(value)
                }
                moviesArrayList.sortWith(compareBy({it.watched}, {it.id}))
                adapter.setData(moviesArrayList)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                moviesArrayList.sortWith(compareBy({it.watched}, {it.id}))
                adapter.setData(moviesArrayList)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val value: Movie = snapshot.getValue(Movie::class.java)!!
                moviesArrayList.remove(value)
                adapter.setData(moviesArrayList)
                adapter.notifyDataSetChanged()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("DebugTag", "moved")
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun displayBottomSheet(movie: Movie){
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val layout: View = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        bottomSheetDialog.setContentView(layout)
        bottomSheetDialog.show()

        val posterIV: ImageView = layout.findViewById(R.id.iv_poster)
        val titleTV: TextView = layout.findViewById(R.id.tv_title)
        val yearTV: TextView = layout.findViewById(R.id.tv_year)
        val descriptionTV: TextView = layout.findViewById(R.id.tv_description)
        val seenCB: CheckBox = layout.findViewById(R.id.cb_seen)
        val deleteBtn: Button = layout.findViewById(R.id.btn_delete)

        Glide.with(layout)
            .asBitmap()
            .fitCenter()
            .load(movie.poster)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(posterIV)
        titleTV.text = movie.title
        yearTV.text = movie.year
        descriptionTV.text = movie.plot
        seenCB.isChecked = movie.watched

        seenCB.setOnCheckedChangeListener { _, isChecked ->
            movie.watched = isChecked
            moviesRef.child(movie.id).child("watched").setValue(isChecked)
        }

        deleteBtn.setOnClickListener {
//            moviesArrayList.remove(movie)
//            adapter.setData(moviesArrayList)
            moviesRef.child(movie.id).addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.ref.removeValue()
                    bottomSheetDialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}