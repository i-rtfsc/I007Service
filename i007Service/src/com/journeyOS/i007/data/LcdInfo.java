package com.journeyOS.i007.data;

import android.os.Parcel;

public class LcdInfo extends BaseInfo {
    public long state;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.state);
        dest.writeLong(this.factorId);
        dest.writeString(this.packageName);
    }

    public LcdInfo() {
    }

    protected LcdInfo(Parcel in) {
        super(in);
        this.state = in.readLong();
        this.factorId = in.readLong();
        this.packageName = in.readString();
    }

    public static final Creator<LcdInfo> CREATOR = new Creator<LcdInfo>() {
        @Override
        public LcdInfo createFromParcel(Parcel source) {
            return new LcdInfo(source);
        }

        @Override
        public LcdInfo[] newArray(int size) {
            return new LcdInfo[size];
        }
    };
}
