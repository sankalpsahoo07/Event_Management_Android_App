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

class MyEventsAdapter(
    private var myEventsList: List<MyEventsModel>, // Use List instead of ArrayList
    private val onItemClick: (MyEventsModel) -> Unit,
    private val onEditClick: (MyEventsModel) -> Unit,
    private val onViewClick: (MyEventsModel) -> Unit
) : RecyclerView.Adapter<MyEventsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.new_card_view, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = myEventsList[position]
        holder.bind(currentItem)
    }

    fun updateData(newEvents: List<MyEventsModel>) {
        myEventsList = newEvents
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return myEventsList.size
    }

    // ViewHolder class to hold item views
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventName: TextView = itemView.findViewById(R.id.eventname)
        private val eventType: TextView = itemView.findViewById(R.id.eventtype)
        private val imageView: ImageView = itemView.findViewById(R.id.imgView)
        private val editButton: Button = itemView.findViewById(R.id.button4)
        private val viewButton: Button = itemView.findViewById(R.id.button3)

        // Properly declare currentItem
        private lateinit var currentItem: MyEventsModel

        init {
            itemView.setOnClickListener {
                if (::currentItem.isInitialized) {
                    onItemClick.invoke(currentItem)
                }
            }

            editButton.setOnClickListener {
                if (::currentItem.isInitialized) {
                    onEditClick.invoke(currentItem)
                }
            }

            viewButton.setOnClickListener {
                if (::currentItem.isInitialized) {
                    onViewClick.invoke(currentItem)
                }
            }
        }

        // Bind item data to views
        fun bind(item: MyEventsModel) {
            currentItem = item // Initialize currentItem
            eventName.text = item.getEventName()
            eventType.text = item.getEventType()

            // Load image using Glide
            Glide.with(itemView.context)
                .load("https://cdn.pixabay.com/photo/2013/02/01/18/14/url-77169_1280.jpg")
                .error(R.drawable.baseline_error_24)
                .placeholder(R.drawable.baseline_downloading_24)
                .into(imageView)
        }
    }
}
