package com.journeyOS.i007.data;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseInfo implements Parcelable {
    public long factorId;

    public String packageName = "";


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.factorId);
        dest.writeString(this.packageName);
    }

    public BaseInfo() {
    }

    protected BaseInfo(Parcel in) {
        this.factorId = in.readLong();
        this.packageName = in.readString();
    }

}
