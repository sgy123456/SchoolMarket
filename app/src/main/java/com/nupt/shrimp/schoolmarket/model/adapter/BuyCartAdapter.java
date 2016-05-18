package com.nupt.shrimp.schoolmarket.model.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.util.SortedList;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.nupt.shrimp.schoolmarket.R;
import com.nupt.shrimp.schoolmarket.SlidingButtonView;
import com.nupt.shrimp.schoolmarket.Utils;
import com.nupt.shrimp.schoolmarket.model.bean.BuyCartItem;
import com.nupt.shrimp.schoolmarket.model.bean.ItemModel;
import com.nupt.shrimp.schoolmarket.view.activity.BuyCartActivity;
import com.nupt.shrimp.schoolmarket.view.activity.MainActivity;
import com.nupt.shrimp.schoolmarket.view.fragment.RecycleViewFragment;

import java.util.ArrayList;

/**
 * Created by sunguoyan on 2016/4/15.
 */
public class BuyCartAdapter extends RecyclerView.Adapter implements SlidingButtonView.IonSlidingButtonListener{
    private LayoutInflater mLayoutInflater;
    // 购物车所有的商品
    private SortedList<BuyCartItem> mSortedList;
    // 选中的商品
//    private Map<Integer,BuyCartItem> SelectedProduct;
    // 左滑删除监听回调
    private IonSlidingViewClickListener mIDeleteBtnClickListener;
    private SlidingButtonView mMenu = null;
    private Context context;
    /**
     * @param context
     *
     */
    public BuyCartAdapter (Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        mIDeleteBtnClickListener = (IonSlidingViewClickListener)context;
        // 数据集
        mSortedList = new SortedList<>(BuyCartItem.class, new SortedList.Callback<BuyCartItem>() {
            /**
             * 返回一个负整数（第一个参数小于第二个）、零（相等）或正整数（第一个参数大于第二个）
             */
            @Override
            public int compare(BuyCartItem o1, BuyCartItem o2) {

                if (o1.id < o2.id) {
                    return -1;
                } else if (o1.id > o2.id) {
                    return 1;
                }
                return 0;
            }

            @Override
            public boolean areContentsTheSame(BuyCartItem oldItem, BuyCartItem newItem) {
                return oldItem.title.equals(newItem.title);
            }

            @Override
            public boolean areItemsTheSame(BuyCartItem item1, BuyCartItem item2) {
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

    public void addItems(ArrayList<BuyCartItem> list) {
        mSortedList.beginBatchedUpdates();
        for(BuyCartItem BuyCartItem : list) {
            mSortedList.add(BuyCartItem);
        }
        mSortedList.endBatchedUpdates();
    }

    public void deleteItems(ArrayList<BuyCartItem> items) {
        mSortedList.beginBatchedUpdates();
        for (BuyCartItem item : items) {
            mSortedList.remove(item);
        }
        mSortedList.endBatchedUpdates();
    }
    public void deleteItem(BuyCartItem item) {
        mSortedList.beginBatchedUpdates();
        mSortedList.remove(item);
        mSortedList.endBatchedUpdates();
    }
    public void clearCurrentItems() {
        mSortedList.clear();
        RecycleViewFragment.mCurrentCounter = 0;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.adapter_item_buy_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final BuyCartItem item = mSortedList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.goodsInfoTextView.setText(item.getTitle());
        viewHolder.priceTextView.setText("￥" + item.getPrice() + "元");
        viewHolder.editTextNum.setText(String.valueOf(item.getNum()));
        //设置内容布局的宽为屏幕宽度
        viewHolder.layout_left.getLayoutParams().width = Utils.getScreenWidth(context);
        // 只有一开始或者点击全选时候才调用该方法
        viewHolder.signelCheckBox.setChecked(item.getIsChecked());
        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuyCartItem itemTemp = item;
                itemTemp.setNum(itemTemp.getNum() + 1);
                mSortedList.updateItemAt(position, itemTemp);
                ((BuyCartActivity) context).setCountAllTextView();
                ((BuyCartActivity) context).setItemById(itemTemp);
                MainActivity.setItemById(itemTemp);

            }
        });
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否有删除菜单打开
                if (menuIsOpen()) {
                    closeMenu();//关闭菜单
                } else {
                    int n = viewHolder.getLayoutPosition();
                    mIDeleteBtnClickListener.onItemClick(v, n);
                }
            }
        });
        viewHolder.btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n = viewHolder.getLayoutPosition();
                mIDeleteBtnClickListener.onDeleteBtnCilck(v, n);
            }
        });
        viewHolder.reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getNum() > 1) {
                    BuyCartItem itemTemp = item;
                    itemTemp.setNum(itemTemp.getNum()-1);
                    mSortedList.updateItemAt(position, itemTemp);
                    ((BuyCartActivity) context).setCountAllTextView();
                    ((BuyCartActivity) context).setItemById(itemTemp);
                    MainActivity.setItemById(itemTemp);
                }
            }
        });
