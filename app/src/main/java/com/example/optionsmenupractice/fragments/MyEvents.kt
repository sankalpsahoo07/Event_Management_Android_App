package com.example.optionsmenupractice.fragments

import adapters.MyEventsAdapter
import models.MyEventsModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.optionsmenupractice.R
import saved_instance_data.MyEventViewModel

class MyEvents : Fragment() {

    private var userId: Int = 0
    private lateinit var myEventsList: ArrayList<MyEventsModel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyEventsAdapter
    private val myEventViewModel: MyEventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_events, container, false)

        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        myEventsList = ArrayList()

        // Initialize the adapter
        initializeAdapter()

        arguments?.let {
            userId = it.getInt("user_id", -1) // Default to -1 if not found
            if (userId != -1) {
                storeDataInArray()
            }
        }

        // Observe changes in the event list
        myEventViewModel.myEventsList.observe(viewLifecycleOwner, Observer { events ->
            adapter.updateData(events)
        })

        return view
    }

    private fun initializeAdapter() {
        adapter = MyEventsAdapter(myEventsList,
            // onItemClick function
            { clickedItem -> handleItemClick(clickedItem) },
            // onEditClick function
            { editClicked -> handleEditClick(editClicked) },
            { viewClicked -> handleViewClick(viewClicked) }
        )
        recyclerView.adapter = adapter
    }

    private fun handleItemClick(clickedItem: MyEventsModel) {
        val bundle = createBundleFromEvent(clickedItem)
        displayEventDetailsFragment(EventDetails(), bundle)
        Toast.makeText(context, "Clicked: ${clickedItem.getEventName()}", Toast.LENGTH_SHORT).show()
    }

    private fun handleEditClick(editClicked: MyEventsModel) {
        val bundle = createBundleFromEvent(editClicked)
        displayEventDetailsFragment(EditMyEventFragment(), bundle)
        Toast.makeText(context, "Editing: ${editClicked.getEventName()}", Toast.LENGTH_SHORT).show()
    }

    private fun handleViewClick(viewClicked: MyEventsModel) {
        val bundle = createBundleFromEvent(viewClicked)
        displayEventDetailsFragment(EventDetails(), bundle)
    }

    private fun createBundleFromEvent(event: MyEventsModel): Bundle {
        return Bundle().apply {
            putInt("event_id", event.getEventId())
            putString("event_name", event.getEventName())
            putString("event_type", event.getEventType())
            putString("event_start", event.getEventStartTime())
            putString("event_finish", event.getEventFinishTime())
            putString("event_info", event.getEventDescription())
        }
    }

    private fun storeDataInArray() {
        val url = "http://10.0.2.2:8888/MyEvents.php?user_id=$userId"
        val requestQueue = Volley.newRequestQueue(requireContext())

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    myEventsList.clear()
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        // Instantiate MyEventsModel directly using its constructor
                        val event = MyEventsModel(
                            _event_id = jsonObject.getInt("event_id"),
                            _event_name = jsonObject.getString("event_name"),
                            _event_type = jsonObject.getString("event_type"),
                            _event_start = jsonObject.getString("event_start"),
                            _event_finish = jsonObject.getString("event_finish"),
                            _event_info = jsonObject.getString("event_info")
                        )
                        myEventsList.add(event)
                    }
                    adapter.notifyDataSetChanged() // Notify adapter of data changes
                } catch (e: Exception) {
                    Log.e("MyEvents", "Error parsing JSON: ${e.message}")
                    Toast.makeText(requireContext(), "Error parsing JSON: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("MyEvents", "Error: ${error.message}")
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }


    private fun displayEventDetailsFragment(fragment: Fragment, bundle: Bundle) {
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
