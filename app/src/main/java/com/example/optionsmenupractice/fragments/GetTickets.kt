package com.example.optionsmenupractice.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.optionsmenupractice.R

class GetTickets : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_get_tickets, container, false)

        val promocode = view.findViewById<EditText>(R.id.promo_code)
        val user_name = view.findViewById<EditText>(R.id.edit_text_name)
        val email = view.findViewById<EditText>(R.id.edit_text_email)
        val phone = view.findViewById<EditText>(R.id.edit_text_phone)
        val quantity = view.findViewById<EditText>(R.id.num_tickets_editText)
        val submit = view.findViewById<Button>(R.id.button_confirm_transaction)
        val event_name = view.findViewById<TextView>(R.id.event_name)
        val event_type = view.findViewById<TextView>(R.id.event_date)

        val bundle = arguments
        val id = bundle?.getInt("event_id")?.toString() ?: ""
        val user_id = bundle?.getInt("user_id")?.toString() ?: ""
        val eventname = bundle?.getString("event_name") ?: "Unknown Event"
        val eventtype = bundle?.getString("event_type") ?: "Unknown Type"

        event_name.text = eventname
        event_type.text = eventtype

        submit.setOnClickListener {
            val nameText = user_name.text.toString()
            val emailText = email.text.toString()
            val phoneText = phone.text.toString()
            val quantityText = quantity.text.toString()

            if (nameText.isEmpty() || emailText.isEmpty() || phoneText.isEmpty() || quantityText.isEmpty()) {
                Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!phoneText.matches("\\d{10}".toRegex())) {
                Toast.makeText(context, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!quantityText.matches("\\d+".toRegex())) {
                Toast.makeText(context, "Please enter a valid ticket quantity", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            submit.isEnabled = false
            bookTickets(user_id, promocode.text.toString(), id, nameText, emailText, phoneText, quantityText)
        }

        return view
    }

    private fun bookTickets(userid: String, promoCode: String, eventID: String, name: String, email: String, phone: String, ticketno: String) {
        val url = "http://10.0.2.2:8888/GetTickets.php"

        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                // Handle successful response
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show()
                view?.findViewById<Button>(R.id.button_confirm_transaction)?.isEnabled = true
            },
            Response.ErrorListener { error ->
                // Handle error
                val networkResponse = error.networkResponse
                val message = if (networkResponse != null && networkResponse.data != null) {
                    String(networkResponse.data)
                } else {
                    "Error: ${error.message}"
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                view?.findViewById<Button>(R.id.button_confirm_transaction)?.isEnabled = true
            }) {

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "promo_code" to promoCode,
                    "event_id" to eventID,
                    "name" to name,
                    "email" to email,
                    "phone" to phone,
                    "ticket_no" to ticketno,
                    "user_id" to userid
                )
            }
        }

        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }
}
