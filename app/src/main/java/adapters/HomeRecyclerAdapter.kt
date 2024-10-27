package adapters

import models.GeneralEventModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.optionsmenupractice.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeRecyclerAdapter(
    private var generalList: ArrayList<GeneralEventModel>,
    private val onItemClick: (GeneralEventModel) -> Unit,
    private val onSaveClick: (GeneralEventModel) -> Unit,
    private val onLikeClicked: (GeneralEventModel) -> Unit
) : RecyclerView.Adapter<HomeRecyclerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.modern_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = generalList[position]
        holder.bind(currentItem)

        // Click listener for the item
        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }

        holder.like_event.setOnClickListener {
            onLikeClicked(currentItem)
        }

        holder.saveforLater.setOnClickListener {
            onSaveClick(currentItem)
        }
    }

    fun updateData(newEvents: ArrayList<GeneralEventModel>) {
        generalList = newEvents
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return generalList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var currentItem: GeneralEventModel

        val eventname: TextView = itemView.findViewById(R.id.eventname)
        val eventtype: TextView = itemView.findViewById(R.id.eventtype)
        val eventinfo: TextView = itemView.findViewById(R.id.event_info)
        val saveforLater: Button = itemView.findViewById(R.id.save)
        val like_count: TextView = itemView.findViewById(R.id.like_count)
        val like_event: FloatingActionButton = itemView.findViewById(R.id.fab1)

        fun bind(item: GeneralEventModel) {
            currentItem = item
            eventname.text = item.getEventName()
            eventtype.text = item.getEventType()
            eventinfo.text = item.getEventDescription() // Assuming you have this method
            like_count.text = "Likes: ${item.getLikeCount()}" // Ensure getLikeCount() returns a String or Int
        }
    }
}
