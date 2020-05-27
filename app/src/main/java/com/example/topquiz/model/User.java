package com.example.topquiz.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String mFirstName;
    private int score;
    private int lastScore = 0;
    private int bestScore = 0;

    public int getLastScore() {
        return lastScore;
    }

    public User(String firstName) {
        mFirstName = firstName;
    }

    private User(Parcel in) {
        mFirstName = in.readString();
        score = in.readInt();
        lastScore = in.readInt();
        bestScore = in.readInt();
    }

    public void saveScores() {
        this.lastScore = score;
        if(score > bestScore)
        {
            bestScore = score;
        }
        score = 0;
    }

    public int getBestScore() {
        return bestScore;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }

    };


    public String getFirstName() {
        return mFirstName;
    }

    public int getScore() {
        return score;
    }


    public void incrementScore() {
        score++;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFirstName);
        dest.writeInt(score);
        dest.writeInt(lastScore);
        dest.writeInt(bestScore);
    }

}
