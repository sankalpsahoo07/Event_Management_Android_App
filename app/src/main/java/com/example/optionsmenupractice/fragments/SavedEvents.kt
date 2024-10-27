package com.example.optionsmenupractice.fragments

import adapters.SavedEventsAdapter
import models.GeneralEventModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.optionsmenupractice.R
import org.json.JSONArray
import saved_instance_data.SavedEventViewModel
import saved_instance_data.SharedViewModel

class SavedEvents : Fragment() {

    private lateinit var generalEventsList: ArrayList<GeneralEventModel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SavedEventsAdapter
    private var userId: Int = 0
    private val savedEventViewModel: SavedEventViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_saved_events, container, false)
        setupRecyclerView(view)

        // Get the user ID from sharedViewModel
        sharedViewModel.userId.observe(viewLifecycleOwner) { userId ->
            this.userId = userId
            storeDataInArray()
        }

        // Observe changes in the event list
        savedEventViewModel.generalEventsList.observe(viewLifecycleOwner) { events ->
            adapter.updateData(events)
        }

        return view
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        generalEventsList = ArrayList()
        adapter = SavedEventsAdapter(generalEventsList, { clickedItem ->
            handleItemClick(clickedItem)
        }, { onRemoveClick ->
            removeEvent(onRemoveClick.getEventId()!!)
        })
        recyclerView.adapter = adapter
    }

    private fun handleItemClick(clickedItem: GeneralEventModel) {
        val bundle = Bundle().apply {
            putInt("event_id", clickedItem.getEventId()?.toInt() ?: -1)
            putInt("user_id", userId)
            putString("event_name", clickedItem.getEventName())
            putString("event_type", clickedItem.getEventType())
            putString("event_start", clickedItem.getEventStartTime())
            putString("event_finish", clickedItem.getEventFinishTime())
            putString("event_info", clickedItem.getEventDescription())
        }

        val fragmentB = EventDetails().apply {
            arguments = bundle
        }

        displayEventDetailsFragment(fragmentB)

        Toast.makeText(
            context,
            "Clicked: ${clickedItem.getEventName()}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun storeDataInArray() {
        val url = "http://10.0.2.2:8888/SavedEvents.php?userid=$userId"
        val requestQueue = Volley.newRequestQueue(requireContext())

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                parseEventsResponse(response)
            },
            { error ->
                handleErrorResponse(error)
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    private fun parseEventsResponse(response: JSONArray) {
        generalEventsList.clear()

        try {
            for (i in 0 until response.length()) {
                val jsonObject = response.getJSONObject(i)
                val event = GeneralEventModel().apply {
                    setEventId(jsonObject.getString("event_id").toInt())
                    setEventName(jsonObject.getString("event_name"))
                    setEventType(jsonObject.getString("event_type"))
                    setEventStartTime(jsonObject.getString("event_start"))
                    setEventFinishTime(jsonObject.getString("event_finish"))
                    setEventDescription(jsonObject.getString("event_info"))
                }
                generalEventsList.add(event)
            }

            savedEventViewModel.generalEventsList.value = generalEventsList
            adapter.notifyDataSetChanged() // Notify adapter of data change
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error parsing JSON: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleErrorResponse(error: VolleyError) {
        generalEventsList.clear()
        adapter.notifyDataSetChanged() // Notify adapter of data change

        // Check if there is a network response to provide more context
        val errorMessage = if (error.networkResponse != null) {
            String(error.networkResponse.data)
        } else {
            "No saved events found"
        }

        Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_SHORT).show()
    }


    private fun removeEvent(eventId: Int) {
        val url = "http://10.0.2.2:8888/UnsaveItem.php"
        val requestQueue = Volley.newRequestQueue(requireContext())
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                Toast.makeText(context, response, Toast.LENGTH_LONG).show()
                storeDataInArray() // Refresh the events list
            },
            Response.ErrorListener { error ->
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("userid" to userId.toString(), "eventid" to eventId.toString())
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun displayEventDetailsFragment(fragmentB: EventDetails) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragmentB)
            .addToBackStack(null)
            .commit()
    }
}