//        viewHolder.signelCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                System.out.println("----------------isChecked"+isChecked);
//                BuyCartItem itemTemp = item;
//                itemTemp.setIsChecked(isChecked);
//                mSortedList.updateItemAt(position, itemTemp);
//                ((BuyCartActivity) context).setCountAllTextView();
//            }
//        });
        viewHolder.signelCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 由于SortedList一定会更新ui，但是跟新ui时候会造成checkbox闪烁，
                 * 所以不把ischecked的信息保存在Sortedlist中，而直接修改activity中的值
                 */
                if(item.getIsChecked()){
                    BuyCartItem itemTemp = item;
                    itemTemp.setIsChecked(false);
                    viewHolder.signelCheckBox.setChecked(false);
                    ((BuyCartActivity) context).setCountAllTextView();
                    ((BuyCartActivity) context).setItemById(itemTemp);
                }else{
                    BuyCartItem itemTemp = item;
                    itemTemp.setIsChecked(true);
                    viewHolder.signelCheckBox.setChecked(true);
                    ((BuyCartActivity) context).setCountAllTextView();
                    ((BuyCartActivity) context).setItemById(itemTemp);
                }
            }
        });
        String url = "http://115.159.98.48:8080/Shrimp/service/image?image="+item.getImageName() + "&category=" + item.getCatrgory();
        getImageFromServer(url, viewHolder.goodsImageView);

    }

    @Override
    public int getItemCount() {
        return mSortedList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView goodsImageView;
        // 单选
        private CheckBox signelCheckBox;
        // 输入数量的
        private EditText editTextNum;
        private Button add;
        private Button reduce;
        private TextView priceTextView;
        private TextView goodsInfoTextView;
        public ViewGroup layout_left;
        public TextView btn_Delete;
        public LinearLayout cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            goodsImageView = (ImageView) itemView.findViewById(R.id.goodsImageView);
            signelCheckBox = (CheckBox) itemView.findViewById(R.id.checxbox_singel);
            editTextNum = (EditText) itemView.findViewById(R.id.edit_text_count);
            add = (Button)itemView.findViewById(R.id.button_add);
            reduce = (Button)itemView.findViewById(R.id.button_reduce);
            priceTextView = (TextView) itemView.findViewById(R.id.priceTextView);
            goodsInfoTextView = (TextView) itemView.findViewById(R.id.goodsInfoTextView);
            layout_left = (ViewGroup) itemView.findViewById(R.id.layout_left);
            btn_Delete = (TextView) itemView.findViewById(R.id.tv_delete);
            cardView = (LinearLayout) itemView.findViewById(R.id.card_view);
            ((SlidingButtonView) itemView).setSlidingButtonListener(BuyCartAdapter.this);
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
            int maxSize = 10 * 1024 * 1024;
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
     * 删除菜单打开信息接收
     */
    @Override
    public void onMenuIsOpen(View view) {
        mMenu = (SlidingButtonView) view;
    }

    /**
     * 滑动或者点击了Item监听
     * @param slidingButtonView
     */
    @Override
    public void onDownOrMove(SlidingButtonView slidingButtonView) {
        if(menuIsOpen()){
            if(mMenu != slidingButtonView){
                closeMenu();
            }
        }
    }
    /**
     * 关闭菜单
     */
    public void closeMenu() {
        mMenu.closeMenu();
        mMenu = null;

    }
    /**
     * 判断是否有菜单打开
     */
    public Boolean menuIsOpen() {
        if(mMenu != null){
            return true;
        }
        Log.i("asd", "mMenu为null");
        return false;
    }
    public interface IonSlidingViewClickListener {
        void onItemClick(View view,int position);
        void onDeleteBtnCilck(View view,int position);
    }
}
