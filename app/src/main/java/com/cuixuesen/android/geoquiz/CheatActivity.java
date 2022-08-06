package com.cuixuesen.android.geoquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE = "com.cuixuesen.android.geoquiz.answer_is_true";

    private static final String EXTRA_ANSWER_SHOWN = "com.cuixuesen.android.geoquiz.answer_shown";

    private static final String EXTRA_IS_CHEATED = "com.cuixuesen.android.geoquiz.is_cheated";

    private static final String EXTRA_ANSWER_TEXT = "com.cuixuesen.android.geoquiz.answer_text";

    private static final String EXTRA_ANSWER_TEXT_ID = "com.cuixuesen.android.geoquiz.answer_text_id";

    private static final String EXTRA_ANSWER_TEXT_ID_FOR_INTENT = "com.cuixuesen.android.geoquiz.answer_text_id_for_intent";

    private boolean mAnswerIsTrue;

    private TextView mAnswerTextView;
    private Button mShowAnswerButton;

    private boolean mIsAnswerShown = false;

    private int mAnswerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        // 有个问题，如果已经偷看过某道题的答案，退出CheatActivity，状态就没了，没有做持久存储
        // 添加了生命周期存储，在app运行过程中，存储在QuizActivity主屏幕bundle中

        // 提前获取答案文本按钮，因为旋转后如果之前显示过答案，需要再设置一下text
        mAnswerTextView = (TextView)findViewById(R.id.answer_text_view);

        if (savedInstanceState != null) {
            mIsAnswerShown = savedInstanceState.getBoolean(EXTRA_IS_CHEATED, false);
            setAnswerShownResult(mIsAnswerShown);
            mAnswerText = savedInstanceState.getInt(EXTRA_ANSWER_TEXT);
            mAnswerTextView.setText(mAnswerText);
        }

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        // 获取答案id，这个是如果之前偷看过某个问题的答案，会把答案id传回去QuizActivity，然后再进来会带过来答案id，如果非0说明之前显示过（偷看过），就把
        // 把答案id对应的答案内容显示出来
        mAnswerText = getIntent().getIntExtra(EXTRA_ANSWER_TEXT_ID_FOR_INTENT, 0);
        if (mAnswerText != 0) {
            mAnswerTextView.setText(mAnswerText);
        }

        mShowAnswerButton = (Button)findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAnswerIsTrue) {
                    mAnswerTextView.setText(R.string.true_button);
                    mAnswerText = R.string.true_button;
                } else {
                    mAnswerTextView.setText(R.string.false_button);
                    mAnswerText = R.string.false_button;
                }
                mIsAnswerShown = true;
                setAnswerShownResult(mIsAnswerShown);
            }
        });
    }

    public static Intent newIntent(Context packageContext, boolean answerIsTrue, int id) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(EXTRA_ANSWER_TEXT_ID_FOR_INTENT, id);
        return intent;
    }

    // 返回给QuizActivity数据
    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        data.putExtra(EXTRA_ANSWER_TEXT_ID, mAnswerText);
        setResult(RESULT_OK, data);
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    // 获取答案对应的id，给QuizActivity使用
    public static int getAnswerTextId(Intent result) {
        return result.getIntExtra(EXTRA_ANSWER_TEXT_ID, 0);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // 存储是否看了答案 solve=》用户作弊后，可以旋转CheatActivity来清除作弊痕迹
        outState.putBoolean(EXTRA_IS_CHEATED, mIsAnswerShown);
        // 存储显示的答案内容 solve=》自己新增的功能，旋转后可以仍然显示答案
        outState.putInt(EXTRA_ANSWER_TEXT, mAnswerText);
    }
}