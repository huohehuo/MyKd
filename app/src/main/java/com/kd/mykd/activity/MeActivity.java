package com.kd.mykd.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kd.mykd.Easyrecycle.ViewPagerAdapter;
import com.kd.mykd.R;
import com.kd.mykd.Util.SelectPhotoDialog;
import com.kd.mykd.Util.SelectPicUtil;
import com.kd.mykd.Util.URL;
import com.kd.mykd.databinding.ActivityMeBinding;
import com.kd.mykd.fragment.FriendFragment;
import com.kd.mykd.fragment.MsgFragment;
import com.kd.mykd.listener.OnPageSelected;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.kd.mykd.Util.URL.AA;

public class MeActivity extends AppCompatActivity {
    public static final int REQUEST_CHANGE_AVATAR = 0;
    private static final int REQUEST_SELECT_PICTURE = 0x01;
    public static final int FROM_CAREMA = 1315;
    private ActivityMeBinding binding;
    List<Fragment> fragments;
    View oldView;
    private SelectPhotoDialog selectPhotoDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_me);
        binding=DataBindingUtil.setContentView(MeActivity.this, R.layout.activity_me);

        oldView = binding.layoutDown.btnFriends;
        oldView.setSelected(true);
        selectPhotoDialog = new SelectPhotoDialog(this, R.style.MyDialog);


        binding.layoutDown.nestedscrollviewMe.setFillViewport(true);//目的是让viewpager显示，不冲突
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_me);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //设置toolbar的返回箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        initViewPager();
        initListener();
    }



    private void initListener() {
        binding.layoutDown.btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTab(binding.layoutDown.btnFriends);
                binding.layoutDown.viewPager.setCurrentItem(0);
            }
        });
        binding.layoutDown.btnChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTab(binding.layoutDown.btnChats);
                binding.layoutDown.viewPager.setCurrentItem(1);
            }
        });
        binding.layoutDown.viewPager.addOnPageChangeListener(pageChangeListener);
        binding.layoutHead.ivMeHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectPicUtil.showSelectPhotoUCrop(MeActivity.this, selectPhotoDialog);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        Bitmap bitmap1 = BitmapFactory.decodeFile(AA);
        if (bitmap1 != null) {
            binding.layoutHead.ivMeHeader.setImageBitmap(bitmap1);
        }
        selectPhotoDialog.dismiss();
    }
    private void initViewPager() {
        fragments = new ArrayList<>();
        fragments.add(new FriendFragment());
        fragments.add(new MsgFragment());
        binding.layoutDown.viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), fragments));
    }

    private void changeTab(View v) {
        oldView.setSelected(false);
        oldView = v;
        oldView.setSelected(true);
    }

    OnPageSelected pageChangeListener = new OnPageSelected() {

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    changeTab(binding.layoutDown.btnFriends);
                    break;
                case 1:
                    changeTab(binding.layoutDown.btnChats);
                    break;
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case FROM_CAREMA:
                    Uri uri = Uri.fromFile(new File(AA));
                    startCropActivity(uri);
                    break;
                case REQUEST_SELECT_PICTURE:
                    final Uri selectedUri = data.getData();
                    if (selectedUri != null) {
                        startCropActivity(data.getData());
                        Bitmap bitmap2 = BitmapFactory.decodeFile(URL.AA);
                        binding.layoutHead.ivMeHeader.setImageBitmap(bitmap2);
                    } else {
                        //Toast.makeText(App.getContext(), "相册错误。。", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case UCrop.REQUEST_CROP:

                    Bitmap bitmap = BitmapFactory.decodeFile(AA);
                    binding.layoutHead.ivMeHeader.setImageBitmap(bitmap);
                    break;
                case UCrop.RESULT_ERROR:
                    Log.e("cancle","取消");
            }
        }

    }
    //启动系统图片获取页面
    public static void pickFromGallery(Activity fragment) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        fragment.startActivityForResult(intent,REQUEST_SELECT_PICTURE);
        //会启动选择相册还是文件
        //startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), REQUEST_SELECT_PICTURE);
    }
    public static void openCamera(Activity fragment) {
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        intent.addCategory("android.intent.category.DEFAULT");
        File file = new File(AA);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri uri = Uri.fromFile(file);//将文件路径转化为Uri
        //这里指定存储位置，但是onActivityResult中返回的data很有可能为空，所以在那里不做判断，而是直接获取保存的这个路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        fragment.startActivityForResult(intent, FROM_CAREMA);
    }


    private void startCropActivity(@NonNull Uri uri) {
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(AA)));
        uCrop.withAspectRatio(1, 1);
        UCrop.Options options = new UCrop.Options();
        options.setHideBottomControls(true);
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        uCrop.withOptions(options);
        uCrop.start(MeActivity.this);
    }
}
