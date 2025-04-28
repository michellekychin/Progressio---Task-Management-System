package com.example.progressiomobileapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.progressiomobileapp.data.Notification
import com.example.progressiomobileapp.databinding.ItemNotificationBinding


class NotificationAdapter(private var notifications: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.bind(notification)
    }

    override fun getItemCount(): Int = notifications.size

    class NotificationViewHolder(private val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) {
            binding.notificationTitle.text = notification.message
            binding.notificationDate.text = notification.createdAt
            binding.notificationStatus.setBackgroundColor(
                if (notification.isRead == 1) android.graphics.Color.GRAY else android.graphics.Color.BLUE
            )
        }
    }
}
