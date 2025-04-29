package com.example.progressiomobileapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.progressiomobileapp.data.ChecklistItem
import com.example.progressiomobileapp.databinding.ItemChecklistBinding


class ChecklistItemAdapter(private val checklistItems: List<ChecklistItem>) : RecyclerView.Adapter<ChecklistItemAdapter.ChecklistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val binding = ItemChecklistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChecklistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        val item = checklistItems[position]
        holder.bind(item)
    }


    override fun getItemCount() = checklistItems.size

    class ChecklistViewHolder(private val binding: ItemChecklistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChecklistItem) {
            binding.checklistItemText.text = item.itemText
            binding.checklistCheckBox.isChecked = item.isChecked == 1
            binding.checklistCheckBox.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = if (isChecked) 1 else 0
                // Update the item in the database if needed
            }
        }
    }
}
