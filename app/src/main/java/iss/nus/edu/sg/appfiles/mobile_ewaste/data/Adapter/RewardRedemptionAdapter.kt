package iss.nus.edu.sg.appfiles.mobile_ewaste.data.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.RewardRedemptionItemDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.ItemRewardRedemptionBinding
import java.text.SimpleDateFormat
import java.util.Locale

class RewardRedemptionAdapter(
    private var items: List<RewardRedemptionItemDto> = emptyList(),
    private val onUse: (RewardRedemptionItemDto) -> Unit = {}
) : RecyclerView.Adapter<RewardRedemptionAdapter.ViewHolder>() {
    private var userId: Int? = null

    class ViewHolder(val binding: ItemRewardRedemptionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRewardRedemptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.rewardTitle.text = item.rewardName
        holder.binding.rewardStatus.text = item.redemptionStatus
        holder.binding.rewardDate.text = formatDate(item.redemptionDateTime)
        holder.binding.rewardThumb.load(item.imageUrl)
        holder.binding.rewardUseButton.setOnClickListener {
            val code = userId?.let { "Code: U${it}R${item.redemptionId}" } ?: "Code: UR${item.redemptionId}"
            holder.binding.rewardUseButton.text = code
            holder.binding.rewardUseButton.isEnabled = false
            onUse(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<RewardRedemptionItemDto>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun setUserId(id: Int?) {
        userId = id
    }

    private fun formatDate(raw: String): String {
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val output = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            output.format(input.parse(raw)!!)
        } catch (e: Exception) {
            raw
        }
    }
}
