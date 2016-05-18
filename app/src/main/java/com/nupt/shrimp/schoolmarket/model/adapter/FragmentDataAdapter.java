package com.nupt.shrimp.schoolmarket.model.adapter;

/**
 * Created by sunguoyan on 2016/4/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nupt.shrimp.schoolmarket.R;
import com.nupt.shrimp.schoolmarket.model.bean.ItemModel;
import com.nupt.shrimp.schoolmarket.utils.common.GlobalDef;
import com.nupt.shrimp.schoolmarket.utils.layout.CartAnimationUtil;
import com.nupt.shrimp.schoolmarket.view.activity.MainActivity;
import com.nupt.shrimp.schoolmarket.view.fragment.RecycleViewFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * item用的adapter
 */
public class FragmentDataAdapter extends RecyclerView.Adapter{

    private LayoutInflater mLayoutInflater;
    private SortedList<ItemModel> mSortedList;
    private Context  context;
    private final String ADD_PRODUCT_POST = "http://115.159.98.48:8080/Shrimp/service/order";
    /**
     *
     * @param context
     *
     */
    public FragmentDataAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        this.context = context;

        mSortedList = new SortedList<>(ItemModel.class, new SortedList.Callback<ItemModel>() {

            /**
             * 返回一个负整数（第一个参数小于第二个）、零（相等）或正整数（第一个参数大于第二个）
             */
            @Override
            public int compare(ItemModel o1, ItemModel o2) {

                if (o1.id < o2.id) {
                    return -1;
                } else if (o1.id > o2.id) {
                    return 1;
                }
                return 0;
            }

            @Override
            public boolean areContentsTheSame(ItemModel oldItem, ItemModel newItem) {
                return oldItem.title.equals(newItem.title);
            }

            @Override
            public boolean areItemsTheSame(ItemModel item1, ItemModel item2) {
                return item1.id == item2.id;
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }
        });
    }

    public void addItems(ArrayList<ItemModel> list) {
        mSortedList.beginBatchedUpdates();
        for(ItemModel itemModel : list) {
            mSortedList.add(itemModel);
        }
        mSortedList.endBatchedUpdates();
    }

    public void deleteItems(ArrayList<ItemModel> items) {
        mSortedList.beginBatchedUpdates();
        for (ItemModel item : items) {
            mSortedList.remove(item);
        }
        mSortedList.endBatchedUpdates();
    }
    public void clearCurrentItems() {
        mSortedList.clear();
        RecycleViewFragment.mCurrentCounter = 0;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.adapter_item_fragment_main, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final ItemModel item = mSortedList.get(position);

        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.goodsInfoTextView.setText(item.getTitle());
        viewHolder.buyImageView.setImageResource(R.drawable.buy);
        viewHolder.priceTextView.setText("￥" + item.getPrice() + "元");

        String url = "http://115.159.98.48:8080/Shrimp/service/image?image="+item.getImageName()+"&category="+item.getCatrgory();
        getImageFromServer(url, viewHolder.goodsImageView);

        final ImageView imageView= viewHolder.buyImageView;
        viewHolder.buyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestQueue requestQueue = Volley.newRequestQueue(context);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_PRODUCT_POST, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("addUser", response);
                        CartAnimationUtil.setAnim((Activity) context, imageView, (ImageView) ((Activity) context).findViewById(R.id.cartFloatImageView));
                        CartAnimationUtil.setOnEndAnimListener(new onEndAnim());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"加入购物车错误，请重试!",Toast.LENGTH_SHORT);
                        Log.e("TAG", error.getMessage(), error);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("param", "modify");
                        // 根据id查找当前list中的item
                        int currentNum  = 1;
                        for(int i = 0;i< GlobalDef.selectedList.size();i++) {
                            if (GlobalDef.selectedList.get(i).getId() == item.getId()) {
                                currentNum = GlobalDef.selectedList.get(i).getNum() + 1;
                                break;
                            }
                        }
                        JSONObject jsonObject_userId_info = new JSONObject();
                        JSONObject jsonObject_num_productId = new JSONObject();
                        JSONArray jsonArray = new JSONArray();
                        try {

                            jsonObject_num_productId.put("productId", item.getId());
                            jsonObject_num_productId.put("productNum",currentNum);

                            jsonArray.put(jsonObject_num_productId);
                            jsonObject_userId_info.put("info",jsonArray);

                            SharedPreferences sharedPreferences = context.getSharedPreferences("Shrimp", Activity.MODE_PRIVATE);
                            String username = sharedPreferences.getString("username","");
                            jsonObject_userId_info.put("userId",username);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        ((MainActivity) context).addBuyCartList(item);
                        map.put("orderInfo", jsonObject_userId_info.toString());
                        Log.d("发送orderInfo",jsonObject_userId_info.toString());
                        return map;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSortedList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView goodsImageView;
        private ImageView buyImageView;

        private TextView priceTextView;
        private TextView goodsInfoTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            goodsImageView = (ImageView) itemView.findViewById(R.id.goodsImageView);
            buyImageView = (ImageView) itemView.findViewById(R.id.buyImageView);
            priceTextView = (TextView) itemView.findViewById(R.id.priceTextView);
            goodsInfoTextView = (TextView) itemView.findViewById(R.id.goodsInfoTextView);
        }
    }

    /**
     * 从服务器加载图片，使用一级缓存
     * @param url
     * @param imageView
     */
    private void getImageFromServer(String url,ImageView imageView) {
        RequestQueue mQueue = Volley.newRequestQueue(context);
        ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView,
                R.drawable.default_image, R.drawable.failed_image);
        imageLoader.get(url, listener,200,200);
    }

    public class BitmapCache implements ImageLoader.ImageCache {

        private LruCache<String, Bitmap> mCache;

        public BitmapCache() {
            int maxSize = 30 * 1024 * 1024;
            mCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
        }

    }
    /**
     * 动画结束后，更新所有数量和所有价格
     */
    class onEndAnim implements CartAnimationUtil.OnEndAnimListener {
        @Override
        public void onEndAnim() {
            Toast t = Toast.makeText(context,"已加入购物车",Toast.LENGTH_SHORT);
            t.show();
            System.out.println("加入购物车完毕");
            ((MainActivity) context).countBuyCartListTextView();
        }
    }

}
