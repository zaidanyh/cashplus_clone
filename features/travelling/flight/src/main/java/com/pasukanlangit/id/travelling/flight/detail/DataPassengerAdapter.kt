package com.pasukanlangit.id.travelling.flight.detail

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.core.utils.setDropDownView
import com.pasukanlangit.id.travelling.flight.R
import com.pasukanlangit.id.travelling.flight.databinding.ItemPassengerDataBinding
import java.text.SimpleDateFormat
import java.util.*

class DataPassengerAdapter(
    private val passengers: List<String>
): RecyclerView.Adapter<DataPassengerAdapter.FlightPriceViewHolder>() {

    inner class FlightPriceViewHolder(val binding: ItemPassengerDataBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightPriceViewHolder {
        return FlightPriceViewHolder(ItemPassengerDataBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FlightPriceViewHolder, position: Int) {
        with(holder.binding) {
            tvTypePassenger.text = passengers[position]
            if (passengers[position].contains("Anak") || passengers[position].contains("Bayi")) {
                txtNIK.text = root.context.getString(R.string.nik_or_student_id)
                edtNik.hint = root.context.getString(R.string.input_nik_or_student_id)
            } else {
                txtNIK.text = root.context.getString(R.string.nik_number)
                edtNik.hint = root.context.getString(R.string.input_nik_number)
            }

            val adapter = setDropDownView(root.context, listOf("Pilih", "Mr.", "Mrs.", "Ms."))
            adapter.setDropDownViewResource(R.layout.item_spinner_small)
            spinnerTitelPassenger.adapter = adapter
            setUpDateBirthClick(edtBirthDate, root.context)
        }
    }

    override fun getItemCount(): Int = passengers.size

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