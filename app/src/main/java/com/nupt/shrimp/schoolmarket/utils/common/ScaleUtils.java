package com.nupt.shrimp.schoolmarket.utils.common;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.nupt.shrimp.schoolmarket.MainApplication;

public class ScaleUtils {

    /**
     * dp转px
     * 
     * @param dp
     * @return
     */
    public static int dp2px(float dp) {
        final float scale = MainApplication.getContext().getResources()
                .getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * px转dp
     * 
     * @param px
     * @return
     */
    public static int px2dp(float px) {
        final float scale = MainApplication.getContext().getResources()
                .getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * 获得屏幕的宽度
     * 
     * @return
     */
    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) MainApplication.getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

}
