package com.hzh.game2048.application;

import android.app.Application;

import com.hzh.game2048.utils.Constances;
import com.hzh.game2048.utils.SpUtils;

public class Config extends Application {

    /**
     * 游戏目标
     */
    public static int mGameGoal;

    /**
     * GameView行列数
     */
    public static int mGameLines;

    /**
     * Item宽高
     */
    public static int mItemSize;

    /**
     * 记录分数
     */
    public static int SCORE = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        mGameGoal = SpUtils.getInt(this, Constances.GAME_GOAL, 2048);
        mGameLines = SpUtils.getInt(this, Constances.GAME_LINES, 4);
        mItemSize = 0;
    }
}
