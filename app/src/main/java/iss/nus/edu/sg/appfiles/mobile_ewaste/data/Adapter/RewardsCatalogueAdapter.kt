package iss.nus.edu.sg.appfiles.mobile_ewaste.data.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.RewardCatalogueDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.ItemRewardCatalogueBinding
import iss.nus.edu.sg.appfiles.mobile_ewaste.ui.rewards.RewardsRules

class RewardsCatalogueAdapter(
    private var items: List<RewardCatalogueDto> = emptyList(),
    private val onRedeem: (RewardCatalogueDto) -> Unit
) : RecyclerView.Adapter<RewardsCatalogueAdapter.ViewHolder>() {

    private var availablePoints: Int = 0

    class ViewHolder(val binding: ItemRewardCatalogueBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRewardCatalogueBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.rewardName.text = item.rewardName
        holder.binding.rewardDescription.text = item.description
        holder.binding.rewardPoints.text = "${item.points} points"
        holder.binding.rewardBadge.text = item.rewardCategory

        holder.binding.rewardImage.load(item.imageUrl)

        val redeemEnabled = RewardsRules.canRedeem(
            availablePoints = availablePoints,
            rewardPoints = item.points,
            rewardAvailable = item.availability,
            stockQuantity = item.stockQuantity
        )
        holder.binding.redeemButton.isEnabled = redeemEnabled
        holder.binding.redeemButton.alpha = if (redeemEnabled) 1f else 0.5f
        holder.binding.redeemButton.setOnClickListener {
            onRedeem(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<RewardCatalogueDto>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun updateAvailablePoints(points: Int) {
        availablePoints = points
        notifyDataSetChanged()
    }
}
