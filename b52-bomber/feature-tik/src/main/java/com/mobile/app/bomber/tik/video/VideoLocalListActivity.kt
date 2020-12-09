package com.mobile.app.bomber.tik.video

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mobile.app.bomber.common.base.MyBaseActivity
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.runner.RunnerX
import com.mobile.app.bomber.tik.R
import com.mobile.app.bomber.tik.databinding.ActivityVideoLocalBinding
import com.mobile.app.bomber.tik.video.items.VideoLocalItem
import com.mobile.ext.glide.GlideApp
import com.mobile.guava.android.ui.view.recyclerview.TestedGridItemDecoration
import com.pacific.adapter.AdapterImageLoader
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class VideoLocalListActivity : MyBaseActivity(), AdapterImageLoader, View.OnClickListener {

    private var _binding: ActivityVideoLocalBinding? = null
    private val binding get() = _binding!!
    private var mAdapter: RecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVideoLocalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.videoLocalBack.setOnClickListener { finish() }
        initRecyclerView()
    }

    override fun onBusEvent(event: Pair<Int, Any>) {
        super.onBusEvent(event)
        if (event.first == RunnerX.BUS_VIDEO_UPLOAD_SUCCESS) {
            finish()
        }
    }

    private fun initRecyclerView() {
        mAdapter = RecyclerAdapter()
        mAdapter?.imageLoader = this
        mAdapter?.onClickListener = this
        val layoutManager = GridLayoutManager(baseContext, 3)
        binding.recyclerView.addItemDecoration(TestedGridItemDecoration(this, R.dimen.size_1dp))
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = mAdapter
        lifecycleScope.launch(Dispatchers.IO) {
            loadVideo()
        }
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        val path = holder.item<VideoLocalItem>().data.path
        GlideApp.with(this)
                .load(path)
                .transition(DrawableTransitionOptions.withCrossFade())
                .thumbnail(0.25f)
                .into(view)
    }

    @SingleClick
    override fun onClick(v: View) {
        if (v.id == R.id.video_item_cover) {
            val path = AdapterUtils.getHolder(v).item<VideoLocalItem>().data.path
            VideoUploadActivity.start(this, path)
        }
    }

    private suspend fun loadVideo() {
        val medias = mutableListOf<MediaData>()
        val cursor = contentResolver.query(QUERY_URI,
                PROJECTION,
                getSelectionArgsForSingleMediaCondition(getDurationCondition(mRecordMaxDuration.toLong(), mRecordMinDuration.toLong())),
                arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()), ORDER_BY)
        while (cursor?.moveToNext() == true) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(PROJECTION[0]))
            val path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val uri = QUERY_URI.buildUpon().appendPath(id.toString()).build().toString()
                val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)
                val pathCursor = contentResolver.query(Uri.parse(uri), filePathColumn, null, null, null)
                pathCursor?.moveToFirst()
                val columnIndex = pathCursor?.getColumnIndex(filePathColumn[0]) ?: 0
                val filePath = pathCursor?.getString(columnIndex) ?: ""
                pathCursor?.close()
                filePath
            } else {
                cursor.getString(cursor.getColumnIndexOrThrow(PROJECTION[1]))
            }
            val pictureType = cursor.getString(cursor.getColumnIndexOrThrow(PROJECTION[2]))
            val width = cursor.getInt(cursor.getColumnIndexOrThrow(PROJECTION[3]))
            val height = cursor.getInt(cursor.getColumnIndexOrThrow(PROJECTION[4]))
            val duration = cursor.getInt(cursor.getColumnIndexOrThrow(PROJECTION[5]))
            val item = MediaData(path, pictureType, width, height)
            item.duration = duration
            medias.add(item)
        }
        cursor?.close()
        withContext(Dispatchers.Main) {
            val items = mutableListOf<VideoLocalItem>()
            medias.forEach {
                items.add(VideoLocalItem(it))
            }
            mAdapter?.replaceAll(items)
        }
    }

    private fun getSelectionArgsForSingleMediaCondition(timeCondition: String): String {
        return (MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                + " AND " + timeCondition)
    }

    private fun getDurationCondition(exMaxLimit: Long, exMinLimit: Long): String {
        val videoMinS = 1000L
        val videoMaxS = 60 * 5 * 1000L
        var maxS = videoMaxS
        if (exMaxLimit != 0L) {
            maxS = maxS.coerceAtMost(exMaxLimit)
        }

        return String.format(Locale.CHINA, "%d <%s duration and duration <= %d",
                exMinLimit.coerceAtLeast(videoMinS),
                if (exMinLimit.coerceAtLeast(videoMinS) == 0L) "" else "=",
                maxS)
    }

    companion object {
        private const val DURATION = "duration"
        private val PROJECTION = arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.MIME_TYPE, MediaStore.MediaColumns.WIDTH, MediaStore.MediaColumns.HEIGHT, DURATION)
        private const val ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC"
        private val QUERY_URI = MediaStore.Files.getContentUri("external")
    }

}