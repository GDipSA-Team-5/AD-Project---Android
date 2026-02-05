package iss.nus.edu.sg.appfiles.mobile_ewaste.data.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.DisposalHistoryDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentItemDisposalHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class DisposalHistoryAdapter(private var items:List<DisposalHistoryDto> = emptyList())
    : RecyclerView.Adapter<DisposalHistoryAdapter.ViewHolder>() {
    class ViewHolder(val binding: FragmentItemDisposalHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentItemDisposalHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.itemTitle.text =
            item.itemTypeName ?: "Item ${item.itemTypeId}"

        holder.binding.itemPoints.text =
            "+${item.earnedPoints} pts"

        holder.binding.itemMeta.text =
            "${formatWeight(item.estimatedTotalWeight)} · ${item.categoryName ?: "Category"}"

        val cat = EWasteCategory.from(item.categoryName)
        holder.binding.ivIcon.setImageResource(cat.iconRes)

        holder.binding.itemTime.text =
            formatTime(item.disposalTimeStamp)

        holder.binding.itemLocation.text =
            item.locationName
                ?: item.binLocationName
                        ?: "Unknown"
    }

    override fun getItemCount(): Int = items.size
    fun submitList(newItems: List<DisposalHistoryDto>) {
        items = newItems
        notifyDataSetChanged()
    }

    private fun formatWeight(weight: Double): String {
        return String.format(Locale.getDefault(), "%.2f kg", weight)
    }

    private fun formatTime(iso: String): String {
        return try {
            val clean = iso.removeSuffix("Z")
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")

            val date = parser.parse(clean)
            val out = SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault())
            out.format(date!!)
        } catch (e: Exception) {
            iso.take(16).replace("T", " ")
        }
    }
}
