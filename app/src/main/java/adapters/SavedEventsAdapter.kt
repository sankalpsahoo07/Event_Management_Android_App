package adapters

import models.GeneralEventModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.optionsmenupractice.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SavedEventsAdapter(
    private var savedList: List<GeneralEventModel>, // Changed to List for better flexibility
    private val onItemClick: (GeneralEventModel) -> Unit,
    private val onRemoveClick: (GeneralEventModel) -> Unit
) : RecyclerView.Adapter<SavedEventsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.saved_item_row, parent, false)
        return MyViewHolder(itemView, onItemClick, onRemoveClick)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = savedList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int = savedList.size

    // Update the data in the adapter
    fun updateData(newEvents: List<GeneralEventModel>) {
        savedList = newEvents
        notifyDataSetChanged()
    }

    // Method to clear the data
    fun clearData() {
        savedList = emptyList()
        notifyDataSetChanged()
    }

    class MyViewHolder(
        itemView: View,
        private val onItemClick: (GeneralEventModel) -> Unit,
        private val onRemoveClick: (GeneralEventModel) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val eventName: TextView = itemView.findViewById(R.id.eventname)
        private val eventType: TextView = itemView.findViewById(R.id.eventtype)
        private val eventInfo: TextView = itemView.findViewById(R.id.event_info)
        private val removeEvent: FloatingActionButton = itemView.findViewById(R.id.remove_saved_item)

        init {
            itemView.setOnClickListener {
                val item = it.tag as? GeneralEventModel
                item?.let(onItemClick)
            }
        }

        fun bind(item: GeneralEventModel) {
            itemView.tag = item // Set tag to access in onClickListener
            eventName.text = item.getEventName()
            eventType.text = item.getEventType()
            eventInfo.text = item.getEventDescription()

            removeEvent.setOnClickListener {
                onRemoveClick(item)
            }
        }
    }
}
