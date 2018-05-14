package com.hzh.game2048.bean;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hzh.game2048.utils.Constances;
import com.hzh.game2048.utils.SpUtils;

public class GameItem extends FrameLayout {

    //Item显示的数字
    private int mCardNum;
    //显示数字的TextView
    private TextView mTvNum;

    public GameItem(@NonNull Context context, int cardNum) {
        super(context);
        mCardNum = cardNum;
        initCardItem();
    }

    /**
     * 初始化Item
     */
    private void initCardItem() {
        //设置面板背景颜色
        setBackgroundColor(Color.GRAY);
        mTvNum = new TextView(getContext());
        setNum(mCardNum);
        int gameLines = SpUtils.getInt(getContext(), Constances.GAME_LINES, 4);
        //修改字体大小
        if (gameLines == 4) {
            mTvNum.setTextSize(35);
        } else if (gameLines == 5) {
            mTvNum.setTextSize(25);
        } else {
            mTvNum.setTextSize(20);
        }
        //设置字体为粗体
        TextPaint paint = mTvNum.getPaint();
        paint.setFakeBoldText(true);
        mTvNum.setGravity(Gravity.CENTER);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.setMargins(5, 5, 5, 5);
        addView(mTvNum, params);
    }

    public int getNum() {
        return mCardNum;
    }

    public View getItemView() {
        return mTvNum;
    }

    public void setNum(int num) {
        mCardNum = num;
        if (num == 0) {
            mTvNum.setText("");
        } else {
            mTvNum.setText(String.valueOf(num));
        }
        //设置背景颜色
        switch (num) {
            case 0:
                mTvNum.setBackgroundColor(0x00000000);
                break;
            case 2:
                mTvNum.setBackgroundColor(0xffeee5db);
                break;
            case 4:
                mTvNum.setBackgroundColor(0xffeee0ca);
                break;
            case 8:
                mTvNum.setBackgroundColor(0xfff2c17a);
                break;
            case 16:
                mTvNum.setBackgroundColor(0xfff59667);
                break;
            case 32:
                mTvNum.setBackgroundColor(0xfff68c6f);
                break;
            case 64:
                mTvNum.setBackgroundColor(0xfff66e3c);
                break;
            case 128:
                mTvNum.setBackgroundColor(0xffedcf74);
                break;
            case 256:
                mTvNum.setBackgroundColor(0xffedcc64);
                break;
            case 512:
                mTvNum.setBackgroundColor(0xffedc854);
                break;
            case 1024:
                mTvNum.setBackgroundColor(0xffedc54f);
                break;
            case 2048:
                mTvNum.setBackgroundColor(0xffedc32e);
                break;
            default:
                mTvNum.setBackgroundColor(0xff3c4a34);
                break;
        }
    }
}
