package com.nupt.shrimp.schoolmarket.utils.layout;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nupt.shrimp.schoolmarket.R;

/**
 * Created by sunguoyan on 2016/3/30.
 */
public class CartAnimationUtil {
    private static View view;
    private static Animation animation;
    /** 动画层 */
    private static ViewGroup anim_mask_layout;
    private static Activity mActivity;
    private static View mImgcar;
    private static OnEndAnimListener mEndAnimListener;
    //是否完成清理
    private static boolean isClean = false;
    /** 定义结束之后的接口 */
    public interface OnEndAnimListener{
        void onEndAnim();
    }
    public static void setOnEndAnimListener(OnEndAnimListener listenr){
        mEndAnimListener = listenr;
    }
    public static void setAnim(Activity activity , View imgphoto , View imgcar){
        mActivity = activity;
        mImgcar = imgcar;
        // 一个整型数组，用来存储按钮的在屏幕的X、Y坐标
        int[] start_location = new int[2];
        // 这是获取购买按钮的在屏幕的X、Y坐标（这也是动画开始的坐标）
        imgphoto.getLocationInWindow(start_location);
        int[] start_location1 = new int[]{start_location[0], start_location[1]};
        // buyImg是动画的图片，我的是购买的图片
        ImageView buyImg = new ImageView(mActivity);
        // 设置buyImg的图片
        buyImg.setImageResource(R.drawable.buy);
        // 开始执行动画
        startAnim(buyImg, start_location1);
    }
    /**
     *开始动画
     */
    private static void startAnim(final View v, int[] start_location) {

        anim_mask_layout = null;
        anim_mask_layout = createAnimLayout();
        anim_mask_layout.addView(v);//把购买的图片加入动画层
        view = addViewToAnimLayout(anim_mask_layout, v,start_location);
        view.setAlpha(1f);

        int[] end_location = new int[2];// 这是用来存储动画结束位置的X、Y坐标
        mImgcar.getLocationInWindow(end_location);// shopCart是那个购物车
        int width = getWindowsWidth(mActivity);
        // 计算位移
        int endY = end_location[1] - start_location[1];// 动画位移的y坐标
        int endX = end_location[0] - start_location[0] + (mImgcar.getWidth() / 2);// 动画位移的X坐标

        // 大小变化动画
        Animation mScaleAnimation = new ScaleAnimation(1f,0.5f,1,0.5f,Animation.RELATIVE_TO_SELF,0.1f,Animation.RELATIVE_TO_SELF,0.1f);
        mScaleAnimation.setDuration(1300);
        mScaleAnimation.setFillAfter(true);

        // 位置变化动画
        Animation mTranslateAnimation = new TranslateAnimation(0,endX,0,endY);
        mTranslateAnimation.setDuration(1300);
        // 旋转变化动画
//        Animation mRotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        mRotateAnimation.setDuration(1300);

        // 设置动画集
        final AnimationSet set = new AnimationSet(true);
        set.setFillAfter(false);
//        set.addAnimation(mRotateAnimation);
        set.addAnimation(mScaleAnimation);
        set.addAnimation(mTranslateAnimation);

        view.startAnimation(set);

        // 动画监听事件
        set.setAnimationListener(new Animation.AnimationListener() {
            // 动画的开始
            @Override
            public void onAnimationStart(Animation animation) {
                v.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
            // 动画的结束
            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
                anim_mask_layout.removeAllViews();
                isClean = true;
                YoYo.with(Techniques.Bounce).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mEndAnimListener.onEndAnim();
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {}
                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                }).interpolate(new BounceInterpolator()).duration(400).playOn(mImgcar);
            }
        });
    }
    /**
     *  创建动画层
     */
    private static ViewGroup createAnimLayout() {
        ViewGroup rootView = (ViewGroup)mActivity.getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(mActivity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setId(Integer.MAX_VALUE);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }
    private static View addViewToAnimLayout(final ViewGroup vg, final View view,int[] location) {
        int x = location[0];
        int y = location[1];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setLayoutParams(lp);
        return view;
    }
    /**
     * 获取屏幕的宽度
     */
    public final static int getWindowsWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

}
