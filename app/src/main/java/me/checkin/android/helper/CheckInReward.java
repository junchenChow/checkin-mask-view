package me.checkin.android.helper;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by zhoujunchen
 * on 16/11/17.
 */

public class CheckInReward implements Parcelable {
    private String id;
    private String introPic;
    private String name;
    private String price;
    private int points;
    private int inventory;
    private String link;
    private boolean redeemed;
    private int progress;
    private String type;

    public String getType() {
        return type;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public void setRedeemed(boolean redeemed) {
        this.redeemed = redeemed;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public String getId() {
        return TextUtils.isEmpty(id) ? "" : id;
    }

    public String getIntroPic() {
        return introPic;
    }

    public String getName() {
        return TextUtils.isEmpty(name) ? "" : name;
    }

    public String getPrice() {
        return price;
    }

    public int getPoints() {
        return points;
    }

    public int getInventory() {
        return inventory;
    }

    public String getLink() {
        return link;
    }

    public CheckInReward() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.introPic);
        dest.writeString(this.name);
        dest.writeString(this.price);
        dest.writeInt(this.points);
        dest.writeInt(this.inventory);
        dest.writeString(this.link);
        dest.writeByte(this.redeemed ? (byte) 1 : (byte) 0);
        dest.writeInt(this.progress);
        dest.writeString(this.type);
    }

    protected CheckInReward(Parcel in) {
        this.id = in.readString();
        this.introPic = in.readString();
        this.name = in.readString();
        this.price = in.readString();
        this.points = in.readInt();
        this.inventory = in.readInt();
        this.link = in.readString();
        this.redeemed = in.readByte() != 0;
        this.progress = in.readInt();
        this.type = in.readString();
    }

    public static final Creator<CheckInReward> CREATOR = new Creator<CheckInReward>() {
        @Override
        public CheckInReward createFromParcel(Parcel source) {
            return new CheckInReward(source);
        }

        @Override
        public CheckInReward[] newArray(int size) {
            return new CheckInReward[size];
        }
    };
}
