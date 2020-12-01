package com.mobile.app.bomber.tik.home

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.linkedin.android.spyglass.mentions.Mentionable
import com.linkedin.android.spyglass.tokenization.QueryToken
import com.linkedin.android.spyglass.tokenization.interfaces.QueryTokenReceiver
import com.linkedin.android.spyglass.ui.MentionsEditText
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.data.http.entities.ApiComment
import com.mobile.app.bomber.data.http.entities.ApiVideo
import com.mobile.app.bomber.runner.base.PrefsManager
import com.mobile.app.bomber.tik.R
import com.mobile.app.bomber.tik.base.AppRouterUtils
import com.mobile.app.bomber.tik.base.loadProfile
import com.mobile.app.bomber.tik.base.views.MyKeyboardHelper
import com.mobile.app.bomber.tik.databinding.FragmentCommentInputDialogBinding
import com.mobile.app.bomber.tik.home.items.AtUserItem
import com.mobile.app.bomber.tik.home.items.MyAtUser
import com.mobile.ext.rxjava3.binding.RxView
import com.mobile.guava.android.ime.ImeUtils
import com.mobile.guava.android.mvvm.BaseBottomSheetDialogFragment
import com.mobile.guava.android.ui.view.expandable.ExpandableLayout2
import com.mobile.guava.android.ui.view.text.backspace
import com.mobile.guava.android.ui.view.text.moveCursorToLast
import com.mobile.guava.data.Values
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import com.pacific.adapter.AdapterImageLoader
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class CommentInputDialogFragment : BaseBottomSheetDialogFragment(), View.OnClickListener,
        AdapterImageLoader, QueryTokenReceiver, MentionsEditText.MentionWatcher,
        ExpandableLayout2.OnExpansionUpdateListener {

    private var _binding: FragmentCommentInputDialogBinding? = null
    private val binding get() = _binding!!
    private val atUsers = ArrayList<MyAtUser>()

    private lateinit var behavior: BottomSheetBehavior<FrameLayout>
    private lateinit var video: ApiVideo.Video
    private var action: Int = 0
    private var atd: Long = 0
    private var vid: Long = 0

    private val adapter = RecyclerAdapter()
    private var comment: ApiComment.Comment? = null
    private var textDisposable: Disposable? = null
    private var ignoredTextChanged = false
    private val aboutAtUserItems = ArrayList<AtUserItem>()

    private val model: HomeViewModel by viewModels { AppRouterUtils.viewModelFactory() }

    private val globalLayoutListener = object : OnGlobalLayoutListener {
        private var mWindowHeight = 0
        private var mSoftKeyboardHeight = 0
        private var mIsKeyboardVisible = false

        override fun onGlobalLayout() {
            val outRect = Rect()
            dialog!!.window!!.decorView.getWindowVisibleDisplayFrame(outRect)
            val height: Int = outRect.height()
            if (mWindowHeight == 0) {
                mWindowHeight = height
            } else {
                if (mWindowHeight != height) {
                    mSoftKeyboardHeight = mWindowHeight - height
                }
                if (mIsKeyboardVisible != MyKeyboardHelper.isKeyboardVisible(dialog)) {
                    mIsKeyboardVisible = !mIsKeyboardVisible
                    onKeyboardVisibility(mIsKeyboardVisible, mSoftKeyboardHeight)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.NoDimBottomSheetDialog)
        action = Values.take("CommentInputDialogFragment_action")!!
        video = Values.take("CommentInputDialogFragment_video")!!
        comment = Values.take("CommentInputDialogFragment_comment")
        adapter.onClickListener = this
        adapter.imageLoader = this
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommentInputDialogBinding.inflate(inflater, container, false)
        behavior = (dialog as BottomSheetDialog).behavior
        behavior.peekHeight = 2312
        binding.recycler.layoutManager = LinearLayoutManager(requireActivity())
        binding.recycler.adapter = adapter
        binding.imgAt.setOnClickListener(this)
        binding.imgEmoji.setOnClickListener(this)
        binding.txtSend.setOnClickListener(this)
        binding.layoutAt.setOnExpansionUpdateListener(this)
        binding.layoutEmoji.visibility = View.GONE
        dialog?.window?.decorView?.viewTreeObserver?.addOnGlobalLayoutListener(globalLayoutListener)
        applyEditorAfterTextChanged()
        applyAction()
        loadAboutAtUserItems()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textDisposable?.dispose()
        binding.recycler.adapter = null
        dialog?.window?.decorView?.viewTreeObserver?.removeOnGlobalLayoutListener(globalLayoutListener)
    }

    private fun applyEditorAfterTextChanged() {
        binding.editComment.addMentionWatcher(this)
        textDisposable = RxView.afterTextChangeEvents(binding.editComment)
                .filter { !ignoredTextChanged }
                .doOnNext {
                    val str = it.editable.toString()
                    if (str.endsWith("@")) {
                        binding.layoutAt.expand()
                    }
                }
                .filter {
                    val str = it.editable.toString()
                    if (!str.contains("@") || str.endsWith("@")) {
                        return@filter false
                    }
                    val atIndex = str.lastIndexOf("@")
                    val spaceIndex = str.lastIndexOf(" ")
                    if (atIndex < spaceIndex) {
                        return@filter false
                    }
                    return@filter true

                }
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val str = it.editable.toString()
                    val atIndex = str.lastIndexOf("@")
                    searchUsers(str.substring(atIndex + 1))
                }
    }

    private fun applyAction() {
        binding.editComment.hint = getString(R.string.comment_hint)
        when (action) {
            1 -> showIme()
            2 -> {
                showIme()
                binding.editComment.append("@")
            }
            3 -> binding.layoutEmoji.expand(false)
            4 -> {
                showIme()
                binding.editComment.hint = "回复 @" + comment?.username + "："
            }
            else -> throw IllegalStateException()
        }
    }

    private fun loadAboutAtUserItems() {
        model.aboutUsers.map { AtUserItem(it) }.also {
            aboutAtUserItems.addAll(it)
            // adapter.replaceAll(aboutAtUserItems)
        }
    }

    override fun onMentionDeleted(mention: Mentionable, text: String, start: Int, end: Int) {
        atUsers.remove(mention as MyAtUser)
    }

    override fun onMentionPartiallyDeleted(mention: Mentionable, text: String, start: Int, end: Int) {}

    override fun onMentionAdded(mention: Mentionable, text: String, start: Int, end: Int) {
        atUsers.add(mention as MyAtUser)
    }

    override fun onQueryReceived(queryToken: QueryToken): MutableList<String> {
        return Collections.singletonList("@")
    }

    @SingleClick
    override fun onClick(v: View) {
        when (v.id) {
            R.id.img_close -> dismissAllowingStateLoss()
            R.id.img_at -> {
                if (!binding.editComment.text.toString().endsWith("@")) {
                    binding.editComment.append("@")
                }
            }
            R.id.img_emoji -> {
                if (MyKeyboardHelper.isKeyboardVisible(dialog)) {
                    if (!binding.layoutEmoji.isExpanded) {
                        binding.layoutEmoji.expand(false)
                        binding.root.setPadding(0, 0, 0, 0)
                    }
                    ImeUtils.hideIme(binding.editComment)
                } else {
                    if (binding.layoutEmoji.isExpanded) {
                        binding.layoutEmoji.collapse()
                    } else {
                        binding.layoutEmoji.expand()
                    }
                }
            }
            R.id.item_at_user -> {
                val holder = AdapterUtils.getHolder(v)
                val item = holder.item<AtUserItem>()
                selectAtUserItem(item)
            }
            R.id.txt_send -> createComment()
        }
    }

    private fun selectAtUserItem(item: AtUserItem) {
        val user = MyAtUser.create(item.data)
        if (!atUsers.contains(user)) {
            ignoredTextChanged = true
            val str = binding.editComment.text.toString()
            val maxIndex = binding.editComment.selectionStart - 1
            var count = 1
            for (i in maxIndex downTo 0) {
                if (str[i] == '@') {
                    break
                } else {
                    count++
                }
            }
            binding.editComment.backspace(count)
            binding.editComment.insertMentionWithoutToken(user)
            binding.editComment.moveCursorToLast()
            ignoredTextChanged = false
        }
        binding.layoutAt.collapse()
    }

    private fun buildContent(): String {
        val content = binding.editComment.text.toString()
        binding.editComment.setText("")
        return content
    }

    override fun load(imageView: ImageView, holder: AdapterViewHolder) {
        val item: AtUserItem = holder.item()
        this.loadProfile(item.data.profile, imageView)
    }

    private fun showIme() {
        ImeUtils.showIme(
                binding.editComment, dialog!!,
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }

    private fun onKeyboardVisibility(isKeyboardVisible: Boolean, softKeyboardHeight: Int) {
        if (isKeyboardVisible) {
            PrefsManager.setSoftKeyboardHeight(softKeyboardHeight)
            if (softKeyboardHeight != binding.layoutEmoji.layoutParams.height) {
                binding.layoutEmoji.layoutParams.height = softKeyboardHeight
                binding.layoutEmoji.requestLayout()
            }
            if (!binding.layoutEmoji.isExpanded) {
                binding.root.setPadding(0, 0, 0, softKeyboardHeight)
            }
        } else {
            binding.root.setPadding(0, 0, 0, 0)
            binding.layoutAt.collapse()
            if (binding.editComment.text.toString().endsWith("@")) {
                val len = binding.editComment.text.length
                binding.editComment.text.delete(len - 1, len)
            }
        }
    }

    private fun createComment() {
        if (video.isChecking()) {
            Msg.toast("视频还未审核不能发评论，请等待审核通过")
            return
        }
        val content = buildContent()
        if (content.isNullOrEmpty()) {
            Msg.toast("评论内容不能为空")
            return
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val toCommendId = comment?.id ?: 0L
            val toUserId = comment?.uid ?: 0L

            val at = Gson().toJson(atUsers.map { it.toAtuids() })
            val type = if (video.adId == atd) {
                0
            } else {
                1
            }
            val atList = atUsers.map { it.toAt() }
            val newComment = ApiComment.Comment(
                    PrefsManager.getUserId(),
                    if (type == 0) video.videoId else video.adId!!,
                    0L,
                    0,
                    (System.currentTimeMillis()) / 1000L - 1,
                    PrefsManager.getLoginName(),
                    PrefsManager.getHeadPicUrl(),
                    content,
                    toCommendId,
                    toUserId,
                    comment?.username ?: "",
                    comment?.pic ?: "",
                    false,
                    atList,
                    null
            )
            val source = model.createComment(
                    if (type == 0) video.videoId else video.adId!!,
                    content,
                    toCommendId,
                    toUserId,
                    at,
                    type.toLong()
            )
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        newComment.id = source.requireData().id
                        (parentFragment as CommentDialogFragment).onCreateComment(newComment)
                        Msg.toast("评论成功")
                        dismissAllowingStateLoss()
                    }
                    is Source.Error -> {
                        Msg.toast("评论失败")
                    }
                }.exhaustive
            }
        }
    }

    override fun onExpansionUpdate(expansionFraction: Float, state: Int) {
        when (state) {
            ExpandableLayout2.State.COLLAPSED -> {
                adapter.replaceAll(aboutAtUserItems)
            }
        }
    }

    private fun searchUsers(keyword: String) {
        binding.progress.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            val users = atUsers.filter { keyword.contains(it.username) }
            if (users.isNotEmpty()) return@launch

            val source = model.searchUsers(keyword)
            val list = source.dataOrNull().orEmpty().map { AtUserItem(it) }
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        adapter.replaceAll(list)
                    }
                    is Source.Error -> {
                        Msg.handleSourceException(source.requireError())
                    }
                }.exhaustive
                binding.progress.visibility = View.GONE
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(): CommentInputDialogFragment = CommentInputDialogFragment()
    }
}