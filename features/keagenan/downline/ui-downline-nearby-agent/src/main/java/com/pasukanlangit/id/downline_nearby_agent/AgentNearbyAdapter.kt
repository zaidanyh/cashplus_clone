package com.pasukanlangit.id.downline_nearby_agent

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.domain_downline.model.AgentNearBy
import com.pasukanlangit.id.downline_nearby_agent.databinding.ItemAgentBinding

class AgentNearbyAdapter(
    private val onItemClick: (Lat: Double, long: Double) -> Unit
): RecyclerView.Adapter<AgentNearbyAdapter.AgentViewHolder>(), Filterable {

    private var agens = listOf<AgentNearBy>()
    private var agensFiltered = agens

    fun setAgents(agens: List<AgentNearBy>?) {
        if (agens.isNullOrEmpty()) return
        this.agens = agens
        agensFiltered = agens
        notifyDataSetChanged()
    }

    inner class AgentViewHolder(val binding: ItemAgentBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(agent: AgentNearBy) {
            with(binding) {
                tvAddressAgent.isVisible = agent.address.isNotEmpty()
                tvNameAgent.text = agent.name
                tvAddressAgent.text = agent.address
                tvDistance.text = root.context.getString(R.string.distance_format, agent.distance)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AgentNearbyAdapter.AgentViewHolder {
        val binding = ItemAgentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AgentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AgentNearbyAdapter.AgentViewHolder, position: Int) {
        holder.bind(agensFiltered[position])
        holder.binding.root.setOnClickListener {
            onItemClick(agensFiltered[holder.bindingAdapterPosition].lat, agensFiltered[holder.bindingAdapterPosition].long)
        }
    }

    override fun getItemCount(): Int = agensFiltered.size
    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(char: CharSequence?): FilterResults {
                val query = char.toString()
                agensFiltered = if (query.isEmpty()) agens
                else {
                    val filteredList = mutableListOf<AgentNearBy>()
                    for (i in agens) {
                        if ("""(?i)($query)""".toRegex().containsMatchIn(i.name))
                            filteredList.add(i)
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = agensFiltered
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                agensFiltered = results?.values as List<AgentNearBy>
                notifyDataSetChanged()
            }
        }
    }
}