package com.pasukanlangit.cashplus.ui.pembayarancart.courir

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.databinding.ItemListPriceCourierBinding
import com.pasukanlangit.cashplus.model.response.CostsItem
import com.pasukanlangit.cashplus.utils.MyUtils
import java.lang.StringBuilder

@SuppressLint("SetTextI18n")
class CourierPriceListAdapter(private val label: String, private val products: List<CostsItem>, private val listener : (CostsItem) -> Unit): RecyclerView.Adapter<CourierPriceListAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: ItemListPriceCourierBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = ItemListPriceCourierBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentPrice = products[position]

        with(holder.binding){
            tvTitleCourier.text = "$label ${currentPrice.service} (${MyUtils.getNumberRupiah(currentPrice.cost[0].value)})"
            val desc = StringBuilder()
            if(currentPrice.cost[0].note.isNotEmpty()){
                desc.append(currentPrice.cost[0].note + "\n")
            }
            if(currentPrice.cost[0].etd.isNotEmpty()) {
                desc.append("${desc}Estimasi Tiba ${
                    currentPrice.cost[0].etd.replace(
                        "hari",
                        "",
                        true
                    )
                } Hari")
            }
            tvDescCourier.text =  desc

            root.setOnClickListener {
                listener(currentPrice.copy(prefix = label))
            }
        }
    }

    override fun getItemCount(): Int = products.size
}