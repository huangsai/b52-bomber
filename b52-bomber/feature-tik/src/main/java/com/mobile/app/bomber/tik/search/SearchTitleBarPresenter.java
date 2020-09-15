package com.mobile.app.bomber.tik.search;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.tik.databinding.LayoutSearchToolbarBinding;

/**
 * 处理搜索输入框逻辑
 */
public class SearchTitleBarPresenter implements View.OnClickListener, TextWatcher {

    private final Callback callback;
    private final LayoutSearchToolbarBinding binding;

    public SearchTitleBarPresenter(LayoutSearchToolbarBinding binding, Callback callback) {
        this.binding = binding;
        this.callback = callback;
        init();
    }

    private void init() {
        binding.etSearch.requestFocus();
        binding.searchBack.setOnClickListener(this);
        binding.searchDone.setOnClickListener(this);
        binding.ivClear.setOnClickListener(this);
        binding.etSearch.addTextChangedListener(this);
    }

    /**
     * 选中搜索关键字
     *
     * @param keyword 搜索关键字
     */
    public void selectKeyword(String keyword) {
        binding.etSearch.setText(keyword);
        binding.etSearch.setSelection(keyword.length());
        if (callback != null) callback.doSearch(keyword);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.search_back) {
            if (callback != null) callback.onBack();
        } else if (id == R.id.search_done) {
            String keywordDone = binding.etSearch.getText().toString().trim();
            if (TextUtils.isEmpty(keywordDone)) {
                Msg.INSTANCE.toast("请输入内容");
                return;
            }
            if (callback != null) callback.doSearch(keywordDone);
        } else if (id == R.id.iv_clear) {
            String keywordClear = binding.etSearch.getText().toString().trim();
            if (!TextUtils.isEmpty(keywordClear)) {
                binding.etSearch.setText(null);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String keyword = binding.etSearch.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            binding.ivClear.setVisibility(View.INVISIBLE);
            binding.searchDone.setVisibility(View.INVISIBLE);
        } else {
            binding.ivClear.setVisibility(View.VISIBLE);
            binding.searchDone.setVisibility(View.VISIBLE);
        }
    }

    interface Callback {

        void doSearch(String keyword);

        void onBack();
    }

}
