package com.example.madstagrarn.following

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madstagrarn.dataclass.Phone
import com.example.madstagrarn.R

class UnsignedUserAdapter (private val phoneList: ArrayList<Phone>)
    : RecyclerView.Adapter<UnsignedUserAdapter.UnsignedUserViewHolder>() {
    class UnsignedUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name_text)
        var phoneNumber: TextView = itemView.findViewById(R.id.phonenumber_text)
        var profileImage: ImageView = itemView.findViewById(R.id.following_profile_image)
        var inviteButton: Button = itemView.findViewById(R.id.invite_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnsignedUserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.unsigned_user_item, parent, false)
        return UnsignedUserViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: UnsignedUserViewHolder, position: Int) {
        val currentItem = phoneList[position]

        holder.name.text = currentItem.name
        holder.phoneNumber.text = currentItem.phoneNumber
        holder.profileImage.setImageResource(R.drawable.person)
    }

    override fun getItemCount(): Int {
        return phoneList.size
    }
}