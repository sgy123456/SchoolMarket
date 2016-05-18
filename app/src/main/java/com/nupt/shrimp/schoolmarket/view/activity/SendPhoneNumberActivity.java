package com.nupt.shrimp.schoolmarket.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nupt.shrimp.schoolmarket.R;

public class SendPhoneNumberActivity extends AppCompatActivity {

    // 工具栏
    private Toolbar toolbar = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_phone_number);

        initView();

    }
    private void initView(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.arrow_left);
//        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle("短信注册");
        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
    }
}

