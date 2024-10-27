package com.example.optionsmenupractice.fragments

import adapters.HomeRecyclerAdapter
import models.GeneralEventModel
import requests.RequestAllEvents
import android.os.Bundle
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
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.optionsmenupractice.R
import org.json.JSONObject
import saved_instance_data.HomeViewModel
import saved_instance_data.SharedViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var generalEventsList: ArrayList<GeneralEventModel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HomeRecyclerAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private var userId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home2, container, false)
        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        generalEventsList = ArrayList()

        initializeAdapter()
        observeUserId()
        observeEventList()

        return view
    }

    private fun initializeAdapter() {
        adapter = HomeRecyclerAdapter(generalEventsList, { clickedItem ->
            navigateToEventDetails(clickedItem)
        }, { onSaveClick ->
            onSaveClick.getEventId()?.let { saveEvent(it) }
        }, { onLikeClicked ->
            onLikeClicked.getEventId()?.let { likeEvent(it) }
        })
        recyclerView.adapter = adapter
    }

    private fun observeUserId() {
        sharedViewModel.userId.observe(viewLifecycleOwner, Observer { userId ->
            this.userId = userId
            storeDataInArray()
        })
    }

    private fun observeEventList() {
        homeViewModel.generalEventsList.observe(viewLifecycleOwner, Observer { events ->
            adapter.updateData(events)
        })
    }

    private fun storeDataInArray() {
        val url = "http://10.0.2.2:8888/all_events.php"
        val requestQueue = Volley.newRequestQueue(requireContext())

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    generalEventsList.clear()
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val event = parseEventFromJson(jsonObject)
                        generalEventsList.add(event)
                    }
                    homeViewModel.generalEventsList.value = generalEventsList
                } catch (e: Exception) {
                    e.printStackTrace()
                    showToast("Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                showToast("Network error: ${error.message}")
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    private fun parseEventFromJson(jsonObject: JSONObject): GeneralEventModel {
        val eventID = jsonObject.getString("event_id")
        val eventName = jsonObject.getString("event_name")
        val eventType = jsonObject.getString("event_type")
        val eventStart = jsonObject.getString("event_start")
        val eventFinish = jsonObject.getString("event_finish")
        val eventInfo = jsonObject.getString("event_info")
        val eventDate = jsonObject.getString("event_date")
        val likeCount = jsonObject.getInt("like_count")

        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

        val formattedDateString = try {
            if (eventDate.isNotEmpty()) {
                val date: Date = inputFormat.parse(eventDate)!!
                outputFormat.format(date)
            } else {
                ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }

        return GeneralEventModel().apply {
            setEventId(eventID.toInt())
            setEventName(eventName)
            setEventType(eventType)
            setEventStartTime(eventStart)
            setEventFinishTime(eventFinish)
            setEventDescription(eventInfo)
            setLikeCount(likeCount)
            setEventDate(formattedDateString)
        }
    }

    private fun navigateToEventDetails(clickedItem: GeneralEventModel) {
        val eventId = clickedItem.getEventId()
        val eventName = clickedItem.getEventName()
        val eventType = clickedItem.getEventType()
        val eventStart = clickedItem.getEventStartTime()
        val eventFinish = clickedItem.getEventFinishTime()
        val eventDescription = clickedItem.getEventDescription()
        val likeCount = clickedItem.getLikeCount()
        val eventDate = clickedItem.getEventDate()

        val fragmentB: Fragment = EventDetails()
        val bundle = Bundle().apply {
            eventId?.let { putInt("event_id", it) }
            putInt("user_id", userId)
            putString("event_name", eventName)
            putString("event_type", eventType)
            putString("event_start", eventStart)
            putString("event_finish", eventFinish)
            putString("event_info", eventDescription)
            putString("event_date", eventDate)
            likeCount?.let { putInt("like_count", it) }
        }

        displayEventDetailsFragment(fragmentB, bundle)
    }

    private fun saveEvent(eventId: Int) {
        val url = "http://10.0.2.2:8888/SaveItem.php"
        sendPostRequest(url, eventId)
    }

    private fun likeEvent(eventId: Int) {
        val url = "http://10.0.2.2:8888/LikeEvent.php"
        sendPostRequest(url, eventId, onSuccess = { storeDataInArray() })
    }

    private fun sendPostRequest(url: String, eventId: Int, onSuccess: (() -> Unit)? = null) {
        val requestQueue = Volley.newRequestQueue(requireContext())
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                showToast(response)
                onSuccess?.invoke()
            },
            { error ->
                showToast("Error: ${error.message}")
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("userid" to userId.toString(), "eventid" to eventId.toString())
            }
        }
        requestQueue.add(stringRequest)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun displayEventDetailsFragment(fragment: Fragment, bundle: Bundle) {
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
