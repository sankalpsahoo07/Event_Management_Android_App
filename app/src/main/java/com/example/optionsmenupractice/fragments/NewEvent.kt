package fragments

import Database.DBHelper
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.optionsmenupractice.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class NewEvent : Fragment(R.layout.fragment_new_event) {

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var dbHelper: DBHelper
    private lateinit var button: Button
    private lateinit var selectImageButton: Button // New button for image selection
    private lateinit var imgUri: Uri
    private lateinit var setStart: TextView
    private lateinit var setEnd: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_event, container, false)
        button = view.findViewById(R.id.createneweventBTN)
        // Find the button in the layout
        selectImageButton = view.findViewById(R.id.select_image_button) // Initialize the button
        dbHelper = DBHelper(requireContext())

        val spinner: Spinner = view.findViewById(R.id.event_type)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        val eventname: EditText = view.findViewById(R.id.editTextText)
        val startTime: Button = view.findViewById(R.id.choose_start_time)
        val endTime: Button = view.findViewById(R.id.choose_finish_time)
        setStart = view.findViewById(R.id.set_start_time)
        setEnd = view.findViewById(R.id.set_end_time)
        val event_info = view.findViewById<EditText>(R.id.eventinformation)
        val event_date = view.findViewById<TextView>(R.id.date_text)

        startTime.setOnClickListener {
            showTimePickerDialog(setStart)
        }

        endTime.setOnClickListener {
            showTimePickerDialog(setEnd)
        }

        val datebtn = view.findViewById<Button>(R.id.choose_date)
        datebtn.setOnClickListener {
            showDatePicker(event_date)
        }

        // Image selection button click listener
        selectImageButton.setOnClickListener {
            selectImage() // Call method to select an image
        }

        button.setOnClickListener {
            // Get references to input fields
            val eventName: String = eventname.text.toString()
            val eventType: String = spinner.selectedItem.toString()
            val eventDate: String = event_date.text.toString()
            val eventStart = setStart.text.toString()
            val eventFinish = setEnd.text.toString()
            val info: String = event_info.text.toString()
            val bundle = arguments
            val userId = bundle?.getString("user_id")

            // Validate fields
            if (eventName.isEmpty() || eventType.isEmpty() || eventDate.isEmpty() ||
                eventStart.isEmpty() || eventFinish.isEmpty() || info.isEmpty() || userId.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if imgUri is initialized
            if (!::imgUri.isInitialized || imgUri == Uri.EMPTY) {
                Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val result = dbHelper.insertEvent(eventName, eventType, eventDate, eventStart, eventFinish, imgUri, userId!!)
                withContext(Dispatchers.Main) {
                    if (result) {
                        Toast.makeText(requireContext(), "Event Created Successfully", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), "There has been an issue. Please try again later", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        return view
    }

    private fun showTimePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                textView.text = String.format("%02d:%02d", hourOfDay, minute)
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun showDatePicker(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedYear-${String.format("%02d", selectedMonth + 1)}-${String.format("%02d", selectedDay)}"
            textView.text = selectedDate
        }, year, month, dayOfMonth)

        datePickerDialog.show()
    }

    // Method to select image
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imgUri = data.data ?: Uri.EMPTY // Assign the selected image URI
        }
    }
}
