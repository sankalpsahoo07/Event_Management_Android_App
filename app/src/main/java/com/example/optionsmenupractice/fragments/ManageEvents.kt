package com.example.optionsmenupractice.fragments

import adapters.ManageEventsAdapter
import models.MyEventsModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.optionsmenupractice.R
import com.example.optionsmenupractice.databinding.FragmentManageEventsBinding
import saved_instance_data.ManageEventViewModel
import saved_instance_data.SharedViewModel

class ManageEvents : Fragment() {

    private var _binding: FragmentManageEventsBinding? = null
    private val binding get() = _binding!!

    private var myEventsList: ArrayList<MyEventsModel> = ArrayList()
    private lateinit var adapter: ManageEventsAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val manageEventViewModel: ManageEventViewModel by activityViewModels()
    private var userId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = arguments?.getInt("user_id") ?: 0

        setupRecyclerView()
        observeViewModel()
        fetchEventsData()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ManageEventsAdapter(myEventsList) { clickedItem -> handleEventClick(clickedItem) }
        binding.recyclerView.adapter = adapter
    }

    private fun handleEventClick(clickedItem: MyEventsModel) {
        val bundle = Bundle().apply {
            putInt("event_id", clickedItem.getEventId())
            putInt("user_id", userId)
            putString("event_name", clickedItem.getEventName())
            putString("event_type", clickedItem.getEventType())
            putString("event_start", clickedItem.getEventStartTime())
            putString("event_finish", clickedItem.getEventFinishTime())
            putString("event_info", clickedItem.getEventDescription())
        }

        val fragmentB = EventParticipants()
        fragmentB.arguments = bundle
        displayEventDetailsFragment(fragmentB)

        Toast.makeText(requireContext(), "Clicked: ${clickedItem.getEventName()}", Toast.LENGTH_SHORT).show()
    }

    private fun fetchEventsData() {
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
                        // Ensure you are passing all required parameters
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
                    manageEventViewModel.myEventsList.value = myEventsList
                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    handleError("Error parsing JSON: ${e.message}")
                }
            },
            { error -> handleError("Error: ${error.message}") }
        )

        requestQueue.add(jsonArrayRequest)
    }


    private fun handleError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun observeViewModel() {
        sharedViewModel.userId.observe(viewLifecycleOwner, Observer { userId ->
            this.userId = userId
            fetchEventsData() // Reload data if userId changes
        })

        manageEventViewModel.myEventsList.observe(viewLifecycleOwner, Observer { events ->
            adapter.updateData(events)
        })
    }

    private fun displayEventDetailsFragment(fragmentB: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragmentB)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding reference to avoid memory leaks
    }
}
