package com.pasukanlangit.id.travelling.ship.fill

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.core.utils.setDropDownView
import com.pasukanlangit.id.travelling.ship.R
import com.pasukanlangit.id.travelling.ship.databinding.ItemShipPassengersBinding
import java.text.SimpleDateFormat
import java.util.*

class ShipPassengersAdapter(
    private val passengers: List<String>
): RecyclerView.Adapter<ShipPassengersAdapter.ShipPassengerViewHolder>() {

    inner class ShipPassengerViewHolder(val binding: ItemShipPassengersBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShipPassengerViewHolder {
        return ShipPassengerViewHolder(ItemShipPassengersBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ShipPassengerViewHolder, position: Int) {
        with(holder.binding) {
            tvTypePassenger.text = passengers[position]
            if (passengers[position].contains("Bayi")) {
                txtNikShip.text = root.context.getString(R.string.nik_or_student_id)
                edtNikShip.hint = root.context.getString(R.string.input_nik_or_student_id)
            } else {
                txtNikShip.text = root.context.getString(R.string.nik_number)
                edtNikShip.hint = root.context.getString(R.string.input_nik_number)
            }
            val adapter = setDropDownView(root.context, listOf("Pilih", "Mr.", "Mrs.", "Ms."))
            adapter.setDropDownViewResource(R.layout.item_spinner_small)
            spinnerTitleShipPassenger.adapter = adapter
            setUpDateBirthClick(edtBirthDatePassengerShip, root.context)
        }
    }

    override fun getItemCount(): Int = passengers.size

    override fun getItemViewType(position: Int): Int = position

    private fun setUpDateBirthClick(
        edtText: EditText,
        context: Context
    ) {
        edtText.setOnClickListener {
            val calender = Calendar.getInstance()
            val year = calender.get(Calendar.YEAR)
            val month = calender.get(Calendar.MONTH)
            val day = calender.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                context, R.style.DatePickerCustom,
                { _, _, _, _ -> }, year, month, day
            )

            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

            datePickerDialog.setButton(
                DialogInterface.BUTTON_POSITIVE,
                context.getString(R.string.choose)
            ) { _, which ->
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    val datepicker = datePickerDialog.datePicker

                    val dayPicked = datepicker.dayOfMonth
                    val monthPicked = datepicker.month
                    val yearPicked = datepicker.year
                    calender.set(yearPicked, monthPicked, dayPicked)

                    val simpleDateFormat =
                        SimpleDateFormat("dd MMMM yyyy",
                            Locale(
                                context.getString(R.string.locale_language),
                                context.getString(R.string.locale_country)
                            )
                        )
                    val formatDate = simpleDateFormat.format(calender.time)

                    edtText.setText(formatDate)
                }
            }
            datePickerDialog.setButton(
                DialogInterface.BUTTON_NEGATIVE,
                context.getString(R.string.cancel)
            ) { _, which ->
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    datePickerDialog.dismiss()
                }
            }
            datePickerDialog.show()
        }
    }
}