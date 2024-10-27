package adapters

import models.MyEventsModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.optionsmenupractice.R
import com.example.optionsmenupractice.databinding.ManageEventsRowBinding // Ensure you have this generated

class ManageEventsAdapter(
    private var eventList: ArrayList<MyEventsModel>,
    private val onItemClick: (MyEventsModel) -> Unit // Callback for item click event
) : RecyclerView.Adapter<ManageEventsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate the layout using View Binding
        val binding = ManageEventsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = eventList[position]
        holder.bind(currentItem)

        holder.viewButton.setOnClickListener {
            onItemClick(currentItem) // Invoke onItemClick callback
        }
    }

    fun updateData(newEvents: List<MyEventsModel>) {
        eventList.clear()
        eventList.addAll(newEvents)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    inner class MyViewHolder(private val binding: ManageEventsRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.imgView
        val viewButton: Button = binding.button3

        fun bind(item: MyEventsModel) {
            binding.eventname.text = item.getEventName()
            binding.eventtype.text = item.getEventType()

            // Load image using Glide
            Glide.with(binding.imgView.context)
                .load("https://cdn.pixabay.com/photo/2013/02/01/18/14/url-77169_1280.jpg") // Replace with item.imageUrl if available
                .error(R.drawable.baseline_error_24)
                .placeholder(R.drawable.baseline_downloading_24)
                .into(binding.imgView)
        }
    }
}
