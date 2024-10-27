package adapters

import models.MyTicketsModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.optionsmenupractice.R

class MyTicketsAdapter(
    private val myTicketList: List<MyTicketsModel> // Use List instead of ArrayList
) : RecyclerView.Adapter<MyTicketsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.my_tickets_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = myTicketList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return myTicketList.size
    }

    // ViewHolder class to hold item views
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventName: TextView = itemView.findViewById(R.id.eventname)
        private val eventType: TextView = itemView.findViewById(R.id.eventtype)
        private val eventInfo: TextView = itemView.findViewById(R.id.event_info)
        private val numOfTickets: TextView = itemView.findViewById(R.id.num_of_tickes)

        // Bind item data to views
        fun bind(item: MyTicketsModel) {
            eventName.text = item.getEventName()
            eventType.text = item.getEventType()
            eventInfo.text = item.getEventDescription()
            val quantityText = "Ticket Quantity: ${item.getTicketNo()}"
            numOfTickets.text = quantityText
        }
    }
}
