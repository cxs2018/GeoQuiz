package com.cuixuesen.android.geoquiz;

public class Question {
    private int mTextResId;
    private boolean mAnswerTrue;

    private int mAnswered;

    public int getAnswered() {
        return mAnswered;
    }

    public void setAnswered(int answered) {
        mAnswered = answered;
    }

    public Question(int textResId, boolean answerTrue, int Answered) {
        mTextResId = textResId;
        mAnswerTrue = answerTrue;
        mAnswered = Answered;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }
}
