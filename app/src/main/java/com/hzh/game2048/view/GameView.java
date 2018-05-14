package com.hzh.game2048.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.GridLayout;

import com.hzh.game2048.activity.MainActivity;
import com.hzh.game2048.application.Config;
import com.hzh.game2048.bean.GameItem;
import com.hzh.game2048.utils.Constances;
import com.hzh.game2048.utils.ScreenUtils;
import com.hzh.game2048.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

public class GameView extends GridLayout implements View.OnTouchListener {

    //GameView对应矩阵
    private GameItem[][] mGameMatrix;
    //空格的List
    private List<Point> mBlanks;
    //矩阵行列数
    private int mGameLines;
    //历史记录分数
    private int mScoreHistory;
    //最高记录
    private int mHighScore;
    //历史矩阵
    private int[][] mGameMatrixHistory;
    //辅助数组
    private List<Integer> mCallList;
    private int mKeyItemNum = -1;
    //记录坐标
    private int mStartX;
    private int mStartY;
    //目标分数
    private int mTarget;

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initGameMatrix();
    }

    /**
     * 开始游戏
     */
    public void startGame() {
        initGameMatrix();
    }

    /**
     * 初始化View
     */
    private void initGameMatrix() {
        mTarget = SpUtils.getInt(getContext(), Constances.GAME_GOAL, 2048);
        //初始化矩阵
        removeAllViews();
        mScoreHistory = 0;
        Config.SCORE = 0;
        Config.mGameLines = SpUtils.getInt(getContext(), Constances.GAME_LINES, 4);
        mGameLines = Config.mGameLines;
        mGameMatrix = new GameItem[mGameLines][mGameLines];
        mGameMatrixHistory = new int[mGameLines][mGameLines];
        mBlanks = new ArrayList<>();
        mCallList = new ArrayList<>();
        mHighScore = SpUtils.getInt(getContext(), Constances.HIGH_SCORE, 0);
        setColumnCount(mGameLines);
        setRowCount(mGameLines);
        setOnTouchListener(this);
        //初始化View参数
        Config.mItemSize = ScreenUtils.getScreenSize(getContext()).widthPixels / Config.mGameLines;
        initGameView(Config.mItemSize);
    }

    /**
     * 初始化GameView
     *
     * @param cardSize itemSize
     */
    private void initGameView(int cardSize) {
        removeAllViews();
        GameItem item;
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                item = new GameItem(getContext(), 0);
                addView(item, cardSize, cardSize);
                //初始化GameMatrix全部为0，空格放到mBlanks中
                mGameMatrix[i][j] = item;
                mBlanks.add(new Point(i, j));
            }
        }
        //添加随机数字
        addRandomNum();
        addRandomNum();
    }

    /**
     * 添加随机数字
     */
    private void addRandomNum() {
        getBlanks();
        if (mBlanks.size() > 0) {
            int random = (int) (Math.random() * mBlanks.size());
            Point randomPoint = mBlanks.get(random);
            mGameMatrix[randomPoint.x][randomPoint.y].setNum(Math.random() > 0.2d ? 2 : 4);
            animCreate(mGameMatrix[randomPoint.x][randomPoint.y]);
        }
    }

    /**
     * 获取空格的坐标，并存储在mBlanks中
     */
    private void getBlanks() {
        mBlanks.clear();
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrix[i][j].getNum() == 0) {
                    mBlanks.add(new Point(i, j));
                }
            }
        }
    }

    /**
     * 保存历史记录
     */
    private void saveHistoryMatrix() {
        mScoreHistory = Config.SCORE;
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                mGameMatrixHistory[i][j] = mGameMatrix[i][j].getNum();
            }
        }
    }

    /**
     * 检测滑动的方向
     *
     * @param offsetX X轴的偏移量
     * @param offsetY Y轴的偏移量
     */
    private void judgeDirection(int offsetX, int offsetY) {
        //最小滑动距离
        int slideDis = ScreenUtils.dp2px(getContext(), 5);
        //滑动超过maxDis触发特殊事件
        int maxDis = ScreenUtils.dp2px(getContext(), 200);
        boolean flagNormal = Math.abs(offsetX) > slideDis || Math.abs(offsetY) > slideDis
                && Math.abs(offsetX) < maxDis && Math.abs(offsetY) < maxDis;
        boolean flagSuper = Math.abs(offsetX) > maxDis || Math.abs(offsetY) > maxDis;
        if (flagNormal && !flagSuper) {//没有触发特殊事件
            if (Math.abs(offsetX) > Math.abs(offsetY)) {//横向滑动
                if (offsetX > slideDis) {
                    //向右滑动
                    swipeRight();
                } else {
                    //向左滑动
                    swipeLeft();
                }
            } else {//纵向滑动
                if (offsetY > slideDis) {
                    //向下滑动
                    swipeDown();
                } else {
                    //向上滑动
                    swipeUp();
                }
            }
        } else if (flagSuper) {//触发特殊时间，启动超级用户权限来添加自定义数字
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final EditText et = new EditText(getContext());
            builder.setTitle("Back Door")
                    .setView(et)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!TextUtils.isEmpty(et.getText())) {
                                addSuperNum(Integer.parseInt(et.getText().toString()));
                                //判断游戏是否结束
                                checkCompleted();
                            }
                        }
                    })
                    .setNegativeButton("ByeBye", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
    }

    /**
     * super模式下添加一个数字
     *
     * @param num 要添加的数字
     */
    private void addSuperNum(int num) {
        if (checkSuperNum(num)) {
            getBlanks();
            if (mBlanks.size() > 0) {
                int random = (int) (Math.random() * mBlanks.size());
                Point randomPoint = mBlanks.get(random);
                mGameMatrix[randomPoint.x][randomPoint.y].setNum(num);
                animCreate(mGameMatrix[randomPoint.x][randomPoint.y]);
            }
        }
    }

    /**
     * 检查添加的数是否指定的数
     *
     * @param num 要添加的数
     * @return true：是  false：不是
     */
    private boolean checkSuperNum(int num) {
        return num == 2 || num == 4 || num == 8 || num == 16 || num == 32
                || num == 64 || num == 128 || num == 256 || num == 512 || num == 1024;
    }

    /**
     * 生成的动画
     *
     * @param item item
     */
    private void animCreate(GameItem item) {
        ScaleAnimation scale = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(100);
        item.setAnimation(null);
        item.getItemView().startAnimation(scale);
    }

    /**
     * 向左滑动
     */
    private void swipeLeft() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                int currentNum = mGameMatrix[i][j].getNum();
                if (currentNum != 0) {//获取的数字不是0
                    if (mKeyItemNum == -1) {//标志是否已经合并过一次
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {//数字相同，合并，添加到mCallList
                            mCallList.add(mKeyItemNum * 2);
                            Config.SCORE += mKeyItemNum * 2;
                            mKeyItemNum = -1;
                        } else {//数字不同，把上一个获取的数据添加的mCallList
                            mCallList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                }
            }
            if (mKeyItemNum != -1) {//把最后获取的数字添加到mCallList
                mCallList.add(mKeyItemNum);
            }
            //改变Item值
            for (int j = 0; j < mCallList.size(); j++) {
                mGameMatrix[i][j].setNum(mCallList.get(j));
            }
            for (int m = mCallList.size(); m < mGameLines; m++) {
                mGameMatrix[i][m].setNum(0);
            }
            //重置行参数
            mKeyItemNum = -1;
            mCallList.clear();
        }
    }

    /**
     * 向右滑动
     */
    private void swipeRight() {
        for (int i = mGameLines - 1; i >= 0; i--) {
            for (int j = mGameLines - 1; j >= 0; j--) {
                int currentNum = mGameMatrix[i][j].getNum();
                if (currentNum != 0) {//获取的数字不是0
                    if (mKeyItemNum == -1) {//标志是否已经合并过一次
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {//数字相同，合并，添加到mCallList
                            mCallList.add(mKeyItemNum * 2);
                            Config.SCORE += mKeyItemNum * 2;
                            mKeyItemNum = -1;
                        } else {//数字不同，把上一个获取的数据添加的mCallList
                            mCallList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                }
            }
            if (mKeyItemNum != -1) {//把最后获取的数字添加到mCallList
                mCallList.add(mKeyItemNum);
            }
            //改变Item值
            for (int j = 0; j < mGameLines - mCallList.size(); j++) {
                mGameMatrix[i][j].setNum(0);
            }
            int index = mCallList.size() - 1;
            for (int m = mGameLines - mCallList.size(); m < mGameLines; m++) {
                mGameMatrix[i][m].setNum(mCallList.get(index));
                index--;
            }
            //重置行参数
            mKeyItemNum = -1;
            mCallList.clear();
        }
    }

    /**
     * 向上滑动
     */
    private void swipeUp() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                int currentNum = mGameMatrix[j][i].getNum();
                if (currentNum != 0) {//获取的数字不是0
                    if (mKeyItemNum == -1) {//标志是否已经合并过一次
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {//数字相同，合并，添加到mCallList
                            mCallList.add(mKeyItemNum * 2);
                            Config.SCORE += mKeyItemNum * 2;
                            mKeyItemNum = -1;
                        } else {//数字不同，把上一个获取的数据添加的mCallList
                            mCallList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                }
            }
            if (mKeyItemNum != -1) {//把最后获取的数字添加到mCallList
                mCallList.add(mKeyItemNum);
            }
            //改变Item值
            for (int j = 0; j < mCallList.size(); j++) {
                mGameMatrix[j][i].setNum(mCallList.get(j));
            }
            for (int m = mCallList.size(); m < mGameLines; m++) {
                mGameMatrix[m][i].setNum(0);
            }
            //重置列参数
            mKeyItemNum = -1;
            mCallList.clear();
        }
    }

    /**
     * 向下滑动
     */
    private void swipeDown() {
        for (int i = mGameLines - 1; i >= 0; i--) {
            for (int j = mGameLines - 1; j >= 0; j--) {
                int currentNum = mGameMatrix[j][i].getNum();
                if (currentNum != 0) {//获取的数字不是0
                    if (mKeyItemNum == -1) {//标志是否已经合并过一次
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {//数字相同，合并，添加到mCallList
                            mCallList.add(mKeyItemNum * 2);
                            Config.SCORE += mKeyItemNum * 2;
                            mKeyItemNum = -1;
                        } else {//数字不同，把上一个获取的数据添加的mCallList
                            mCallList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                }
            }
            if (mKeyItemNum != -1) {//把最后获取的数字添加到mCallList
                mCallList.add(mKeyItemNum);
            }
            //改变Item值
            for (int j = 0; j < mGameLines - mCallList.size(); j++) {
                mGameMatrix[j][i].setNum(0);
            }
            int index = mCallList.size() - 1;
            for (int m = mGameLines - mCallList.size(); m < mGameLines; m++) {
                mGameMatrix[m][i].setNum(mCallList.get(index));
                index--;
            }
            //重置列参数
            mKeyItemNum = -1;
            mCallList.clear();
        }
    }

    /**
     * 是否移动过了
     *
     * @return true：移动过了  false：没移动过
     */
    private boolean isMove() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrixHistory[i][j] != mGameMatrix[i][j].getNum()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断游戏是否结束
     */
    private void checkCompleted() {
        int result = checkNum();
        if (result == 0) {//游戏失败
            if (Config.SCORE > mHighScore) {//记录最高分数
                SpUtils.putInt(getContext(), Constances.HIGH_SCORE, Config.SCORE);
                MainActivity.getMainActivity().setScore(Config.SCORE, 1);
                Config.SCORE = 0;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Game Over")
                    .setPositiveButton("Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //重新开始
                            startGame();
                            MainActivity.getMainActivity().setScore(Config.SCORE, 0);
                        }
                    }).create().show();
            Config.SCORE = 0;
        } else if (result == 2) {//游戏成功
            if (Config.SCORE > mHighScore) {//记录最高分数
                SpUtils.putInt(getContext(), Constances.HIGH_SCORE, Config.SCORE);
                MainActivity.getMainActivity().setScore(Config.SCORE, 1);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Mission Accomplished")
                    .setPositiveButton("Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //重新开始
                            Config.SCORE = 0;
                            startGame();
                            MainActivity.getMainActivity().setScore(Config.SCORE, 0);
                        }
                    })
                    .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //继续游戏，修改目标
                            if (mTarget == 2048) {
                                SpUtils.putInt(getContext(), Constances.GAME_GOAL, 4096);
                                mTarget = 4096;
                            } else if (mTarget == 4096) {
                                SpUtils.putInt(getContext(), Constances.GAME_GOAL, 8192);
                                mTarget = 8192;
                            } else if (mTarget == 8192) {
                                SpUtils.putInt(getContext(), Constances.GAME_GOAL, 16384);
                                mTarget = 16382;
                            }
                            MainActivity.getMainActivity().setGoal(mTarget);
                        }
                    }).create().show();
        }
    }

    /**
     * 检测所有数字，看是否满足可以移动的条件
     *
     * @return 0：游戏失败  1：游戏正常  2：游戏成功
     */
    private int checkNum() {
        getBlanks();
        if (mBlanks.size() == 0) {
            for (int i = 0; i < mGameLines; i++) {
                for (int j = 0; j < mGameLines; j++) {
                    if (j < mGameLines - 1) {
                        if (mGameMatrix[i][j].getNum() == mGameMatrix[i][j + 1].getNum()) {
                            return 1;
                        }
                    }
                    if (i < mGameLines - 1) {
                        if (mGameMatrix[i][j].getNum() == mGameMatrix[i + 1][j].getNum()) {
                            return 1;
                        }
                    }
                }
            }
            return 0;
        }
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrix[i][j].getNum() == mTarget) {
                    return 2;
                }
            }
        }
        return 1;
    }

    /**
     * 撤销上一次移动
     */
    public void revertGame() {
        //第一次不能撤销
        int sum = 0;
        for (int[] element : mGameMatrixHistory) {
            for (int i : element) {
                sum += i;
            }
        }
        if (sum != 0) {
            MainActivity.getMainActivity().setScore(mScoreHistory, 0);
            Config.SCORE = mScoreHistory;
            for (int i = 0; i < mGameLines; i++) {
                for (int j = 0; j < mGameLines; j++) {
                    mGameMatrix[i][j].setNum(mGameMatrixHistory[i][j]);
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //保存记录
                saveHistoryMatrix();
                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int endX = (int) event.getX();
                int endY = (int) event.getY();
                judgeDirection(endX - mStartX, endY - mStartY);
                if (isMove()) {//检测是否移动了
                    //添加一个随机数
                    addRandomNum();
                    //修改显示分数
                    MainActivity.getMainActivity().setScore(Config.SCORE, 0);
                }
                //判断游戏是否结束
                checkCompleted();
                break;
            default:
                break;
        }
        return true;
    }
}
