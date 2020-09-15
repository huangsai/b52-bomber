package com.mobile.app.bomber.tik.home.items

import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem
import com.mobile.app.bomber.data.http.entities.ApiAtUser
import com.mobile.app.bomber.tik.R
import com.mobile.app.bomber.tik.databinding.ItemAtUserBinding

class AtUserItem(val data: ApiAtUser.User) : SimpleRecyclerItem() {

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(ItemAtUserBinding::bind)
        binding.txtName.text = data.username
        holder.attachImageLoader(R.id.img_profile)
        holder.attachOnClickListener(R.id.item_at_user)
    }

    override fun getLayout(): Int = R.layout.item_at_user
}