package com.nupt.shrimp.schoolmarket.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nupt.shrimp.schoolmarket.R;
import com.nupt.shrimp.schoolmarket.model.bean.BuyCartItem;
import com.nupt.shrimp.schoolmarket.utils.common.GlobalDef;
import com.nupt.shrimp.schoolmarket.utils.layout.CircleImageView;
import com.nupt.shrimp.schoolmarket.utils.layout.SelectPicPopupWindow;
import com.nupt.shrimp.schoolmarket.view.fragment.RecycleViewFragment;
import com.nupt.shrimp.schoolmarket.model.bean.ItemModel;
import com.nupt.shrimp.schoolmarket.view.fragment.RecycleViewFragment_2;
import com.nupt.shrimp.schoolmarket.view.fragment.RecycleViewFragment_3;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**ArrayList
 * Created by cundong on 2015/10/29.
 *
 * 带HeaderView的分页加载LinearLayout RecyclerView
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

   // 侧拉菜单view
    private NavigationView navigationView = null;
    // 侧拉菜单容器
    private DrawerLayout drawer = null;
    // 工具栏
    private Toolbar toolbar = null;
    // 和drawerlayout配合使用，相当于侧拉菜单的监听器
    private ActionBarDrawerToggle toggle = null;
    // 用于放item，使得可以滑动
    private ViewPager viewPager = null;
    // 顶部滑动条
    private TabLayout tabLayout = null;
//    // 购物车商品
//    public static  ArrayList<BuyCartItem> selectedList = new ArrayList<BuyCartItem>();
    // 购物车余额TextView
    public TextView cartFloatTextView =  null;

    public ImageView cartFloatImageView = null;
    // 头像图片
    private CircleImageView avatarImg;
    // 自定义的头像编辑弹出框
    private SelectPicPopupWindow menuWindow;

    private static final int REQUESTCODE_PICK = 0;		// 相册选图标记
    private static final int REQUESTCODE_TAKE = 1;		// 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 2;	// 图片裁切标记
    private static final String IMAGE_FILE_NAME = "avatarImage.jpg";// 头像文件名称

    private void init() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // 拿到当前的布局文件
        drawer = (DrawerLayout) findViewById(R.id.main_activity);
        // 滑动条
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        // 配合滑动条使用
        viewPager= (ViewPager) findViewById(R.id.viewpager);
        // 购物车总额
        cartFloatTextView = (TextView)findViewById(R.id.cartFloatTextView);
        cartFloatImageView = (ImageView)findViewById(R.id.cartFloatImageView);
        // 侧拉监听器，用于监听item状态
        navigationView.setNavigationItemSelectedListener(this);
        // 设置toolbar
        setSupportActionBar(toolbar);
        cartFloatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BuyCartActivity.class);
                startActivity(intent);
            }
        });
        // 设置头像
        View header = navigationView.getHeaderView(0);
        avatarImg = (CircleImageView)header.findViewById(R.id.avatarImg);
        avatarImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                menuWindow = new SelectPicPopupWindow(MainActivity.this, itemsOnClick);
                menuWindow.showAtLocation(findViewById(R.id.main_activity),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });

        // 监听侧拉事件
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        tabLayout.setupWithViewPager(viewPager);
        // 设置初始显示的购物车总额
        countBuyCartListTextView();
        // 设置head 不用的时候可以注释掉
        // RecyclerViewUtils.setHeaderView(mRecyclerView, new SampleHeader(this));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_activity);
        // 如果侧拉显示状态就关闭侧拉，如果不是就关闭activity，Start为从左向右弹出显示
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.home_page) {

        } else if (id == R.id.collect) {

        } else if (id == R.id.details) {
            Intent intent = new Intent(MainActivity.this,PersonDataActivity.class);
            startActivity(intent);

        } else if (id == R.id.settings) {

        } else if (id == R.id.contact_us) {

        } else if (id == R.id.about_us) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_activity);
        // 关闭侧拉
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(RecycleViewFragment.getInstance(), "饮   料");
        adapter.addFragment(RecycleViewFragment_2.getInstance(), "食   品");
        adapter.addFragment(RecycleViewFragment_3.getInstance(), "生活 用品");
        viewPager.setAdapter(adapter);
    }
    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
    // 设置购物车的list
    public void addBuyCartList(ItemModel item){
        boolean isContain = false;
        //如果list中包含了id就修改num，如果不包含就add
        for (int i = 0; i < GlobalDef.selectedList.size(); i++) {
            if (GlobalDef.selectedList.get(i).getId() == item.getId()) {
                BuyCartItem temp = GlobalDef.selectedList.get(i);
                temp.setNum(temp.getNum() + 1);
                GlobalDef.selectedList.set(i, temp);
                isContain = true;
                break;
            }
        }
        if(!isContain){
            BuyCartItem buyCartItem = new BuyCartItem();
            buyCartItem.setTitle(item.getTitle());
            buyCartItem.setPrice(item.getPrice());
            buyCartItem.setCatrgory(item.getCatrgory());
            buyCartItem.setId(item.getId());
            buyCartItem.setImageName(item.getImageName());
            buyCartItem.setNum(1);
            GlobalDef.selectedList.add(buyCartItem);
        }
    }
    public void countBuyCartListTextView(){
        double allPrice = 0;
        for (BuyCartItem item : GlobalDef.selectedList) {
            allPrice += item.getPrice()*item.getNum();
        }
        if(allPrice>0) {
            DecimalFormat df = new DecimalFormat("######0.00");
            cartFloatTextView.setText("￥" + String.valueOf(df.format(allPrice)));
        }
    }
    // 改变list中的值
    public static void setItemById(BuyCartItem item){
        for(int i = 0; i< GlobalDef.selectedList.size(); i++){
            if(GlobalDef.selectedList.get(i).getId()==item.getId()){
                GlobalDef.selectedList.set(i,item);
                break;
            }
        }
    }

    public void onResume(){
        super.onResume();
        countBuyCartListTextView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUESTCODE_PICK:// 直接从相册获取
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();// 用户点击取消操作
                }
                break;
            case REQUESTCODE_TAKE:// 调用相机拍照
                File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                startPhotoZoom(Uri.fromFile(temp));
                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        // 取得SDCard图片路径做显示
                        Bitmap photo = extras.getParcelable("data");
                        Drawable drawable = new BitmapDrawable(null, photo);
                        avatarImg.setImageDrawable(drawable);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * 裁剪图片方法实现
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }
    //为弹出窗口实现监听类
    private OnClickListener itemsOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                // 拍照
                case R.id.takePhotoBtn:
                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //下面这句指定调用相机拍照后的照片存储的路径
                    takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                    startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    break;
                // 相册选择图片
                case R.id.pickPhotoBtn:
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                    // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                    pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(pickIntent, REQUESTCODE_PICK);
                    break;
                default:
                    break;
            }
        }
    };
}