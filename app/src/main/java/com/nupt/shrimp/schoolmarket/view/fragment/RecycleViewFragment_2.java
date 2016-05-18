package com.nupt.shrimp.schoolmarket.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.nupt.shrimp.schoolmarket.R;
import com.nupt.shrimp.schoolmarket.model.adapter.FragmentDataAdapter;
import com.nupt.shrimp.schoolmarket.model.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.nupt.shrimp.schoolmarket.model.bean.ItemModel;
import com.nupt.shrimp.schoolmarket.utils.common.NetworkUtils;
import com.nupt.shrimp.schoolmarket.utils.layout.RecyclerViewStateUtils;
import com.nupt.shrimp.schoolmarket.view.EndlessLinearLayout.EndlessRecyclerOnScrollListener;
import com.nupt.shrimp.schoolmarket.view.EndlessLinearLayout.footer.LoadingFooter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecycleViewFragment_2 extends Fragment implements OnRefreshListener{

    private List<String>imageUrls = new ArrayList<String>();

    private static ArrayList<ItemModel> productInfoAll = new ArrayList<ItemModel>();;

    // 数据adapter，除了header和footer的item
    private FragmentDataAdapter mDataAdapter = null;
    // 包括了footer和header，和inner adapter即mDataAdapter
    private HeaderAndFooterRecyclerViewAdapter mHeaderAndFooterRecyclerViewAdapter = null;
    // item list 只用于初始化
    private ArrayList<ItemModel> dataList = null;
    // 处理网络请求的handler
    private PreviewHandler mHandler;
    // 主页面布局
    private static RecyclerView mRecyclerView = null;
    // 已经获取到多少条数据了
    public static int mCurrentCounter = 0;
    // 服务器端一共多少条数据
    private static int TOTAL_COUNTER = 32;
    // 每一页展示多少条数据
    private static final int REQUEST_COUNT = 10;
    // 持有当前使用该Frament的Activity引用
    private Context context;
    // 下拉刷新的布局
    private SwipeRefreshLayout mSwipeRefreshWidget;
    private final static String CATEGORY = "2";
    private final static String FIND_ALL_DATA = "http://115.159.98.48:8080/Shrimp/service/query?param=query&category="+CATEGORY;

    private RecycleViewFragment_2() {}
    private static RecycleViewFragment_2 singleton = null;
    public static RecycleViewFragment_2 getInstance() {
        if (mRecyclerView == null) {
            singleton = new RecycleViewFragment_2();
        }
        return singleton;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 防止缓存中已经有了就不再新建布局
        if(null == mSwipeRefreshWidget) {
            mSwipeRefreshWidget = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_recycle_view, container, false);
        }
        mRecyclerView = (RecyclerView)mSwipeRefreshWidget.findViewById(R.id.list);
        init();
        getInitalDataFromServer(FIND_ALL_DATA);
        // 缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个已经有parent的错误。
        ViewGroup parent = (ViewGroup) mSwipeRefreshWidget.getParent();
        if (parent != null) {
            parent.removeView(mSwipeRefreshWidget);
        }
        return mSwipeRefreshWidget;
    }
    @Override
    public void onRefresh() {
        updateDataFromServer(FIND_ALL_DATA);
    }
    /**
     * 初始化context、handler、以及监听事件
     */
    private void init() {

        // 持有应用
        context = this.getActivity();
        mHandler= new PreviewHandler(RecycleViewFragment_2.this);
    }

    private void notifyDataSetChanged() {
        mHeaderAndFooterRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItems(ArrayList<ItemModel> list) {
        mDataAdapter.addItems(list);
        mCurrentCounter += list.size();
    }

    private void deleteItems(ArrayList<ItemModel> list) {
        mDataAdapter.deleteItems(list);
        mCurrentCounter -= list.size();
    }
    /**
     * footer的点击监听
     */
    private View.OnClickListener mFooterClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerViewStateUtils.setFooterViewState(context, mRecyclerView, REQUEST_COUNT, LoadingFooter.State.Loading, null);
            requestData();
        }
    };
    /**
     *  整个headAndFooter的监听事件，滑到底就显示XXX
     */
    private EndlessRecyclerOnScrollListener mOnScrollListener = new EndlessRecyclerOnScrollListener() {

        @Override
        public void onLoadNextPage(View view) {
            super.onLoadNextPage(view);

            LoadingFooter.State state = RecyclerViewStateUtils.getFooterViewState(mRecyclerView);
            if(state == LoadingFooter.State.Loading) {
                Log.d("@Sunguoyan", "the state is Loading, just wait..");
                return;
            }

            if (mCurrentCounter < TOTAL_COUNTER) {
                // loading more
                RecyclerViewStateUtils.setFooterViewState(getActivity(), mRecyclerView, REQUEST_COUNT, LoadingFooter.State.Loading, null);
                requestData();
            } else {
                //the end
                RecyclerViewStateUtils.setFooterViewState(context, mRecyclerView, REQUEST_COUNT, LoadingFooter.State.TheEnd, null);
            }
        }
    };
    /**
     * 请求网络
     */
    private void requestData() {

        new Thread() {

            @Override
            public void run() {
                super.run();

                //模拟一下网络请求失败的情况
                if(NetworkUtils.isNetAvailable(context)) {
                    mHandler.sendEmptyMessage(-1);
                } else {
                    mHandler.sendEmptyMessage(-3);
                }
            }
        }.start();
    }
    /**
     * 处理加载item请求
     */
    private static class PreviewHandler extends Handler {

        private WeakReference<RecycleViewFragment_2> ref;

        PreviewHandler(RecycleViewFragment_2 context) {
            ref = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            final RecycleViewFragment_2 fragmentContext = ref.get();
            if (fragmentContext == null ) {
                return;
            }

            switch (msg.what) {
                //上拉添加数据
                case -1:
                    //模拟组装10个数据
                    ArrayList<ItemModel> newList = new ArrayList<ItemModel>();
                    for (int i = 0; i < 10; i++) {
                        if (newList.size() + mCurrentCounter >= TOTAL_COUNTER) {
                            break;
                        }

                        ItemModel item =  productInfoAll.get(i+mCurrentCounter);
                        newList.add(item);
                    }

                    fragmentContext.addItems(newList);
                    RecyclerViewStateUtils.setFooterViewState(mRecyclerView, LoadingFooter.State.Normal);
                    break;
                //下拉跟新数据
                case -2:
                    int oldDataListLength = fragmentContext.mCurrentCounter;
                    // 更新当前datalist里面的数据
                    fragmentContext.mDataAdapter.clearCurrentItems();
                    ArrayList<ItemModel> waitToUpdateList = new ArrayList<>();
                    if (oldDataListLength>productInfoAll.size()){
                        oldDataListLength = productInfoAll.size();
                        fragmentContext.mCurrentCounter = productInfoAll.size();
                    }
                    for(int i = 0; i< oldDataListLength;i++ ) {

                        ItemModel itemModel = new ItemModel();
                        itemModel = productInfoAll.get(i);
                        waitToUpdateList.add(itemModel);
                    }
                    fragmentContext.addItems(waitToUpdateList);
                    fragmentContext.mSwipeRefreshWidget.setRefreshing(false);
                    fragmentContext.notifyDataSetChanged();
                    break;
                case -3:
                    RecyclerViewStateUtils.setFooterViewState(fragmentContext.context,mRecyclerView, REQUEST_COUNT, LoadingFooter.State.NetWorkError,fragmentContext.mFooterClick);
                    break;
            }
        }
    }
    /**
     * 初始化商品信息
     * @param url
     * @return
     */
    private  void getInitalDataFromServer(String url) {
        productInfoAll.clear();
        RequestQueue mQueue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("获得返回商品List",response.toString());
                        try {
                        for(int i = 0; i< response.length();i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                ItemModel itemModel = new ItemModel();

                                int id = jsonObject.getInt("id");
                                String catrgory = jsonObject.getString("catetory");
                                String title = jsonObject.getString("productName");
                                String imageName = jsonObject.getString("imageName");
                                double price = jsonObject.getDouble("price");

                                itemModel.setId(id);
                                itemModel.setCatrgory(catrgory);
                                itemModel.setTitle(title);
                                itemModel.setImageName(imageName);
                                itemModel.setPrice(price);

                                if(itemModel.getCatrgory().equals(CATEGORY)) {
                                    productInfoAll.add(itemModel);
                                    Collections.sort(productInfoAll);
                                }
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        // 初始化数据量
                        TOTAL_COUNTER = productInfoAll.size();
                        Collections.sort(productInfoAll);

                        //init data
                        dataList = new ArrayList<>();

                        for (int i = 0; i < 10; i++) {

                            ItemModel item = new ItemModel();
                            item = productInfoAll.get(i);
                            dataList.add(item);
                        }
                        mDataAdapter = new FragmentDataAdapter(context);
                        mDataAdapter.addItems(dataList);
                        mCurrentCounter = dataList.size();
                        mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mDataAdapter);

                        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                        mRecyclerView.addOnScrollListener(mOnScrollListener);
                        mRecyclerView.setAdapter(mHeaderAndFooterRecyclerViewAdapter);

                        mSwipeRefreshWidget.setColorSchemeColors(R.color.pullToRefreshColor_1, R.color.pullToRefreshColor_2,
                                R.color.pullToRefreshColor_3, R.color.pullToRefreshColor_4);
                        mSwipeRefreshWidget.setOnRefreshListener(RecycleViewFragment_2.this);
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
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(jsonArrayRequest);
    }

    private  void updateDataFromServer(String url) {
        productInfoAll.clear();
        RequestQueue mQueue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("获得返回商品List",response.toString());
                        try {
                            for(int i = 0; i< response.length();i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                ItemModel itemModel = new ItemModel();

                                int id = jsonObject.getInt("id");
                                String catrgory = jsonObject.getString("catetory");
                                String title = jsonObject.getString("productName");
                                String imageName = jsonObject.getString("imageName");
                                double price = jsonObject.getDouble("price");

                                itemModel.setId(id);
                                itemModel.setCatrgory(catrgory);
                                itemModel.setTitle(title);
                                itemModel.setImageName(imageName);
                                itemModel.setPrice(price);

                                if(itemModel.getCatrgory().equals(CATEGORY)) {
                                    productInfoAll.add(itemModel);
                                    Collections.sort(productInfoAll);
                                }
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        // 初始化数据量
                        TOTAL_COUNTER = productInfoAll.size();
                        Collections.sort(productInfoAll);
                        mSwipeRefreshWidget.setColorSchemeColors(R.color.pullToRefreshColor_1, R.color.pullToRefreshColor_2,
                                R.color.pullToRefreshColor_3, R.color.pullToRefreshColor_4);
                        mSwipeRefreshWidget.setOnRefreshListener(RecycleViewFragment_2.this);
                        mHandler.sendEmptyMessage(-2);
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
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(jsonArrayRequest);
    }
}
