package com.mobile.app.bomber.tik.category;

import android.os.Bundle;
import android.view.View;

import com.mobile.app.bomber.common.base.MyBaseActivity;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.ActivityCategroyFragBinding;

import org.jetbrains.annotations.Nullable;

public class CategoryActivity extends MyBaseActivity {

    private ActivityCategroyFragBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategroyFragBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        addFragment(
                R.id.fragment_container_view,
                CategoryFragment.newInstance(0));
 

        binding.backRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}