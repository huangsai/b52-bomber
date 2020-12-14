package com.mobile.app.bomber.tik.video.items

import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem
import com.mobile.app.bomber.tik.R
import com.mobile.app.bomber.tik.databinding.VideoLocalItemBinding
import com.mobile.app.bomber.tik.video.MediaData
import kotlin.math.roundToInt
//初始化本地视频配置
class VideoLocalItem(val data: MediaData) : SimpleRecyclerItem() {

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(VideoLocalItemBinding::bind)
        holder.attachImageLoader(R.id.video_item_cover)
        holder.attachOnClickListener(R.id.video_item_cover)
        binding.videoItemTime.text = timeParse(data.duration.toLong())
    }

    override fun getLayout(): Int {
        return R.layout.video_local_item
    }

    private fun timeParse(duration: Long): String {
        var time = ""
        val minute = duration / 60000
        val seconds = duration % 60000
        val second = (seconds.toFloat() / 1000).roundToInt().toLong()
        if (minute < 10) {
            time += "0"
        }
        time += "$minute:"
        if (second < 10) {
            time += "0"
        }
        time += second
        return time
    }

}