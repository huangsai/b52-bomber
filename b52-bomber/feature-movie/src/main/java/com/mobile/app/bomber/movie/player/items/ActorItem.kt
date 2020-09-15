package com.mobile.app.bomber.movie.player.items

import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieItemMovieActorBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class ActorItem : SimpleRecyclerItem() {

    override fun bind(holder: AdapterViewHolder) {
        val binding: MovieItemMovieActorBinding = holder.binding(MovieItemMovieActorBinding::bind)
        binding.txtDetail.text = "刘德华"
        holder.attachImageLoader(R.id.img_profile)
    }

    override fun getLayout(): Int = R.layout.movie_item_movie_actor
}