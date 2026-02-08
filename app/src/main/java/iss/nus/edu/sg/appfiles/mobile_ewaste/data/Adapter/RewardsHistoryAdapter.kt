package iss.nus.edu.sg.appfiles.mobile_ewaste.data.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.appfiles.mobile_ewaste.R
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.RewardsHistoryDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentItemRewardsHistoryBinding
import iss.nus.edu.sg.appfiles.mobile_ewaste.ui.rewards.RewardsHistoryDateFormatter

class RewardsHistoryAdapter(private var items:List<RewardsHistoryDto> = emptyList())
    : RecyclerView.Adapter<RewardsHistoryAdapter.ViewHolder>() {
    class ViewHolder(val binding: FragmentItemRewardsHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentItemRewardsHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]

        holder.binding.tvTitle.text = item.title
        holder.binding.tvDate.text = RewardsHistoryDateFormatter.format(item.createdAt)
        if (item.points >= 0) {
            holder.binding.tvPoints.text = "+${item.points}"
            holder.binding.tvPoints.setTextColor(0xFF16A34A.toInt())
            holder.binding.ivIcon.setColorFilter(
                ContextCompat.getColor(holder.itemView.context, R.color.brand_green)
            )
        } else {
            holder.binding.tvPoints.text = item.points.toString()
            holder.binding.tvPoints.setTextColor(0xFFDC2626.toInt())
            holder.binding.ivIcon.setColorFilter(
                ContextCompat.getColor(holder.itemView.context, R.color.brand_red)
            )
        }
    }
    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<RewardsHistoryDto>) {
        items = newItems
        notifyDataSetChanged()
    }

}
