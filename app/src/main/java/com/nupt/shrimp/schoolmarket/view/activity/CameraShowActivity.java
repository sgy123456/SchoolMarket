//package com.nupt.shrimp.schoolmarket.view.activity;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.provider.MediaStore;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.Gravity;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.nupt.shrimp.schoolmarket.R;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.Map;
//
//public class CameraActivity extends Activity implements OnClickListener {
//    private Context mContext;
//    private CircleImg avatarImg;// 头像图片
//    private Button loginBtn;// 页面的登录按钮
//    private SelectPicPopupWindow menuWindow; // 自定义的头像编辑弹出框
//    // 上传服务器的路径【一般不硬编码到程序中】
//    private String imgUrl = "";
//    private static final String IMAGE_FILE_NAME = "avatarImage.jpg";// 头像文件名称
//    private String urlpath;			// 图片本地路径
//    private String resultStr = "";	// 服务端返回结果集
//    private static ProgressDialog pd;// 等待进度圈
//    private static final int REQUESTCODE_PICK = 0;		// 相册选图标记
//    private static final int REQUESTCODE_TAKE = 1;		// 相机拍照标记
//    private static final int REQUESTCODE_CUTTING = 2;	// 图片裁切标记
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        mContext = MainActivity.this;
//
//        initViews();
//    }
//
//    /**
//     * 初始化页面控件
//     */
//    private void initViews() {
//        avatarImg = (CircleImg) findViewById(R.id.avatarImg);
//        loginBtn = (Button) findViewById(R.id.loginBtn);
//
//        avatarImg.setOnClickListener(this);
//        loginBtn.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.avatarImg:// 更换头像点击事件
//                menuWindow = new SelectPicPopupWindow(mContext, itemsOnClick);
//                menuWindow.showAtLocation(findViewById(R.id.mainLayout),
//                        Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
//                break;
//            case R.id.loginBtn://登录按钮跳转事件
//                startActivity(new Intent(mContext, UploadActivity.class));
//                break;
//
//            default:
//                break;
//        }
//    }
//
//    //为弹出窗口实现监听类
//    private OnClickListener itemsOnClick = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            menuWindow.dismiss();
//            switch (v.getId()) {
//                // 拍照
//                case R.id.takePhotoBtn:
//                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    //下面这句指定调用相机拍照后的照片存储的路径
//                    takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
//                    startActivityForResult(takeIntent, REQUESTCODE_TAKE);
//                    break;
//                // 相册选择图片
//                case R.id.pickPhotoBtn:
//                    Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
//                    // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
//                    pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                    startActivityForResult(pickIntent, REQUESTCODE_PICK);
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        switch (requestCode) {
//            case REQUESTCODE_PICK:// 直接从相册获取
//                try {
//                    startPhotoZoom(data.getData());
//                } catch (NullPointerException e) {
//                    e.printStackTrace();// 用户点击取消操作
//                }
//                break;
//            case REQUESTCODE_TAKE:// 调用相机拍照
//                File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
//                startPhotoZoom(Uri.fromFile(temp));
//                break;
//            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
//                if (data != null) {
//                    setPicToView(data);
//                }
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    /**
//     * 裁剪图片方法实现
//     * @param uri
//     */
//    public void startPhotoZoom(Uri uri) {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
//        intent.putExtra("crop", "true");
//        // aspectX aspectY 是宽高的比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        // outputX outputY 是裁剪图片宽高
//        intent.putExtra("outputX", 300);
//        intent.putExtra("outputY", 300);
//        intent.putExtra("return-data", true);
//        startActivityForResult(intent, REQUESTCODE_CUTTING);
//    }
//
//    /**
//     * 保存裁剪之后的图片数据
//     * @param picdata
//     */
//    private void setPicToView(Intent picdata) {
//        Bundle extras = picdata.getExtras();
//        if (extras != null) {
//            // 取得SDCard图片路径做显示
//            Bitmap photo = extras.getParcelable("data");
//            Drawable drawable = new BitmapDrawable(null, photo);
//
//
//            // 新线程后台上传服务端
//        }
//    }
//}