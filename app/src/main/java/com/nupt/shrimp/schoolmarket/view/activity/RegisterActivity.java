package com.nupt.shrimp.schoolmarket.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nupt.shrimp.schoolmarket.R;
import com.nupt.shrimp.schoolmarket.utils.layout.CustomProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

public class RegisterActivity extends AppCompatActivity  implements View.OnClickListener {

    private EditText phoneEditText;
    private EditText phoneConfirmCodeEditText;
    private EditText passwordEditText;

    private Button sendConfirmCodeButton;
    private Button registerButton;

    private String phone = "";
    private String phoneConfirmCode = "";
    private String password = "";
    private String httpUrl = "http://115.159.98.48:8080/Shrimp/service/register";

    private EventHandler handler;
    private RegisterHandler registerHandler;
    private CustomProgressDialog pd;
    private Context context;
    private Toolbar toolbar;

    private final static String MSG_SERVER_SUCCESS = "00";
    private final static String MSG_SERVER_USER_EXIST = "01";

    // 用于registerHandler更新UI
    private final static int MSG_Handler_SUBMIT_CONFIRM_CODE = 0;
    private final static int MSG_Handler_GET_CONFIRM_CODE = 1;

    private final static int MSG_Handler_REGISTER_SUCCESS= 2;
    private final static int MSG_Handler_USER_EXIST = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // 初始化mob SDK
        SMSSDK.initSDK(this, "112f7ebba0db6", "7ec5ca4ba26de0d9df3ed7d304154cc7");
        context = this;
        initView();
        initListener();

    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
        SMSSDK.unregisterEventHandler(handler);
    }

    public void initView(){
        phoneEditText = (EditText)findViewById(R.id.register_phone);
        phoneConfirmCodeEditText = (EditText)findViewById(R.id.register_phone_confirm);
        passwordEditText = (EditText)findViewById(R.id.register_phone_password);

        sendConfirmCodeButton = (Button)findViewById(R.id.button_send_phone_confirm_code);
        registerButton = (Button)findViewById(R.id.button_register);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
    }

    public void initListener(){
        sendConfirmCodeButton.setOnClickListener(this);
        sendConfirmCodeButton.getBackground().setAlpha(100);
        registerButton.setOnClickListener(this);
        registerButton.getBackground().setAlpha(100);

        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                phone = phoneEditText.getText().toString().trim();
            }
        });

        phoneConfirmCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                phoneConfirmCode = phoneConfirmCodeEditText.getText().toString().trim();
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                password = passwordEditText.getText().toString().trim();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        registerHandler = new RegisterHandler(this);
        // 用于获取服务器回调
        handler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                stopProgressDialog();
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
//                    System.out.println("-------------提交验证码成功------------------");
//                    System.out.println("event"+event);
//                    System.out.println("result"+result);
//                    System.out.println("data"+data);
//                    /** 提交验证码 */
//                    afterSubmit(result, data);
                    Message message = new Message();
                    message.arg1 = result;
                    message.obj = data;
                    message.what = MSG_Handler_SUBMIT_CONFIRM_CODE;
                    registerHandler.sendMessage(message);
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
//                    System.out.println("-------------获取成功验证码------------------");
//                    System.out.println("event"+event);
//                    System.out.println("result"+result);
//                    System.out.println("data"+data);
//                    /** 获取验证码成功后的执行动作 */
//                    afterGet(result, data);
                    Message message = new Message();
                    message.arg1 = result;
                    message.obj = data;
                    message.what = MSG_Handler_GET_CONFIRM_CODE;
                    registerHandler.sendMessage(message);
                }
            }
        };
        SMSSDK.registerEventHandler(handler);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_send_phone_confirm_code:
                sendConfirmCode(phone);
                break;
            case R.id.button_register:
                System.out.println("--------------register-------------------");
                register();
                break;
        }
    }
    /**
     *  发送验证码
     */

    public void sendConfirmCode(String phone){
        // 验证手机号是合法
        if(phone.length()==0){
            Toast.makeText(context,"手机号不能为空",Toast.LENGTH_SHORT).show();
        }else if(phone.length() != 11){
            Toast.makeText(context,"手机号输入错误，请重新输入",Toast.LENGTH_SHORT).show();
        }else{
            // 调用mob SDK进行短信发送
            startProgressDialog();
            SMSSDK.getVerificationCode("+86", phone);
//            Toast.makeText(context,"短信发送成功",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 注册
     */
    public void register(){
        startProgressDialog();
        boolean fail = false;
        // 验证手机号是合法
        if(phone.length()==0){
//            phoneEditText.setError("手机号不能为空");
//            fail = true;
//            focusView = phoneEditText;
            stopProgressDialog();
            Toast.makeText(context,"手机号不能为空",Toast.LENGTH_SHORT).show();
            return;
        }else if(phone.length() != 11){
//            phoneEditText.setError("手机号输入错误，请重新输入");
//            fail = true;
//            focusView = phoneEditText;
            stopProgressDialog();
            Toast.makeText(context,"手机号输入错误，请重新输入",Toast.LENGTH_SHORT).show();
            return;
        }

        // 验证验证码是否合法
        if(phoneConfirmCode.length()==0){
//            phoneConfirmCodeEditText.setError("验证码不能为空");
//            fail = true;
//            focusView = phoneConfirmCodeEditText;
            stopProgressDialog();
            Toast.makeText(context,"验证码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }else if(phoneConfirmCode.length() != 4){
//            phoneConfirmCodeEditText.setError("验证码输入错误，请重新输入");
//            fail = true;
//            focusView = phoneConfirmCodeEditText;
            stopProgressDialog();
            Toast.makeText(context,"请输入四位数字验证码",Toast.LENGTH_SHORT).show();
            return;
        }

        // 输入密码
        if(password.length()==0){
//            passwordEditText.setError("密码不能为空");
//            fail = true;
//            focusView = passwordEditText;
            stopProgressDialog();
            Toast.makeText(context,"密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        // 如果fail，在editText后面提示
        if (!fail) {
            SMSSDK.submitVerificationCode("+86", phone, phoneConfirmCode);
        }
    }
    /**
     * 获取验证码成功后,的执行动作
     *
     * @param result
     * @param data
     */
    private void afterGet(final int result, final Object data) {
        if (result == SMSSDK.RESULT_COMPLETE) {
            Toast.makeText(context, "验证码已发送", Toast.LENGTH_SHORT).show();
            System.out.println("-------------验证码已发送------------");
        } else {
            ((Throwable) data).printStackTrace();
            Throwable throwable = (Throwable) data;
            // 根据服务器返回的网络错误，给toast提示
            int status = 0;
            try {
                JSONObject object = new JSONObject(throwable.getMessage());
                String des = object.optString("detail");//错误描述
                status = object.optInt("status");
                if (!TextUtils.isEmpty(des)) {
                    Toast.makeText(context, des, Toast.LENGTH_SHORT).show();
                    System.out.println("-------------" + des + "------------");

                    return;
                }
            } catch (JSONException e) {
                SMSLog.getInstance().w(e);
            }

            if(status >= 400) {
                Toast.makeText(context, "错误代码"+status, Toast.LENGTH_SHORT).show();
                System.out.println("-------------错误代码"+status+"------------");

            }else{
                // 如果没有返回错误desc为空，则显示网络连接错误
                Toast.makeText(context, "网络连接错误", Toast.LENGTH_SHORT).show();
                System.out.println("-------------网络连接错误------------");
            }
        }
    }
    /**
     * 提交验证码成功后的执行事件
     *
     * @param result
     * @param data
     */
    private void afterSubmit(final int result, final Object data) {
        stopProgressDialog();
        if (result == SMSSDK.RESULT_COMPLETE) {
            System.out.println("-------------验证码正确------------");
//            Toast.makeText(context, "验证码正确", Toast.LENGTH_SHORT).show();
            // 发送到post到服务器进行登录验证
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

                                if(respCode.equals(MSG_SERVER_SUCCESS)){
                                    registerHandler.sendEmptyMessage(MSG_Handler_REGISTER_SUCCESS);
                                }else if(respCode.equals(MSG_SERVER_USER_EXIST)){
                                    registerHandler.sendEmptyMessage(MSG_Handler_USER_EXIST);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("错误返回", error.getMessage(), error);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    //在这里设置需要post的参数
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", phone);
                    params.put("password", password);
                    return params;
                }
            };
            requestQueue.add(stringRequest);



        } else {
            ((Throwable) data).printStackTrace();
            // 验证码不正确
            int status = 0;
            String detail = "";
            String message = ((Throwable) data).getMessage();
            try {
                JSONObject json = new JSONObject(message);
                status = json.getInt("status");
                detail = json.getString("detail");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(status >= 400) {
                Toast.makeText(context, detail, Toast.LENGTH_SHORT).show();
                System.out.println("-------------"+detail+"------------");
            }else{
                // 如果没有返回错误desc为空，则显示网络连接错误
                Toast.makeText(context, "网络连接错误", Toast.LENGTH_SHORT).show();
                System.out.println("-------------网络连接错误------------");
            }
        }
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
     * 用于更新ui线程的handler
     */
    class RegisterHandler extends Handler {
        private WeakReference<RegisterActivity> ref;

        public RegisterHandler(RegisterActivity activity) {
            ref = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_Handler_SUBMIT_CONFIRM_CODE:
                    afterSubmit(msg.arg1,msg.obj);
                    break;
                case MSG_Handler_GET_CONFIRM_CODE:
                    afterGet(msg.arg1, msg.obj);
                    break;
                case MSG_Handler_REGISTER_SUCCESS:
                    Toast.makeText(context,"注册成功",Toast.LENGTH_SHORT).show();
                    //数据是使用Intent返回
                    Intent intent = new Intent();
                    //把返回数据存入Intent
                    intent.putExtra("phone", phone);
                    intent.putExtra("password",password);
                    //设置返回数据
                    RegisterActivity.this.setResult(RESULT_OK,intent);
                    finish();
                    break;
                case MSG_Handler_USER_EXIST:
                    Toast.makeText(context,"用户名已存在",Toast.LENGTH_SHORT).show();
                    phoneConfirmCodeEditText.setText("");
                    passwordEditText.setText("");
                    break;
            }
        }
    }
}
