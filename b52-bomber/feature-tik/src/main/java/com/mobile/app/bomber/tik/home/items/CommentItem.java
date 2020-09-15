package com.mobile.app.bomber.tik.home.items;

import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.util.Preconditions;
import com.mobile.app.bomber.data.http.entities.ApiComment;
import com.mobile.app.bomber.data.repository.MappersKt;
import com.mobile.guava.android.mvvm.AndroidX;
import com.mobile.app.bomber.runner.base.PrefsManager;
import com.mobile.guava.android.ui.view.text.MySpannable;
import com.mobile.guava.jvm.date.Java8TimeKt;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.ItemCommentABinding;
import com.mobile.app.bomber.tik.databinding.ItemCommentBBinding;
import com.mobile.app.bomber.tik.databinding.ItemCommentCBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class CommentItem extends SimpleRecyclerItem {

    public static final int SLOT_COUNT = 3;

    @NonNull
    public final ApiComment.Comment data;
    public final int type;
    public final boolean isMe;
    protected MySpannable comment;

    public CommentItem(@NonNull ApiComment.Comment data, int type) {
        this.type = type;
        this.data = data;
        if (type == 3) {
            this.isMe = false;
        } else {
            this.isMe = PrefsManager.INSTANCE.getUserId() == data.getUid();
        }
    }

    protected MySpannable buildComment() {
        if (comment == null) {
            int atColor = ContextCompat.getColor(AndroidX.INSTANCE.myApp(), R.color.comment_at);
            int color = ContextCompat.getColor(AndroidX.INSTANCE.myApp(), R.color.comment_name);
            String replay = data.getReplayText();
            String ago = Java8TimeKt.ago(data.getTime(), System.currentTimeMillis());
            comment = new MySpannable(replay + data.getContent() + "\u3000" + ago)
                    .findAndSpan(replay, () -> new ForegroundColorSpan(atColor))
                    .findAndSpan(ago, () -> new ForegroundColorSpan(color));
            data.getAtTexts().stream().forEach(it -> comment.findAndSpan(
                    it,
                    () -> new ForegroundColorSpan(atColor))
            );
        }
        return comment;
    }

    public static class TypeA extends CommentItem {

        public TypeA(@NonNull ApiComment.Comment data) {
            super(data, 1);
        }

        @Override
        public void bind(@NotNull AdapterViewHolder holder) {
            ItemCommentABinding binding = holder.binding(ItemCommentABinding::bind);
            binding.txtName.setText(data.getUsername());
            if (isMe) {
                binding.txtLabel.setVisibility(View.VISIBLE);
            } else {
                binding.txtLabel.setVisibility(View.INVISIBLE);
            }
            binding.txtComment.setText(buildComment());
            binding.txtLike.setSelected(data.isLiking());
            binding.txtLike.setText(String.valueOf(data.getLikeCount()));
            holder.attachImageLoader(R.id.img_profile);
            holder.attachOnClickListener(R.id.txt_like);
            holder.attachOnClickListener(R.id.item_comment_a);
            holder.attachOnLongClickListener(R.id.item_comment_a);
        }

        @Override
        public void bindPayloads(@NotNull AdapterViewHolder holder, @Nullable List<?> payloads) {
            ItemCommentABinding binding = holder.binding(ItemCommentABinding::bind);
            binding.txtLike.setSelected(data.isLiking());
            binding.txtLike.setText(String.valueOf(data.getLikeCount()));
        }

        @Override
        public int getLayout() {
            return R.layout.item_comment_a;
        }
    }

    public static class TypeB extends CommentItem {

        public TypeB(@NonNull ApiComment.Comment data) {
            super(data, 2);
        }

        @Override
        public void bind(@NotNull AdapterViewHolder holder) {
            ItemCommentBBinding binding = holder.binding(ItemCommentBBinding::bind);
            binding.txtName.setText(data.getUsername());
            if (isMe) {
                binding.txtLabel.setVisibility(View.VISIBLE);
            } else {
                binding.txtLabel.setVisibility(View.INVISIBLE);
            }
            binding.txtComment.setText(buildComment());
            binding.txtLike.setSelected(data.isLiking());
            binding.txtLike.setText(String.valueOf(data.getLikeCount()));
            holder.attachImageLoader(R.id.img_profile);
            holder.attachOnClickListener(R.id.txt_like);
            holder.attachOnClickListener(R.id.item_comment_b);
            holder.attachOnLongClickListener(R.id.item_comment_b);
        }

        @Override
        public void bindPayloads(@NotNull AdapterViewHolder holder, @Nullable List<?> payloads) {
            ItemCommentBBinding binding = holder.binding(ItemCommentBBinding::bind);
            binding.txtLike.setSelected(data.isLiking());
            binding.txtLike.setText(String.valueOf(data.getLikeCount()));
        }

        @Override
        public int getLayout() {
            return R.layout.item_comment_b;
        }
    }

    public static class TypeC extends CommentItem {

        private final List<ApiComment.Comment> list;
        private int hidingCount;
        private int showingCount;
        private int lastShowingPager;

        public TypeC(@NonNull ApiComment.Comment data) {
            super(data, 3);
            list = data.getChildren().subList(SLOT_COUNT, data.getChildren().size());
            Preconditions.checkArgument(list.size() > 0, "");
            hidingCount = list.size();
            showingCount = 0;
            lastShowingPager = 0;
        }

        @Override
        public void bind(@NotNull AdapterViewHolder holder) {
            ItemCommentCBinding binding = holder.binding(ItemCommentCBinding::bind);
            setComment(binding);
            holder.attachOnClickListener(R.id.item_comment_c);
        }

        @Override
        public void bindPayloads(@NotNull AdapterViewHolder holder, @Nullable List<?> payloads) {
            ItemCommentCBinding binding = holder.binding(ItemCommentCBinding::bind);
            setComment(binding);
        }

        @Override
        public int getLayout() {
            return R.layout.item_comment_c;
        }

        private void setComment(ItemCommentCBinding binding) {
            String comment;
            if (list.size() == hidingCount) {
                comment = "展开" + list.size() + "条回复";
            } else if (hidingCount == 0) {
                comment = "收起";
            } else {
                comment = "展开更多回复";
            }
            binding.txtComment.setText(comment);
        }

        public List<ApiComment.Comment> next() {
            List<ApiComment.Comment> next = MappersKt.forPage(list, lastShowingPager + 1, SLOT_COUNT);
            if (!next.isEmpty()) {
                lastShowingPager++;
                showingCount = showingCount + next.size();
                hidingCount = hidingCount - next.size();
            }
            return next;
        }

        public void reset() {
            hidingCount = list.size();
            showingCount = 0;
            lastShowingPager = 0;
        }

        public int size() {
            return list.size();
        }
    }


    public static class TypeD extends SimpleRecyclerItem {

        public TypeD() {
            super();
        }

        @Override
        public void bind(@NotNull AdapterViewHolder adapterViewHolder) {
        }

        @Override
        public int getLayout() {
            return R.layout.item_comment_d;
        }
    }
}
