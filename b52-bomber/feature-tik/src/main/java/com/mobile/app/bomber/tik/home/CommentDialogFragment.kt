package com.mobile.app.bomber.tik.home

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.result.ActivityResultCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.tool.ClipBoardUtil
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.data.http.entities.ApiComment
import com.mobile.app.bomber.data.http.entities.ApiVideo
import com.mobile.app.bomber.runner.RunnerX
import com.mobile.app.bomber.tik.R
import com.mobile.app.bomber.tik.base.AppRouterUtils
import com.mobile.app.bomber.tik.base.loadProfile
import com.mobile.app.bomber.tik.base.requireLogin
import com.mobile.app.bomber.tik.databinding.DialogCommentActionsBinding
import com.mobile.app.bomber.tik.databinding.FragmentCommentDialogBinding
import com.mobile.app.bomber.tik.home.items.CommentItem
import com.mobile.guava.android.mvvm.BaseBottomSheetDialogFragment
import com.mobile.guava.android.mvvm.showDialogFragment
import com.mobile.guava.android.ui.view.recyclerview.keepItemViewVisible
import com.mobile.guava.android.ui.view.text.disableEditable
import com.mobile.guava.data.Values
import com.mobile.guava.jvm.coroutines.Bus
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import com.pacific.adapter.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentDialogFragment : BaseBottomSheetDialogFragment(), View.OnClickListener,
        AdapterImageLoader, OnDataSetChanged, View.OnLongClickListener {

    private var _binding: FragmentCommentDialogBinding? = null
    private val binding get() = _binding!!

    private val adapter = RecyclerAdapter()
    private val model: HomeViewModel by viewModels { AppRouterUtils.viewModelFactory() }
    private var atd: Long = 0

    private lateinit var video: ApiVideo.Video
    private lateinit var behavior: BottomSheetBehavior<FrameLayout>
    private var commentCount = 0
    private var commentItem: CommentItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.NoDimBottomSheetDialog)
        video = Values.take("CommentDialogFragment")!!
        adapter.onClickListener = this
        adapter.onLongClickListener = this
        adapter.imageLoader = this
        adapter.onDataSetChanged = this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        _binding = FragmentCommentDialogBinding.bind(View.inflate(
                requireActivity(), R.layout.fragment_comment_dialog, null
        ))
        behavior = dialog.behavior
        behavior.peekHeight = 2312
        dialog.setContentView(binding.root)
        return dialog
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding.recycler.layoutManager = LinearLayoutManager(requireActivity())
        binding.recycler.adapter = adapter
        val divider = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.custom_divider)!!)
        binding.recycler.addItemDecoration(divider)
        binding.editComment.disableEditable()
        binding.editComment.setOnClickListener(this)
        binding.imgAt.setOnClickListener(this)
        binding.imgEmoji.setOnClickListener(this)
        binding.imgClose.setOnClickListener(this)
        loadComments()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun loadComments() {
        val type = if (video.adId == atd) {
            0
        } else {
            1
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val source = model.comments(if (type == 0) video.videoId else video.adId!!,type.toLong())
            val list = ArrayList<SimpleRecyclerItem>()

            var childIndex: Int
            commentCount = 0
            source.dataOrNull().orEmpty().forEach { o ->
                list.add(CommentItem.TypeA(o,video))
                commentCount++
                childIndex = 0
                o.recursiveChildren(o).forEach { oo ->
                    when {
                        childIndex < CommentItem.SLOT_COUNT -> {
                            list.add(CommentItem.TypeB(oo,video))
                            commentCount++
                            childIndex++
                        }
                        childIndex == CommentItem.SLOT_COUNT -> {
                            list.add(CommentItem.TypeC(o,video))
                            childIndex++
                        }
                    }
                }
            }
            list.add(CommentItem.TypeD())
            withContext(Dispatchers.Main) {
                adapter.replaceAll(list)
            }
        }
    }

    override fun apply(count: Int) {
        var value = 0
        adapter.getAll().forEach {
            if (it is CommentItem.TypeA || it is CommentItem.TypeB) {
                value++
            }
        }
        binding.txtTitle.text = ("${value}条评论")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recycler.adapter = null
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Bus.offer(RunnerX.BUS_VIDEO_UPDATE)
    }

    @SingleClick
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.img_close -> dismissAllowingStateLoss()
            R.id.item_comment_c -> {
                val holder = AdapterUtils.getHolder(v)
                val typeC = holder.item<CommentItem.TypeC>()
                val next = typeC.next().map { CommentItem.TypeB(it,video) }
                val ppp = holder.bindingAdapterPosition
                if (next.isEmpty()) {
                    adapter.removeAll(adapter.getAll().subList(ppp - typeC.size(), ppp))
                    typeC.reset()
                } else {
                    adapter.addAll(ppp, next)
                }
                adapter.notifyItemChanged(adapter.indexOf(typeC))
            }
            R.id.txt_like -> {
                requireActivity().requireLogin(ActivityResultCallback {
                    if (it.resultCode == Activity.RESULT_OK) {
                        val item = AdapterUtils.getHolder(v).item<CommentItem>()
                        if (!item.data.isLiking) {
                            YoYo.with(Techniques.ZoomIn).duration(300).playOn(v)
                        }
                        likeComment(AdapterUtils.getHolder(v).item())
                    }
                })
            }
            R.id.edit_comment -> {
                commentItem = null
                showCommentInput(1)
            }
            R.id.img_at -> {
                commentItem = null
                showCommentInput(2)
            }
            R.id.img_emoji -> {
                commentItem = null
                showCommentInput(3)
            }
            R.id.item_comment_a, R.id.item_comment_b -> {
                commentItem = AdapterUtils.getHolder(v).item()
                showCommentInput(4)
            }
        }
    }

    override fun onLongClick(v: View): Boolean {
        val holder = AdapterUtils.getHolder(v)
        val item: CommentItem = holder.item()
        val dialog = AlertDialog.Builder(requireActivity()).create()
        val dialogBinding = DialogCommentActionsBinding.inflate(
                requireActivity().layoutInflater,
                null,
                false
        )
        dialogBinding.txtCopy.setOnClickListener {
            ClipBoardUtil.copy(item.data.realContent)
            dialog.dismiss()
        }
        dialogBinding.txtDelete.setOnClickListener {
            deleteComment(item, holder.bindingAdapterPosition)
            dialog.dismiss()
        }
        if (!item.isMe) {
            dialogBinding.txtDelete.visibility = View.GONE
        }
        dialog.show()
        dialog.setContentView(dialogBinding.root)
        return true
    }

    override fun load(imageView: ImageView, holder: AdapterViewHolder) {
        val item = holder.item<CommentItem>()
        loadProfile(item.data.pic, imageView)
    }

    private fun showCommentInput(action: Int) {
        requireActivity().requireLogin(ActivityResultCallback {
            if (it.resultCode == Activity.RESULT_OK) {
                Values.put("CommentInputDialogFragment_action", action)
                Values.put("CommentInputDialogFragment_video", video)
                commentItem?.let { o ->
                    Values.put("CommentInputDialogFragment_comment", o.data)
                }
                showDialogFragment(CommentInputDialogFragment.newInstance())
            }
        })
    }

    fun onCreateComment(newComment: ApiComment.Comment) {
        if (commentItem == null) {
            if (adapter.itemCount > 0) {
                adapter.add(0, CommentItem.TypeA(newComment,video))
            } else {
                adapter.add(CommentItem.TypeA(newComment, video))
            }
            binding.recycler.keepItemViewVisible(0, true)
            binding.recycler.scrollToPosition(0)
        } else {
            val index = adapter.indexOf(commentItem!!)
            if (adapter.itemCount == index + 1) {
                adapter.add(CommentItem.TypeB(newComment,video))
            } else {
                adapter.add(index + 1, CommentItem.TypeB(newComment,video))
            }
            binding.recycler.keepItemViewVisible(index + 1, true)
        }
    }

    private fun deleteComment(item: CommentItem, position: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val source = model.deleteComment(item.data.id, item.data.videoId)
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        adapter.remove(position)
                    }
                    is Source.Error -> {
                        Msg.handleSourceException(source.requireError())
                    }
                }.exhaustive
            }
        }
    }

    private fun likeComment(item: CommentItem) {
        setLikingState(item)
        val type = if (video.adId == atd) {
            0
        } else {
            1
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val source = model.likeComment(item.data,type.toLong())
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                    }
                    is Source.Error -> {
                        setLikingState(item)
                        Msg.toast("评论失败")
                    }
                }
            }
        }
    }

    private fun setLikingState(item: CommentItem) {
        item.data.also { comment ->
            comment.isLiking = !comment.isLiking
            if (comment.isLiking) {
                comment.likeCount++
            } else {
                comment.likeCount--
            }
        }
        adapter.notifyItemChanged(adapter.indexOf(item), 0)
    }

    companion object {

        @JvmStatic
        fun newInstance(): CommentDialogFragment = CommentDialogFragment()
    }
}