package com.hzh.game2048.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hzh.game2048.utils.Constances;
import com.hzh.game2048.utils.SpUtils;

public class ConfigActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mBtnGameLines;
    private Button mBtnGoal;
    private String[] mGameLinesList;
    private String[] mGameGoalList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        initView();
    }

    private void initView() {
        mBtnGameLines = findViewById(R.id.btn_game_lines);
        mBtnGoal = findViewById(R.id.btn_goal);
        Button btn_back = findViewById(R.id.btn_back);
        Button btn_done = findViewById(R.id.btn_done);
        mBtnGameLines.setText(String.valueOf(SpUtils.getInt(this, Constances.GAME_LINES, 4)));
        mBtnGoal.setText(String.valueOf(SpUtils.getInt(this, Constances.GAME_GOAL, 2048)));
        mBtnGameLines.setOnClickListener(this);
        mBtnGoal.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_done.setOnClickListener(this);
        mGameLinesList = new String[]{"4", "5", "6"};
        mGameGoalList = new String[]{"2048", "4096", "8192", "16384"};
    }

    private void saveConfig() {
        SpUtils.putInt(this, Constances.GAME_LINES, Integer.valueOf(mBtnGameLines.getText().toString()));
        SpUtils.putInt(this, Constances.GAME_GOAL, Integer.valueOf(mBtnGoal.getText().toString()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_game_lines:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("choose the lines of the game");
                builder1.setItems(mGameLinesList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBtnGameLines.setText(mGameLinesList[which]);
                    }
                }).create().show();
                break;
            case R.id.btn_goal:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle("choose the goal of the game");
                builder2.setItems(mGameGoalList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBtnGoal.setText(mGameGoalList[which]);
                    }
                }).create().show();
                break;
            case R.id.btn_back:
                this.finish();
                break;
            case R.id.btn_done:
                saveConfig();
                setResult(RESULT_OK);
                this.finish();
                break;
            default:
                break;
        }
    }
}
