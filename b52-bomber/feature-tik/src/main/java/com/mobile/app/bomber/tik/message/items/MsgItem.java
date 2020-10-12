package com.mobile.app.bomber.tik.message.items;

import android.view.View;

import androidx.annotation.NonNull;

import com.mobile.app.bomber.data.http.entities.ApiUsermsg;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.ItemMsgBinding;
import com.mobile.app.bomber.tik.mine.UserDetailActivity;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MsgItem extends SimpleRecyclerItem {

    @NonNull
    public final ApiUsermsg.Item data;

    public MsgItem(@NonNull ApiUsermsg.Item data) {
        this.data = data;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        ItemMsgBinding binding = holder.binding(ItemMsgBinding::bind);
        // 绑定图片加载，逻辑在MsgFragment.load(...)方法
        // MsgFragment.onCreate(...)方法设置了图片加载
        // MsgFragment实现AdapterImageLoader接口
//        binding.txtTitle.setText(data.getN);
//        //binding.imgProfile.setImageResource(R.drawable.default_profile);
//        binding.txtContent.setText("动漫合计，不容错过");
//        binding.txtTime.setText("2020-08-28");

        binding.txtTitle.setText((data.getFromuserinfo().get(0).getName()));
        switch (data.getMsgtype()) {
            case 1: {
                binding.txtContent.setText("关注了你");
                break;
            }
            case 2: {
                binding.txtContent.setText("赞了你的视频");
                break;
            }
            case 3: {
                binding.txtContent.setText("在评论中提到了你");
                break;
            }
            case 4: {
                binding.txtContent.setText("评论了你的作品");
                break;
            }
        }
        //binding.txtContent.setText((data.getContent()));

        String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date(data.getCreatetime() * 1000L));
        binding.txtTime.setText(time);


        holder.attachImageLoader(R.id.img_profile);
        holder.attachOnClickListener(R.id.item_msg);
    }

    @Override
    public int getLayout() {
        return R.layout.item_msg;
    }
}
