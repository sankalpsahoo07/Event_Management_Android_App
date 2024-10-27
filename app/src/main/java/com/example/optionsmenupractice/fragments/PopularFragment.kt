package com.example.optionsmenupractice.fragments

import Database.DBHelper
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import adapters.PopularRecyclerAdapter
import models.PopularEventModel
import com.example.optionsmenupractice.R

class PopularFragment : Fragment() {

    private lateinit var popularEventsList: MutableList<PopularEventModel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: DBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_popular, container, false)

        // Initialize DBHelper and the events list
        dbHelper = DBHelper(requireContext())
        popularEventsList = mutableListOf()

        recyclerView = view.findViewById(R.id.recyclerview)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        // Load data into the list
        loadPopularEvents()

        return view
    }

    private fun loadPopularEvents() {
        // Clear the list to avoid duplicating data
        popularEventsList.clear()

        // Read all data from the database
        val events = dbHelper.readAllEvents()

        if (events.isNotEmpty()) {
            popularEventsList.addAll(events)
        } else {
            Toast.makeText(context, "No data", Toast.LENGTH_SHORT).show()
        }

        // Set the adapter to the RecyclerView after loading data
        recyclerView.adapter = PopularRecyclerAdapter(popularEventsList)
    }
}
