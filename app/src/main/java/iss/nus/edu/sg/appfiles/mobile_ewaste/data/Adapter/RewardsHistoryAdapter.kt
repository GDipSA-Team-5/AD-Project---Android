package iss.nus.edu.sg.appfiles.mobile_ewaste.data.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.appfiles.mobile_ewaste.R
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.RewardsHistoryDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentItemRewardsHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

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
        holder.binding.tvDate.text = formatDate(item.createdAt)
        if (item.points >= 0) {
            holder.binding.tvPoints.text = "+${item.points}"
            holder.binding.tvPoints.setTextColor(0xFF16A34A.toInt())
        } else {
            holder.binding.tvPoints.text = item.points.toString()
            holder.binding.tvPoints.setTextColor(0xFFDC2626.toInt())
        }

        holder.binding.ivIcon.setImageResource(getIconByCategory(item.categoryName))
    }
    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<RewardsHistoryDto>) {
        items = newItems
        notifyDataSetChanged()
    }
    private fun getIconByCategory(category: String): Int {
        return when (category.lowercase()) {
            "mobile", "phone" -> R.drawable.ic_medal
            "laptop", "computer" -> R.drawable.ic_medal
            "battery" -> R.drawable.ic_medal
            "appliance" -> R.drawable.ic_medal
            "cable", "charger" -> R.drawable.ic_medal
            else -> R.drawable.ic_medal
        }
    }

    private fun formatDate(raw: String): String {
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val output = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            output.format(input.parse(raw)!!)
        } catch (e: Exception) {
            raw
        }
    }
    }