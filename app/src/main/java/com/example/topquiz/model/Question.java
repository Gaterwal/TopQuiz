package com.example.topquiz.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Question implements Parcelable {
    private String mQuestion;
    private List<String> mChoiceList;
    private int mAnswerIndex;

    public Question(String mQuestion, List<String> mChoiceList, int mAnswerIndex) {
        this.mQuestion = mQuestion;
        this.mChoiceList = mChoiceList;
        this.mAnswerIndex = mAnswerIndex;
    }
    //Parcel où on lit les données
    private Question(Parcel in)
    {
        mQuestion = in.readString();
        in.readStringList(mChoiceList);
        mAnswerIndex =  in.readInt();
    }

    public String getQuestion() {
        return mQuestion;
    }

    public List<String> getChoiceList() {
        return mChoiceList;
    }

    public int getAnswerIndex() {
        return mAnswerIndex;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mQuestion);
        dest.writeStringList(mChoiceList);
        dest.writeInt(mAnswerIndex);

    }
    public static final Parcelable.Creator<Question> CREATOR
            = new Parcelable.Creator<Question>() {
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
