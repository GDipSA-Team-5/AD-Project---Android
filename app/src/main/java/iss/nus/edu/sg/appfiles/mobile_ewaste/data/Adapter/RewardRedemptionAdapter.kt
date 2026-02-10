package iss.nus.edu.sg.appfiles.mobile_ewaste.data.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import coil.load
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.RewardRedemptionItemDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.ItemRewardRedemptionBinding
import java.text.SimpleDateFormat
import java.util.Locale

class RewardRedemptionAdapter(
    private var items: List<RewardRedemptionItemDto> = emptyList(),
    private val onUse: (RewardRedemptionItemDto, String) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<RewardRedemptionAdapter.ViewHolder>() {
    private val usedRedemptionIds: MutableSet<Int> = mutableSetOf()

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
        val isUsed = usedRedemptionIds.contains(item.redemptionId) ||
            item.redemptionStatus.equals("USED", ignoreCase = true)
        holder.binding.rewardStatus.text = if (isUsed) "USED" else item.redemptionStatus
        holder.binding.rewardDate.text = formatDate(item.redemptionDateTime)
        holder.binding.rewardThumb.load(item.imageUrl)
        holder.binding.rewardUseButton.isEnabled = !isUsed
        holder.binding.rewardUseButton.text = if (isUsed) "USED" else "USE"
        holder.binding.rewardUseButton.setOnClickListener {
            showVendorCodeDialog(holder, item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<RewardRedemptionItemDto>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun markUsed(redemptionId: Int) {
        usedRedemptionIds.add(redemptionId)
        notifyDataSetChanged()
    }

    private fun showVendorCodeDialog(holder: ViewHolder, item: RewardRedemptionItemDto) {
        val context = holder.itemView.context
        val input = EditText(context).apply {
            hint = "Vendor Code"
        }
        AlertDialog.Builder(context)
            .setTitle("Enter Vendor Code")
            .setView(input)
            .setPositiveButton("Submit") { _, _ ->
                val code = input.text?.toString()?.trim().orEmpty()
                onUse(item, code)
            }
            .setNegativeButton("Cancel", null)
            .show()
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
