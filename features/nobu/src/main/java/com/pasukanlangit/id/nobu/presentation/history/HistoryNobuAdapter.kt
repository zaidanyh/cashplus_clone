package com.pasukanlangit.id.nobu.presentation.history

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.core.utils.InputUtil.toCapitalize
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiahWithoutRp
import com.pasukanlangit.id.nobu.R
import com.pasukanlangit.id.nobu.databinding.ItemHistoryNobuBinding
import com.pasukanlangit.id.nobu.domain.model.DataTrx
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryNobuAdapter: RecyclerView.Adapter<HistoryNobuAdapter.HistoryViewHolder>(), Filterable {

    private val dataTrx = ArrayList<DataTrx>()
    private var dataFiltered: List<DataTrx> = dataTrx

    fun setDataTrx(dataTrx: List<DataTrx>?) {
        if (dataTrx == null) return
        this.dataTrx.clear()
        this.dataTrx.addAll(dataTrx)
    }

    inner class HistoryViewHolder(val binding: ItemHistoryNobuBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(ItemHistoryNobuBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val data = dataFiltered[position]
        with(holder.binding) {
            val dateTime = reFormattingDateTime(data.dateTimeTrx)
            dateTrx.text = dateTime.first()
            timeTrx.text = root.context.getString(R.string.clock_history_trx, dateTime.last())
            when(data.type) {
                "TOPUP" -> {
                    iconType.setImageResource(R.drawable.ic_topup_nobu)
                    tvRemark.text = root.context.getString(R.string.remark_trx, data.remark)
                    tvNominal.text = root.context.getString(R.string.nominal_trx, data.currency, getNumberRupiahWithoutRp(data.amount.split(".").first().toDoubleOrNull()))
                }
                "PURCHASE" -> {
                    iconType.setImageResource(R.drawable.ic_pay_nobu)
                    tvRemark.text = root.context.getString(R.string.nominal_trx, data.type,data.remark).toCapitalize()
                    tvNominal.text = root.context.getString(R.string.nominal_trx, data.currency, getNumberRupiahWithoutRp(data.amount.split(".").first().toDoubleOrNull()))
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int = position

    override fun getItemCount(): Int = dataFiltered.size

    private fun reFormattingDateTime(dateTime: String): List<String> {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale("in", "ID"))
        val dateFormatted = formatter.parse(dateTime)

        val newFormatter = SimpleDateFormat(
            "EEEE, dd MMMM yyyy-HH:mm", Locale("in", "ID")
        )
        newFormatter.timeZone = TimeZone.getTimeZone("GMT+07:00")
        val newDateString = newFormatter.format(dateFormatted as Date)

        return newDateString.split("-")
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(char: CharSequence?): FilterResults {
                val query = char.toString()
                dataFiltered = if (query.isEmpty()) {
                    dataTrx
                } else {
                    val filteredList: MutableList<DataTrx> = ArrayList()
                    for (i in dataTrx) {
                        if (query == i.type) filteredList.add(i)
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = dataFiltered
                return filterResults
            }

            override fun publishResults(char: CharSequence?, result: FilterResults?) {
                dataFiltered = result?.values as List<DataTrx>
                notifyDataSetChanged()
            }
        }
    }
}