package com.mobile.app.bomber.tik.mine;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mobile.app.bomber.tik.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

public class SpaceImageDetailActivity extends AppCompatActivity
{
    private LinearLayout ll;
    private SmoothImageView imageView;
    private SmoothImageView.TransformListener listener;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_image_detail);
        checkNeedPermissions();
//        mActivity = this;

        ll = findViewById(R.id.ll);
        imageView = findViewById(R.id.simg);

//        mDatas = (ArrayList<String>) getIntent().getSerializableExtra("images");
//        mPosition = getIntent().getIntExtra("position", 0);
        int mLocationX = getIntent().getIntExtra("locationX", 0);
        int mLocationY = getIntent().getIntExtra("locationY", 0);
        int mWidth = getIntent().getIntExtra("width", 0);
        final int mHeight = getIntent().getIntExtra("height", 0);

        imageView = new SmoothImageView(this);
        imageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
        imageView.transformIn();
        imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        setContentView(imageView);
//        File url =  new File(getIntent().getStringExtra("image"));
//        Uri uri = Uri.fromFile(url);
        Log.e("------aaa",getIntent().getStringExtra("image"));
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        ImageLoader.getInstance().displayImage("file://"+getIntent().getStringExtra("image"), imageView);
        listener = new SmoothImageView.TransformListener()
        {
            @Override
            public void onTransformComplete(int mode)
            {
                if (mode == SmoothImageView.STATE_TRANSFORM_OUT)
                {
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        };
        imageView.setOnTransformListener(listener);

        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                Log.i("SpaceImageDetailActivity", "点击了imageView");
                imageView.transformOut();
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        imageView.transformOut();

        super.onBackPressed();

        overridePendingTransition(0, 0);
    }

    private void checkNeedPermissions(){
        //6.0以上需要动态申请权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //多个权限一起申请
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 1);
        }
    }
}
