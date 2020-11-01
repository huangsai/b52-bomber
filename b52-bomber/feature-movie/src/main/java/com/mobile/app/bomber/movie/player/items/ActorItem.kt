package com.mobile.app.bomber.movie.player.items

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mobile.app.bomber.data.http.entities.ApiMovieDetailById
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieItemMovieActorBinding
import com.mobile.ext.glide.GlideApp
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class ActorItem : SimpleRecyclerItem() {

    var data: ApiMovieDetailById.Performer? = null

    fun getData(dataPre: ApiMovieDetailById.Performer?){
        data = dataPre
    }

    override fun bind(holder: AdapterViewHolder) {
        val binding: MovieItemMovieActorBinding = holder.binding(MovieItemMovieActorBinding::bind)

            binding.txtDetail.text = data!!.name
            GlideApp.with(holder.activity<FragmentActivity>())
                    .load(data!!.img)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.imgProfile)
//        holder.attachImageLoader(R.id.img_profile)
            holder.attachOnClickListener(R.id.img_profile)

    }

    override fun getLayout(): Int = R.layout.movie_item_movie_actor
}