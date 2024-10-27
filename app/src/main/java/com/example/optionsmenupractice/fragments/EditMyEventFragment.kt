package com.example.optionsmenupractice.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.optionsmenupractice.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class EditMyEventFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_my_event, container, false)

        val nametxt: TextView = view.findViewById(R.id.nameEditText)
        val typetxt: TextView = view.findViewById(R.id.typeEditText)
        val datetxt: TextView = view.findViewById(R.id.dateEditText)
        val starttxt: TextView = view.findViewById(R.id.startEditText)
        val finishtxt: TextView = view.findViewById(R.id.finishEditText)
        val infotxt: TextView = view.findViewById(R.id.infoEditText)

        // Retrieve event data from arguments
        val bundle = arguments
        val id = bundle?.getString("event_id", "0") // Default to "0"
        val name = bundle?.getString("event_name", "")
        val type = bundle?.getString("event_type", "")
        val date = bundle?.getString("event_date", "")
        val start = bundle?.getString("event_start", "")
        val finish = bundle?.getString("event_finish", "")
        val info = bundle?.getString("event_info", "")

        // Set initial values to the text views
        nametxt.text = name ?: ""
        typetxt.text = type ?: ""
        datetxt.text = date ?: ""
        starttxt.text = start ?: ""
        finishtxt.text = finish ?: ""
        infotxt.text = info ?: ""

        val submit: Button = view.findViewById(R.id.submit)

        submit.setOnClickListener {
            // Get updated values from the text fields
            val updatedName = nametxt.text.toString()
            val updatedType = typetxt.text.toString()
            val updatedDate = datetxt.text.toString()
            val updatedStart = starttxt.text.toString()
            val updatedFinish = finishtxt.text.toString()
            val updatedInfo = infotxt.text.toString()

            // Start network task with coroutines
            if (id != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val result = updateEvent(
                        updatedName,
                        updatedType,
                        updatedDate,
                        updatedStart,
                        updatedFinish,
                        updatedInfo,
                        id
                    )
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, result, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        return view
    }

    private suspend fun updateEvent(
        eventName: String,
        eventType: String,
        eventDate: String,
        eventStart: String,
        eventFinish: String,
        eventInfo: String,
        eventId: String
    ): String {
        val url = URL("http://10.0.2.2:8888/ManageEvents.php")
        val urlConnection = url.openConnection() as HttpURLConnection
        return try {
            urlConnection.doOutput = true
            urlConnection.requestMethod = "POST"

            // Prepare post data
            val postData = "event_id=$eventId&event_name=$eventName&event_type=$eventType&event_date=$eventDate&event_start=$eventStart&event_finish=$eventFinish&event_info=$eventInfo"

            // Send post data
            urlConnection.outputStream.use { outputStream ->
                outputStream.write(postData.toByteArray())
                outputStream.flush()
            }

            val responseCode = urlConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read response
                val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                response.toString()
            } else {
                "HTTP Error: $responseCode"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error: ${e.message}"
        } finally {
            urlConnection.disconnect()
        }
    }
}
