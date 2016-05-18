package com.nupt.shrimp.schoolmarket.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nupt.shrimp.schoolmarket.R;
import com.nupt.shrimp.schoolmarket.utils.common.NetworkUtils;
import com.nupt.shrimp.schoolmarket.utils.layout.CustomProgressDialog;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录Activity
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private AutoCompleteTextView mUsernameEdit;
    private EditText mPasswordEdit;
    private Button mLoginButton;
    private View loginLayout;
    private  Context mContext;
    private CustomProgressDialog pd;

    private TextView mRegisterText;
    private TextView mForgetText;

    private static LoginHandler mHandler;
    private String httpUrl = "http://115.159.98.48:8080/Shrimp/service/login";
    // 服务器返回登录的不同respCode
    private final static String MSG_SERVER_SUCCESS = "00";
    private final static String MSG_SERVER_PASSWORD_WRONG = "01";
    private final static String MSG_SERVER_USERNAME_WRONG = "02";
    // 用于LoginHandler更新UI
    private final static int MSG_HANDLER_SUCCESS  = 0;
    private final static int MSG_HANDLER_PASSWORD_WRONG = 1;
    private final static int MSG_HANDLER_USERNAME_WRONG = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mContext = LoginActivity.this;
        mHandler = new LoginHandler(LoginActivity.this);
        initView();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String usernameByRegisterActivity = "";
        if(data != null && data.getExtras().getString("phone")!= null){
            usernameByRegisterActivity= data.getExtras().getString("phone");//得到新Activity 关闭后返回的数据
            mUsernameEdit.setText(usernameByRegisterActivity);
        }
        Log.i("拿到RegisteAcitvity返回data", usernameByRegisterActivity);
    }
    /**
     * 初始化控件
     */
    private void initView() {
        mUsernameEdit = (AutoCompleteTextView) findViewById(R.id.login_username);
        mPasswordEdit = (EditText) findViewById(R.id.login_password);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mRegisterText = (TextView) findViewById(R.id.login_register);
        mForgetText = (TextView) findViewById(R.id.login_forget_password);
        loginLayout = (View)findViewById(R.id.login_layout);

        mLoginButton.setOnClickListener(this);
        mLoginButton.getBackground().setAlpha(100);
        mRegisterText.setOnClickListener(this);
        mForgetText.setOnClickListener(this);
        loginLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                login();
                break;
            case R.id.login_register:
                register();
                break;
            case R.id.login_forget_password:
                forgetPassword();
                break;
            case R.id.login_layout:
                //强制隐藏键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(loginLayout.getWindowToken(), 0);
                break;
        }
    }

    /**
     * 登录
     */
    private void login() {

        // Reset errors.
        mUsernameEdit.setError(null);
        mPasswordEdit.setError(null);

        final String username = mUsernameEdit.getText().toString();
        final String password = mPasswordEdit.getText().toString();

        boolean fail = false;
        View focusView = null;

        if (username.length() == 0 || username.equals("")) {
            mUsernameEdit.setError("登录名不能为空");
            fail = true;
            focusView = mUsernameEdit;
        } else if (password.length() == 0 || password.equals("")) {
            mPasswordEdit.setError("密码不能为空");
            fail = true;
            focusView = mPasswordEdit;
        }
        if(!NetworkUtils.isNetAvailable(mContext)){
            fail = true;
            Toast.makeText(mContext,"网络连接错误，请稍后重试",Toast.LENGTH_SHORT).show();
        }
        if (focusView!=null && fail) {
            focusView.requestFocus();
            return;
        }
        // 发送到post到服务器进行登录验证
        startProgressDialog();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,httpUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("正常返回", "response -> " + response);
                        // 返回的是json，解析其中的respMsg字段,并且使用Toast提示
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String respCode = jsonObject.get("respCode").toString();
                            switch (respCode){
                                case MSG_SERVER_SUCCESS:
                                    mHandler.sendEmptyMessage(MSG_HANDLER_SUCCESS);
                                    break;
                                case MSG_SERVER_PASSWORD_WRONG:
                                    mHandler.sendEmptyMessage(MSG_HANDLER_PASSWORD_WRONG);
                                    break;
                                case MSG_SERVER_USERNAME_WRONG:
                                    mHandler.sendEmptyMessage(MSG_HANDLER_USERNAME_WRONG);
                                    break;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stopProgressDialog();
                Log.e("错误返回", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //在这里设置需要post的参数
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    /**
     * 注册
     */
    private void register() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(intent,0);// RegisterActivity的识别码为0
    }

    /**
     * 忘记密码
     */
    private void forgetPassword() {

    }

    /**
     * 显示进度条
     */
    private void startProgressDialog(){
        if (pd == null){
            pd = CustomProgressDialog.createDialog(this);
        }

        pd.show();
    }

    /**
     * 隐藏进度条
     */
    private void stopProgressDialog(){
        if (pd != null){
            pd.dismiss();
            pd = null;
        }
    }

    /**
     * 本地持久化用户名
     */
    private void saveUserNanme(String username){
        SharedPreferences sharedPreferences = getSharedPreferences("Shrimp", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putString("username",username);
        editor.commit();
        Log.d("保存用户名","成功");
    }
    /**
     * 登录Handler
     */
    private class LoginHandler extends Handler {

        private WeakReference<LoginActivity> ref;

        public LoginHandler(LoginActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity activity = ref.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case MSG_HANDLER_SUCCESS:
                    saveUserNanme(mUsernameEdit.getText().toString());
                    Intent intent = new Intent(activity, MainActivity.class);
                    stopProgressDialog();
                    activity.startActivity(intent);
                    finish();
                    Toast.makeText(mContext,"登录成功",Toast.LENGTH_SHORT).show();

                    break;
                case MSG_HANDLER_PASSWORD_WRONG:
                    Toast.makeText(mContext,"密码错误",Toast.LENGTH_SHORT).show();
                    stopProgressDialog();
                    break;
                case MSG_HANDLER_USERNAME_WRONG:
                    Toast.makeText(mContext,"用户不存在",Toast.LENGTH_SHORT).show();
                    stopProgressDialog();
                    break;
            }
        }
    }

}
