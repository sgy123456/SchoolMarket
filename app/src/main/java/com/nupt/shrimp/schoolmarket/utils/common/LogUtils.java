package com.nupt.shrimp.schoolmarket.utils.common;

import android.util.Log;

/**
 * 此类用于打印Log
 */
public class LogUtils {
    
    private static String TAG = "SchoolMarket";
    
    /** Log等级：不输出log */
    public static final int LOG_LEVEL_NONE = 0;
    /** Log等级：只打印error */
    public static final int LOG_LEVEL_ERR = 1;
    /** Log等级：可打印error和warning */
    public static final int LOG_LEVEL_WARN = 2;
    /** Log等级：可打印error、warning和info */
    public static final int LOG_LEVEL_INFO = 3;
    /** Log等级：可打印error、warning、info和debug */
    public static final int LOG_LEVEL_DBG = 4;
    /** Log等级：可打印所有log */
    public static final int LOG_LEVEL_VERBOSE = 5;
    
    /** 当前可打印的log等级 */
    public static final int LOG_LEVEL = LogUtils.LOG_LEVEL_VERBOSE;
    
    /**
     * 打印Log
     * @param level 设置Log的等级
     * @param msg 设置Log的内容
     */
    public static void LOG(int level, String msg) {
        if (level <= LOG_LEVEL) {
            switch (level) {
            case LOG_LEVEL_ERR:
                Log.e(TAG, msg);
                break;
            case LOG_LEVEL_WARN:
                Log.w(TAG, msg);
                break;
            case LOG_LEVEL_INFO:
                Log.i(TAG, msg);
                break;
            case LOG_LEVEL_DBG:
                Log.d(TAG, msg);
                break;
            case LOG_LEVEL_VERBOSE:
                Log.v(TAG, msg);
                break;
            }
        }
    }
    
    /**
     * 打印等级为Debug的Log
     * @param msg 设置Log的内容
     */
    public static void d(String msg) {
        if (LOG_LEVEL_DBG <= LOG_LEVEL) {
            Log.d(TAG, msg);
        }
    }
    
    /**
     * 打印等级为Error的Log
     * @param msg 设置Log的内容
     */
    public static void e(String msg) {
        if (LOG_LEVEL_ERR <= LOG_LEVEL) {
            Log.e(TAG, msg);
        }
    }
    
}
