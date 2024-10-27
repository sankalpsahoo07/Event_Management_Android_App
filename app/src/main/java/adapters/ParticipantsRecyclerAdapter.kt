package adapters

import models.ParticipantModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.optionsmenupractice.R
import com.example.optionsmenupractice.databinding.ParticipantsRowBinding // Import your generated binding class

class ParticipantsRecyclerAdapter(
    private var participantList: ArrayList<ParticipantModel>
) : RecyclerView.Adapter<ParticipantsRecyclerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ParticipantsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = participantList[position]
        holder.bind(currentItem)
    }

    fun updateData(myParticipants: ArrayList<ParticipantModel>) {
        participantList = myParticipants
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return participantList.size
    }

    inner class MyViewHolder(private val binding: ParticipantsRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ParticipantModel) {
            // Set the participant details in the corresponding TextViews
            binding.username.text = item.name           // Access property directly
            binding.ticketQuantity.text = "${item.quantity}" // Access property directly
            binding.email.text = "Email: ${item.email}" // Access property directly
            binding.phoneno.text = "Phone Number: ${item.phoneNo}" // Access property directly
        }
    }
}
