package requests

import models.GeneralEventModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

class RequestAllEvents {

    fun fetchEvents(onSuccess: (ArrayList<GeneralEventModel>) -> Unit, onFailure: (String) -> Unit) {
        val url = "http://10.0.2.2:8888/all_events.php"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    onFailure("HTTP Error: ${response.code}")
                    return
                }

                val responseBody = response.body?.string()
                if (responseBody.isNullOrBlank()) {
                    onFailure("Empty response body")
                    return
                }

                try {
                    val jsonArray = JSONArray(responseBody)
                    val events = ArrayList<GeneralEventModel>()

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val event = GeneralEventModel().apply {
                            setEventName(jsonObject.getString("event_name"))
                            setEventType(jsonObject.getString("event_type"))
                            setEventStartTime(jsonObject.getString("event_start"))
                            setEventFinishTime(jsonObject.getString("event_finish"))
                            setEventDescription(jsonObject.getString("event_info"))
                        }
                        events.add(event)
                    }

                    onSuccess(events)
                } catch (e: Exception) {
                    onFailure("Error parsing JSON: ${e.message}")
                }
            }
        })
    }
}
