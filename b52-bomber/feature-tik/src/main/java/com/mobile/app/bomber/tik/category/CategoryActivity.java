package com.mobile.app.bomber.tik.category;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

        Fragment fragment = CategoryFragment.newInstance(0);
        getSupportFragmentManager().beginTransaction()
                .disallowAddToBackStack()
                .add(R.id.fragment_container_view, fragment, fragment.getClass().getSimpleName())
                .commit();


        binding.imgBack.setOnClickListener(v -> finish());
    }

}