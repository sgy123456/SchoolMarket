package com.nupt.shrimp.schoolmarket.view.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nupt.shrimp.schoolmarket.R;
import com.nupt.shrimp.schoolmarket.model.adapter.BuyCartAdapter;
import com.nupt.shrimp.schoolmarket.model.bean.BuyCartItem;
import com.nupt.shrimp.schoolmarket.utils.common.GlobalDef;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BuyCartActivity extends AppCompatActivity implements BuyCartAdapter.IonSlidingViewClickListener {
    // 数据adapter，除了header和footer的item
    private  BuyCartAdapter mDataAdapter = null;
    // 全选框
    private CheckBox selectAllCheckBox = null;
    private ArrayList <BuyCartItem> list = null;
    private TextView countAllTextView = null;
    // 用于adapter的容器
    private RecyclerView recyclerView;
    private CheckBox checkBoxAllSelect = null;
    private Toolbar toolbar = null;
    private Button buyButton = null;
    private final String getBuyCartByUserId_URL = "http://115.159.98.48:8080/Shrimp/service/order?param=query&userId=";
    private final String sendBuyInfoURL = "http://115.159.98.48:8080/Shrimp/service/buy";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_cart);
        init();
    }
    @Override
    public void onItemClick(View view, int position) {
        Log.i("RecycleviewFragment", "点击项：" + position);
    }

    @Override
    public void onDeleteBtnCilck(View view, int position) {
        Log.i("RecycleviewFragment", "删除项：" + position);
        mDataAdapter.deleteItem(list.get(position));
        list.remove(position);
    }
    public void init(){
        countAllTextView = (TextView)findViewById(R.id.textview_all_count);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        selectAllCheckBox = (CheckBox)findViewById(R.id.all_select_checkbox);
        recyclerView = (RecyclerView)findViewById(R.id.list);
        checkBoxAllSelect = (CheckBox)findViewById(R.id.all_select_checkbox);
        buyButton = (Button)findViewById(R.id.button_buy);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendBuyInfoToServer();
            }
        });
        checkBoxAllSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    allSelect();
                } else {
                    allSelectRemoved();
                }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initData();
    }
    public void initData(){
        list = new ArrayList<BuyCartItem>();
        mDataAdapter = new BuyCartAdapter(BuyCartActivity.this);
        list = GlobalDef.selectedList;
        mDataAdapter.addItems(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mDataAdapter);
        setCountAllTextView();
    }
    // 改变list中的值
    public void setItemById(BuyCartItem item){
        for(int i = 0; i< list.size(); i++){
            if(list.get(i).getId()==item.getId()){
                list.set(i,item);
                break;
            }
        }
    }
    //合计的值
    public void setCountAllTextView(){
        double allPrice = 0;
        for(BuyCartItem item:list){
            // 如果被选中则算到合计中
            if(item.getIsChecked()) {
                allPrice += item.getPrice() * item.getNum();
            }
        }
        DecimalFormat df = new DecimalFormat("######0.00");
        countAllTextView.setText("合计:￥"+String.valueOf(df.format(allPrice)));
    }
    // 选中全选时
    public void allSelect(){
        for(int i = 0; i< list.size(); i++){
            BuyCartItem item = list.get(i);
            item.setIsChecked(true);
            list.set(i, item);
            setCountAllTextView();
            mDataAdapter.clearCurrentItems();
            mDataAdapter.addItems(list);
            recyclerView.setAdapter(mDataAdapter);
        }
    }
    // 取消全选时
    public void allSelectRemoved(){
        for(int i = 0; i< list.size(); i++){
            BuyCartItem item = list.get(i);
            item.setIsChecked(false);
            list.set(i, item);
            setCountAllTextView();
            mDataAdapter.clearCurrentItems();
            mDataAdapter.addItems(list);
            recyclerView.setAdapter(mDataAdapter);
        }
    }
    public void initList(String userId){
        RequestQueue mQueue = Volley.newRequestQueue(BuyCartActivity.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getBuyCartByUserId_URL+userId,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("获得返回商品List", response.toString());
                        list.clear();
                        for(int i = 0; i< response.length();i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                BuyCartItem itemModel = new BuyCartItem();
                                itemModel.setId(jsonObject.getInt("id"));
                                itemModel.setCatrgory(jsonObject.getString("catetory"));
                                itemModel.setTitle(jsonObject.getString("productName"));
                                itemModel.setImageName(jsonObject.getString("imageName"));
                                itemModel.setPrice(jsonObject.getDouble("price"));
                                itemModel.setNum(jsonObject.getInt("num"));
                                list.add(itemModel);
                                Collections.sort(list);
                            }catch(Exception e){
                                e.printStackTrace();
                            }

                        }
                        Collections.sort(list);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
            }
        }){
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                try {
                    JSONArray jsonArray = new  JSONArray(new String(response.data, "UTF-8"));
                    return Response.success(jsonArray, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (Exception je) {
                    return Response.error(new ParseError(je));
                }
            }
        };
        mQueue.add(jsonArrayRequest);
    }

    /**
     * 发送购买信息给服务器
     */
    private void sendBuyInfoToServer(){
        SharedPreferences sharedPreferences = getSharedPreferences("Shrimp", Activity.MODE_PRIVATE);
        final String user_id = sharedPreferences.getString("username", "");

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, sendBuyInfoURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("addUser", response);
                list.clear();
                Toast.makeText(BuyCartActivity.this, "下单成功，请稍后！", Toast.LENGTH_SHORT).show();
                finish();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                JSONObject jsonObject_userId = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                try {
                    for(int i = 0;i< GlobalDef.selectedList.size();i++) {
                        JSONObject jsonObject_num_productId = new JSONObject();

                        jsonObject_num_productId.put("productId", GlobalDef.selectedList.get(i).getId());
                         jsonObject_num_productId.put("productNum",GlobalDef.selectedList.get(i).getNum());
                         jsonArray.put(jsonObject_num_productId);
                    }
                    jsonObject_userId.put("userId",user_id);
                    jsonObject_userId.put("info",jsonArray);
                }catch (Exception e){
                    e.printStackTrace();
                }
                map.put("buyInfo", jsonObject_userId.toString());
                Log.d("发送购买信息", jsonObject_userId.toString());
                return map;
            }
        };
        requestQueue.add(stringRequest);
    };
}
