package com.hackfest.swiftaid.adapter

import android.content.Context
import android.util.Log.e
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.hackfest.swiftaid.databinding.ListItemAmbulanceBinding
import com.hackfest.swiftaid.models.Ambulance

class AmbulancesAdapter(private val onComplete: (String) -> Unit) :
    RecyclerView.Adapter<AmbulancesAdapter.AmbulanceViewHolder>() {
    private lateinit var context: Context

    private var ambulanceList = ArrayList<Ambulance>()

    inner class AmbulanceViewHolder(val binding: ListItemAmbulanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmbulanceViewHolder {
        val binding =
            ListItemAmbulanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context

        return AmbulanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AmbulanceViewHolder, position: Int) {

        val ambulance = ambulanceList[position]

        var strEquipments = ""
        holder.binding.title.text = ambulance.driverName
        holder.binding.phone.text = ambulance.driverNumber
        holder.binding.vechicleNumber.text = ambulance.vehicleNumber
        if (ambulance.busy == true) {
            holder.binding.status.text = "Busy"
        } else {
            holder.binding.status.text = "Idle"
        }
        if (ambulance.ventilator == true) {
            strEquipments += "Ventilator, "
        }
        if (ambulance.suctionUnit == true) {
            strEquipments += "Suction Unit, "
        }
        if (ambulance.ecgMonitor == true) {
            strEquipments += "ECG Monitor"
        }
        holder.binding.equipment.text = strEquipments
        holder.binding.root.setOnClickListener {
                    onComplete(ambulance.vehicleNumber!!)




        }
    }

    fun updateAmbulanceList(ambulanceList: List<Ambulance>) {

        this.ambulanceList.clear()
        notifyItemRangeRemoved(0, this.ambulanceList.lastIndex)
        this.ambulanceList.addAll(ambulanceList)
        notifyItemRangeInserted(0, ambulanceList.lastIndex)

    }

    fun setFilteredList(ambulanceList: ArrayList<Ambulance>) {
        this.ambulanceList.clear()
        this.ambulanceList = ambulanceList
        notifyItemRangeInserted(0, ambulanceList.lastIndex)
    }

    override fun getItemCount() = ambulanceList.size


}

