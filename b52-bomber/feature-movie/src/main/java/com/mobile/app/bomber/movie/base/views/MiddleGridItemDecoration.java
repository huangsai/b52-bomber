package com.mobile.app.bomber.movie.base.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.guava.android.context.ContextExtKt;

public class MiddleGridItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;
    private int mDividerHeight;

    public MiddleGridItemDecoration(Context context, @DimenRes int inserts) {
        mDivider = new ColorDrawable(Color.TRANSPARENT);
        mDividerHeight = context.getResources().getDimensionPixelSize(inserts);
    }

    public MiddleGridItemDecoration(Context context, @DimenRes int inserts, @ColorRes int color) {
        mDivider = new ColorDrawable(ContextExtKt.toColor(context, color));
        mDividerHeight = context.getResources().getDimensionPixelSize(inserts);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    private int getSpanCount(RecyclerView parent) { // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin + mDivider.getIntrinsicWidth();
            final int top = child.getBottom() + params.bottomMargin;
            int bottom = 0;
            if (childCount > getSpanCount(parent) && isLastRow(i, parent)) {//是最后一行
                bottom = top;
            } else {
                bottom = top + mDivider.getIntrinsicHeight();
            }
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicWidth();
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    /**
     * /* 是否是最后一行
     */

    private boolean isLastRow(int itemPosition, RecyclerView parent) {
        int spanCount = getSpanCount(parent);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();//有多少列
        if (layoutManager instanceof GridLayoutManager) {
            int childCount = parent.getAdapter().getItemCount();
            double count = Math.ceil((double) childCount / (double) spanCount);//总行数
            double currentCount = Math.ceil((double) (itemPosition + 1) / spanCount);//当前行数
            // 最后当前数量小于总的
            if (currentCount < count) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否是最后一列
     *
     * @param itemPosition
     * @param parent
     * @return
     */
    private boolean isLastColum(int itemPosition, RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager(); //有多少列
        if (layoutManager instanceof GridLayoutManager) {
            int spanCount = getSpanCount(parent);
            if ((itemPosition + 1) % spanCount == 0) {//因为是从0可以所以要将ItemPosition先加1
                return true;
            }
        }
        return false;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (isLastRow(parent.getChildLayoutPosition(view), parent)) {// 如果是最后一行，则不需要绘制底部
            if (isLastColum(parent.getChildLayoutPosition(view), parent)) {// 如果是最后一列，则不需要绘制右边
                outRect.set(0, 0, 0, 0);
            } else {
                outRect.set(0, 0, mDividerHeight, 0);
            }
        } else {//不是最后一行
            if (isLastColum(parent.getChildLayoutPosition(view), parent)) {// 如果是最后一列，则不需要绘制右边
                outRect.set(0, 0, 0, mDividerHeight);
            } else {//不是最后一列
                outRect.set(0, 0, mDividerHeight, mDividerHeight);
            }
        }
    }
}
