package com.example.optionsmenupractice.fragments

import adapters.ParticipantsRecyclerAdapter
import models.ParticipantModel
import requests.RequestAllEvents
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.optionsmenupractice.databinding.FragmentEventParticipantsBinding
import saved_instance_data.ParticipantsViewModel
import saved_instance_data.SharedViewModel

class EventParticipants : Fragment() {

    private var _binding: FragmentEventParticipantsBinding? = null
    private val binding get() = _binding!!

    private lateinit var participantList: ArrayList<ParticipantModel>
    private lateinit var adapter: ParticipantsRecyclerAdapter
    private val requestAllEvents: RequestAllEvents = RequestAllEvents()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val participantsViewModel: ParticipantsViewModel by activityViewModels()
    private var userId: Int = 0
    private var eventid: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventParticipantsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventid = arguments?.getInt("event_id") ?: 0

        setupRecyclerView()
        loadParticipantsData()

        // Observe the userId
        sharedViewModel.userId.observe(viewLifecycleOwner, Observer { userId ->
            this.userId = userId
            loadParticipantsData() // Reload data if userId changes
        })

        // Observe changes in the participants list
        participantsViewModel.participantsList.observe(viewLifecycleOwner, Observer { participants ->
            adapter.updateData(participants)
        })
    }

    private fun setupRecyclerView() {
        participantList = ArrayList()
        adapter = ParticipantsRecyclerAdapter(participantList)
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.adapter = adapter
    }

    private fun loadParticipantsData() {
        val url = "http://10.0.2.2:8888/MyParticipants.php?eventid=$eventid"
        val requestQueue = Volley.newRequestQueue(requireContext())

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    participantList.clear()
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val event = ParticipantModel(
                            name = jsonObject.getString("participant_name"),
                            email = jsonObject.getString("participant_email"),
                            quantity = jsonObject.getInt("ticket_quantity"), // Make sure this is an Int
                            phoneNo = jsonObject.getInt("participant_phone").toLong()
                        )
                        participantList.add(event)
                    }
                    participantsViewModel.participantsList.value = participantList
                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error parsing JSON: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding reference to avoid memory leaks
    }
}
