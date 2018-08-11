package com.journeyOS.i007.data;

import android.os.Parcel;
import android.os.Parcelable;

public class BatteryInfo extends BaseInfo implements Parcelable {
    public int level;

    public int status;

    public int health;

    public float temperature;

    public boolean pluggedIn;

    public boolean charging;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.level);
        dest.writeInt(this.status);
        dest.writeInt(this.health);
        dest.writeFloat(this.temperature);
        dest.writeByte(this.pluggedIn ? (byte) 1 : (byte) 0);
        dest.writeByte(this.charging ? (byte) 1 : (byte) 0);
        dest.writeLong(this.factorId);
        dest.writeString(this.packageName);
    }

    public BatteryInfo() {
    }

    protected BatteryInfo(Parcel in) {
        super(in);
        this.level = in.readInt();
        this.status = in.readInt();
        this.health = in.readInt();
        this.temperature = in.readFloat();
        this.pluggedIn = in.readByte() != 0;
        this.charging = in.readByte() != 0;
        this.factorId = in.readLong();
        this.packageName = in.readString();
    }

    public static final Creator<BatteryInfo> CREATOR = new Creator<BatteryInfo>() {
        @Override
        public BatteryInfo createFromParcel(Parcel source) {
            return new BatteryInfo(source);
        }

        @Override
        public BatteryInfo[] newArray(int size) {
            return new BatteryInfo[size];
        }
    };
}
