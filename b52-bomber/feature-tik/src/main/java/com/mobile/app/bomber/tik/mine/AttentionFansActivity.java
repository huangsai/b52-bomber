package com.mobile.app.bomber.tik.mine;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mobile.guava.android.mvvm.RouterKt;
import com.mobile.app.bomber.data.http.entities.ApiUserCount;
import com.mobile.guava.data.Values;

import com.mobile.app.bomber.common.base.MyBaseActivity;
import com.mobile.app.bomber.tik.databinding.ActivityAttentionFansBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AttentionFansActivity extends MyBaseActivity {

    public static final int TYPE_FOLLOW = 0;
    public static final int TYPE_FANS = 1;

    private ActivityAttentionFansBinding binding;
    private List<String> indexTitle = new ArrayList<>();
    private TabLayoutMediator tabLayoutMediator;

    public static void start(Activity activity, int type, long userId) {
        Values.INSTANCE.put("AttentionFansActivity_type", type);
        Values.INSTANCE.put("AttentionFansActivity_userId", userId);
        RouterKt.newStartActivity(activity, AttentionFansActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttentionFansBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int type = Values.INSTANCE.take("AttentionFansActivity_type");
        long userId = Values.INSTANCE.take("AttentionFansActivity_userId");

        indexTitle.add("关注0");
        indexTitle.add("粉丝0");

        binding.comBack.setOnClickListener(v -> finish());
        MyAdapter adapter = new MyAdapter(this, userId);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.viewPager.setCurrentItem(type, false);
        tabLayoutMediator = new TabLayoutMediator(binding.layoutTab, binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(indexTitle.get(position));
            }
        });
        tabLayoutMediator.attach();
    }

    public void setTabTitle(ApiUserCount apiUserCount) {
        binding.layoutTab.getTabAt(0).setText("关注" + apiUserCount.getFollowCount());
        binding.layoutTab.getTabAt(1).setText("粉丝" + apiUserCount.getFansCount());
    }

    private static class MyAdapter extends FragmentStateAdapter {

        private final long mUserId;

        public MyAdapter(AttentionFansActivity activity, long userId) {
            super(activity);
            this.mUserId = userId;
        }

        @NotNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0://关注
                    fragment = AttentionFansFragment.newInstance(TYPE_FOLLOW, mUserId);
                    break;
                case 1://粉丝
                    fragment = AttentionFansFragment.newInstance(TYPE_FANS, mUserId);
                    break;
            }
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

}
