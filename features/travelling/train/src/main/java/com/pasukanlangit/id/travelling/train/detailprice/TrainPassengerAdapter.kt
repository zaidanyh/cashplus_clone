package com.pasukanlangit.id.travelling.train.detailprice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.core.utils.setDropDownView
import com.pasukanlangit.id.travelling.train.R
import com.pasukanlangit.id.travelling.train.databinding.ItemTrainPassengersBinding

class TrainPassengerAdapter(
    private val passengers: List<String>
): RecyclerView.Adapter<TrainPassengerAdapter.TrainPassengerViewHolder>() {

    inner class TrainPassengerViewHolder(val binding: ItemTrainPassengersBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainPassengerViewHolder {
        return TrainPassengerViewHolder(ItemTrainPassengersBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TrainPassengerViewHolder, position: Int) {
        with(holder.binding) {
            tvTypePassenger.text = passengers[position]
            if (passengers[position].contains("Bayi")) {
                txtNikTrain.text = root.context.getString(R.string.nik_or_student_id)
                edtNikTrain.hint = root.context.getString(R.string.input_nik_or_student_id)
            } else {
                txtNikTrain.text = root.context.getString(R.string.nik_number)
                edtNikTrain.hint = root.context.getString(R.string.input_nik_number)
            }

            val adapter = setDropDownView(root.context, listOf("Pilih", "Mr.", "Mrs.", "Ms."))
            adapter.setDropDownViewResource(R.layout.item_spinner_small)
            spinnerTitleTrainPassenger.adapter = adapter
        }
    }

    override fun getItemCount(): Int = passengers.size
}