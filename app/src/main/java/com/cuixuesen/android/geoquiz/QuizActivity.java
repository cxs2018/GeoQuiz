package com.cuixuesen.android.geoquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.DialogTitle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";

    private static final String KEY_INDEX = "index";

    private static final String KEY_ANSWER = "answer";

    private static final String KEY_COUNT = "count";

    private static final String KEY_DONE = "done";

    private static final String KEY_IS_CHEATED = "com.cuixuesen.android.geoquiz.quiz_is_cheated";

    private static final String KEY_IDS = "com.cuixuesen.android.geoquiz.quiz_ids";

    private Button mTrueButton;

    private Button mFalseButton;

    private ImageButton mNextButton;

    private ImageButton mPrevButton;

    private Button mCheatButton;

    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true, 0),
            new Question(R.string.question_oceans, true, 0),
            new Question(R.string.question_mideast, false, 0),
            new Question(R.string.question_africa, false, 0),
            new Question(R.string.question_americas, true, 0),
            new Question(R.string.question_asia, true, 0)
    };

    private int mCurrentIndex = 0;

    private static final int REQUEST_CODE_CHEAT = 0;

    private boolean mIsCheater;

    // 使用数组存储每个问题的答案是否偷看过 solve=》用户可以不断单击NEXT按钮，跳过偷看过答案的问题，从而使作弊记录丢失
    // + solve=》作弊返回后，用户可以旋转QuizActivity来清除mIsCheater变量值
    private boolean[] mQuestionCheated = new boolean[]{false, false, false, false, false, false};

    // 使用数组来存储每个问题的答案id，以传给CheatActivity显示答案
    private int[] mQuestionIds = new int[]{0, 0, 0, 0, 0, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Log.d(TAG, "onCreate: called");

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            int[] answerList = savedInstanceState.getIntArray(KEY_ANSWER);
            for (int i = 0; i < mQuestionBank.length; i++) {
                mQuestionBank[i].setAnswered(answerList[i]);
            }
            correctCount = savedInstanceState.getInt(KEY_COUNT, 0);
            questionDoneCount = savedInstanceState.getInt(KEY_DONE, 0);
            mQuestionCheated = savedInstanceState.getBooleanArray(KEY_IS_CHEATED);
            mQuestionIds = savedInstanceState.getIntArray(KEY_IDS);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
            }
        });
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempIndex = mCurrentIndex;
                if (tempIndex == 0) {
                    tempIndex = mQuestionBank.length;
                }
                mCurrentIndex = tempIndex - 1;
                updateQuestion();
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Log.d(TAG, "" + mQuestionIds[mCurrentIndex]);
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue, mQuestionIds[mCurrentIndex]);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        updateQuestion();
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        if (mQuestionBank[mCurrentIndex].getAnswered() == 0) {
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
        } else {
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        }
    }

    private int questionDoneCount = 0;

    private int correctCount = 0;

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if (mQuestionCheated[mCurrentIndex]) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                correctCount++;
                mQuestionBank[mCurrentIndex].setAnswered(1);
            } else {
                messageResId = R.string.incorrect_toast;
                mQuestionBank[mCurrentIndex].setAnswered(-1);
            }
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        }


        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();

        questionDoneCount++;

        if (questionDoneCount == mQuestionBank.length) {
            NumberFormat percent = NumberFormat.getPercentInstance();
            percent.setMaximumFractionDigits(2);

            double per = new BigDecimal((float) correctCount / questionDoneCount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            Toast.makeText(this, "恭喜你答完了所有题目，评分是 " + correctCount + "/" + questionDoneCount + " = " + percent.format(per), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState: ");
        outState.putInt(KEY_INDEX, mCurrentIndex);
        int[] answerList = new int[mQuestionBank.length];
        for (int i = 0; i < answerList.length; i++) {
            answerList[i] = mQuestionBank[i].getAnswered();
        }
        outState.putIntArray(KEY_ANSWER, answerList);
        outState.putInt(KEY_COUNT, correctCount);
        outState.putInt(KEY_DONE, questionDoneCount);
        outState.putBooleanArray(KEY_IS_CHEATED, mQuestionCheated);
        outState.putIntArray(KEY_IDS, mQuestionIds);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mQuestionCheated[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
            mQuestionIds[mCurrentIndex] = CheatActivity.getAnswerTextId(data);
        }
    }
}