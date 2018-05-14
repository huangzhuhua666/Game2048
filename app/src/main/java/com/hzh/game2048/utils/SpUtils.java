package com.hzh.game2048.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharePreference工具类
 */
public class SpUtils {

    private static SharedPreferences sp;

    /**
     * 放置boolean值到SharePreferences中
     * @param context 上下文环境
     * @param key 键
     * @param value 值
     */
    public static void putBoolean(Context context, String key, boolean value) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).apply();
    }

    /**
     * 获取SharePreferences中的boolean值
     * @param context 上下文环境
     * @param key 键
     * @param defaultValue 默认值
     * @return 返回获取到的值
     */
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defaultValue);
    }

    /**
     * 放置String值到SharePreferences中
     * @param context 上下文环境
     * @param key 键
     * @param value 值
     */
    public static void putString(Context context, String key, String value){
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).apply();
    }

    /**
     * 获取SharePreferences中的String值
     * @param context 上下文环境
     * @param key 键
     * @param defaultValue 默认值
     * @return 返回获取到的值
     */
    public static String getString(Context context, String key, String defaultValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key, defaultValue);
    }

    /**
     * 删除SharePreferences中的key值
     * @param context 上下文环境
     * @param key 键
     */
    public static void removeKey(Context context, String key){
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().remove(key).apply();
    }

    /**
     * 放置Int值到SharePreferences中
     * @param context 上下文环境
     * @param key 键
     * @param value 值
     */
    public static void putInt(Context context, String key, int value){
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, value).apply();
    }

    /**
     * 获取SharePreferences中的Int值
     * @param context 上下文环境
     * @param key 键
     * @param defaultValue 默认值
     * @return 返回获取到的值
     */
    public static int getInt(Context context, String key, int defaultValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getInt(key, defaultValue);
    }
}
