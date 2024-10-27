package com.example.optionsmenupractice.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.optionsmenupractice.R

class EventDetails : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_details, container, false)
    }

    override fun onStart() {
        super.onStart() // Call the superclass method first

        // Retrieve values from the arguments bundle
        val eventId = arguments?.getInt("event_id") ?: -1 // Use -1 as a default value
        val eventName = arguments?.getString("event_name") ?: "N/A"
        val eventType = arguments?.getString("event_type") ?: "N/A"
        val start = arguments?.getString("event_start") ?: "N/A"
        val end = arguments?.getString("event_finish") ?: "N/A"
        val info = arguments?.getString("event_info") ?: "N/A"
        val userId = arguments?.getInt("user_id") ?: -1
        val eventDate = arguments?.getString("event_date") ?: "N/A"

        // Initialize views
        val eventIdView: TextView = requireView().findViewById(R.id.eventid)
        val eventNameView: TextView = requireView().findViewById(R.id.event_name)
        val eventTypeView: TextView = requireView().findViewById(R.id.eventtype)
        val eventStartView: TextView = requireView().findViewById(R.id.start_time)
        val eventFinishView: TextView = requireView().findViewById(R.id.finish_time)
        val eventInfoView: TextView = requireView().findViewById(R.id.event_info)
        val getTicketsButton: Button = requireView().findViewById(R.id.gettickets)
        val dateView: TextView = requireView().findViewById(R.id.date)

        // Set the text of the TextViews
        eventIdView.text = eventId.toString()
        eventNameView.text = eventName
        eventTypeView.text = eventType
        eventStartView.text = start
        eventFinishView.text = end
        eventInfoView.text = info
        dateView.text = eventDate

        // Create a bundle for the GetTickets fragment
        val ticketBundle = Bundle().apply {
            putInt("event_id", eventId)
            putInt("user_id", userId)
            putString("event_name", eventName)
            putString("event_type", eventType)
        }

        // Set the click listener for the Get Tickets button
        getTicketsButton.setOnClickListener {
            openFragment(GetTickets(), ticketBundle)
        }
    }

    private fun openFragment(fragment: Fragment, bundle: Bundle) {
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
