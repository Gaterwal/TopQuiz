package com.example.topquiz.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collections;
import java.util.List;

public class QuestionBank implements Parcelable {
    private List<Question> mQuestionList;
    private int mNextQuestionIndex;

    public QuestionBank(List<Question> questionList)
    {
        mQuestionList = questionList;

        Collections.shuffle(mQuestionList);

        mNextQuestionIndex = 0;
    }

    private QuestionBank(Parcel in) {
        //Crée une liste de questions
        mQuestionList = in.readArrayList(Question.class.getClassLoader());
        mNextQuestionIndex = in.readInt();

    }

    public Question getQuestion()
    {
        if(mNextQuestionIndex == mQuestionList.size())
        {
            mNextQuestionIndex = 0;
        }
        return mQuestionList.get(mNextQuestionIndex++);
    }

    public int indexOf(Question question)
    {
        return mQuestionList.indexOf(question);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    //Ecriture de Parcel (container de primitifs
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(mQuestionList);
        dest.writeInt(mNextQuestionIndex);

    }
    public static final Parcelable.Creator<QuestionBank> CREATOR
            = new Parcelable.Creator<QuestionBank>() {
        public QuestionBank createFromParcel(Parcel in) {
            return new QuestionBank(in);
        }

        public QuestionBank[] newArray(int size) {
            return new QuestionBank[size];
        }
    };
}
