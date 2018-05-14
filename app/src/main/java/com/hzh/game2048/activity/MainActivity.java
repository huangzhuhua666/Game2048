package com.hzh.game2048.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzh.game2048.application.Config;
import com.hzh.game2048.utils.Constances;
import com.hzh.game2048.utils.SpUtils;
import com.hzh.game2048.view.GameView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Activity的引用
    private static MainActivity mGame;
    //记录分数
    private TextView mTvScore;
    //历史记录分数
    private TextView mTvHighScore;
    //目标分数
    private TextView mTvGoal;
    private int mGoal;
    private GameView mGameView;
    private int mHighScore;

    public MainActivity() {
        mGame = this;
    }

    /**
     * 获取当前Activity的引用
     *
     * @return MainActivity.this
     */
    public static MainActivity getMainActivity() {
        return mGame;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Button btn_revert = findViewById(R.id.btn_revert);
        btn_revert.setOnClickListener(this);

        Button btn_restart = findViewById(R.id.btn_restart);
        btn_restart.setOnClickListener(this);

        Button btn_options = findViewById(R.id.btn_options);
        btn_options.setOnClickListener(this);

        mTvScore = findViewById(R.id.tv_score);
        mTvScore.setText(String.valueOf(0));

        mTvHighScore = findViewById(R.id.tv_record);
        mHighScore = SpUtils.getInt(this, Constances.HIGH_SCORE, 0);
        mTvHighScore.setText(String.valueOf(mHighScore));

        mTvGoal = findViewById(R.id.tv_goal);
        mGoal = SpUtils.getInt(this, Constances.GAME_GOAL, 2048);
        mTvGoal.setText(String.valueOf(mGoal));

        mGameView = new GameView(this);
        // 为了GameView能居中
        RelativeLayout relativeLayout = findViewById(R.id.game_panel_rl);
        relativeLayout.addView(mGameView);
    }

    /**
     * 修改目标
     *
     * @param goal 目标
     */
    public void setGoal(int goal) {
        mTvGoal.setText(String.valueOf(goal));
    }

    /**
     * 修改得分
     *
     * @param score score
     * @param flag  0 ：score  1：high score
     */
    public void setScore(int score, int flag) {
        switch (flag) {
            case 0:
                mTvScore.setText(String.valueOf(score));
                break;
            case 1:
                mTvHighScore.setText(String.valueOf(score));
                break;
            default:
                break;
        }
    }

    /**
     * 获取最高记录
     */
    private void getHighScore() {
        int score = SpUtils.getInt(this, Constances.HIGH_SCORE, 0);
        setScore(score, 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_revert:
                mGameView.revertGame();
                break;
            case R.id.btn_restart:
                mGameView.startGame();
                break;
            case R.id.btn_options:
                startActivityForResult(new Intent(MainActivity.this, ConfigActivity.class),
                        0);
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mGoal = SpUtils.getInt(this, Constances.GAME_GOAL, 2048);
            mTvGoal.setText(String.valueOf(mGoal));
            getHighScore();
            mGameView.startGame();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Config.SCORE > mHighScore) {
            SpUtils.putInt(this, Constances.HIGH_SCORE, Config.SCORE);
        }
    }
}
