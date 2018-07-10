package me.checkin.android.helper;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by zhoujunchen
 * on 17/9/10.
 */

public class CheckInMonthly implements Parcelable {
    private List<CheckInReward> rewards;
    private int monthlySignedInDays;
    private String promText;
    private String promAction;

    public String getPromAction() {
        return promAction;
    }

    public String getPromText() {
        return promText;
    }

    public CheckInMonthly setMonthlySignedInDays(int monthlySignedInDays) {
        this.monthlySignedInDays = monthlySignedInDays;
        return this;
    }

    public int getMonthlySignedInDays() {
        return monthlySignedInDays;
    }

    public List<CheckInReward> getRewards() {
        return rewards;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.rewards);
        dest.writeInt(this.monthlySignedInDays);
    }

    public CheckInMonthly() {
    }

    protected CheckInMonthly(Parcel in) {
        this.rewards = in.createTypedArrayList(CheckInReward.CREATOR);
        this.monthlySignedInDays = in.readInt();
    }

    public static final Creator<CheckInMonthly> CREATOR = new Creator<CheckInMonthly>() {
        @Override
        public CheckInMonthly createFromParcel(Parcel source) {
            return new CheckInMonthly(source);
        }

        @Override
        public CheckInMonthly[] newArray(int size) {
            return new CheckInMonthly[size];
        }
    };
}
