package com.nupt.shrimp.schoolmarket.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nupt.shrimp.schoolmarket.R;
import com.nupt.shrimp.schoolmarket.utils.common.LogUtils;

/**
 * 开屏页面
 */
public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity ";
    /**
     * 最短开屏时间
     */
    private static final int MIN_SPLASH_TIME = 2000;
    /**
     * 加载耗时工作，失败
     */
    private static final int FAILURE = 0;
    /**
     * 加载耗时工作，成功
     */
    private static final int SUCCESS = 1;

    private RelativeLayout rootLayout;
    private TextView versionText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        rootLayout = (RelativeLayout) findViewById(R.id.splash_layout);
        versionText = (TextView) findViewById(R.id.splash_version);

        versionText.setText(getVersion());

        // 启动app时的动画
        rootLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
    }

    @Override
    protected void onStart() {
        super.onStart();

        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                int result = FAILURE;
                long startTime = System.currentTimeMillis();
                result = loadCache();
                long loadingTime = System.currentTimeMillis() - startTime;
                if (loadingTime < MIN_SPLASH_TIME) {
                    try {
                        Thread.sleep(MIN_SPLASH_TIME - loadingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(Integer result) {
                SharedPreferences sharedPreferences = getSharedPreferences("Shrimp", Activity.MODE_PRIVATE);
                String username = sharedPreferences.getString("username", "");
                Log.i("username-sharedperence",username);
                if(username==""||username==null) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }else{
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                finish();
                //两个参数分别表示进入的动画和退出的动画
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                super.onPostExecute(result);
            }

        }.execute(new Void[]{});
    }

    /**
     * 获取当前应用程序的版本号
     */
    private String getVersion() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
            String version = packinfo.versionName;
            return version;
        } catch (NameNotFoundException e) {
            LogUtils.e(TAG + "版本号读取错误");
            return null;
        }
    }

    /**
     * 加载缓存
     *
     * @return 加载成功，返回SUCCESS；加载失败，返回FAILURE
     */
    private int loadCache() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return SUCCESS;
    }

}

