package com.nupt.shrimp.schoolmarket;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

public class TestActivity extends Activity {

    private final String TAG = "TakePhoto";
    private Button openbButton;
    private Button takePhoto2; // 拍照2
    private Button pickButton;
    private ImageView imageView;
    private String mPictureFile;
    private String filePath;

    private final int OPEN_RESULT = 1; // 打开相机
    private final int PICK_RESULT = 2; // 查看相册
    private final int OPEN_RESULT2 = 3; // 打开相机2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Log.i(TAG, "onCreate");
        openbButton = (Button) findViewById(R.id.btnTakePhoto);
        takePhoto2 = (Button) findViewById(R.id.btnTakePhoto2);
        pickButton = (Button) findViewById(R.id.btnPick);
        imageView = (ImageView) findViewById(R.id.imgPotho);
        openbButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 使用意图直接调用安装在手机上的照相机
                Intent intent = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                // 打开照相机,设置请求码
                startActivityForResult(intent, OPEN_RESULT);
            }
        });

        takePhoto2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 调用系统相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                // 取当前时间为照片名
                mPictureFile = DateFormat.format("yyyyMMdd_hhmmss",
                        Calendar.getInstance(Locale.CHINA))
                        + ".jpg";
                Log.d("onactivity", "mPictureFile：" + mPictureFile);
                filePath = getPhotoPath() + mPictureFile;
                // 通过文件创建一个uri中
                Uri imageUri = Uri.fromFile(new File(filePath));
                // 保存uri对应的照片于指定路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, OPEN_RESULT2);
            }
        });

        pickButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 使用意图直接调用手机相册
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // 打开手机相册,设置请求码
                startActivityForResult(intent, PICK_RESULT);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("filePath", filePath);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (TextUtils.isEmpty(filePath)) {
            filePath = savedInstanceState.getString("filePath");
        }
        Log.d(TAG, "onRestoreInstanceState");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OPEN_RESULT) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                imageView.setImageBitmap(bitmap);
            }
        } else if (requestCode == PICK_RESULT) {
            // 表示选择图片库的图片结果
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                imageView.setImageURI(uri);
            }
        } else if (requestCode == OPEN_RESULT2) {
            if (resultCode == RESULT_OK) {
                Log.e("takePhoto", filePath);
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                // imageView.setImageURI(Uri.fromFile(new File(filePath)));
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 获得照片路径
     *
     * @return
     */
    private String getPhotoPath() {
        return Environment.getExternalStorageDirectory() + "/DCIM/";
    }
}
