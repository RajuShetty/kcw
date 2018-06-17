package com.mcc.wallpaper.model.wallpaper;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Embedded implements Parcelable {
    @SerializedName("wp:featuredmedia")
    @Expose
    private List<WpFeaturedmedium_> wpFeaturedmedia = null;

    public List<WpFeaturedmedium_> getWpFeaturedmedia() {
        return wpFeaturedmedia;
    }

    public Embedded() {
    }

    public void setWpFeaturedmedia(List<WpFeaturedmedium_> wpFeaturedmedia) {
        this.wpFeaturedmedia = wpFeaturedmedia;
    }

    protected Embedded(Parcel in) {
        if (in.readByte() == 0x01) {
            wpFeaturedmedia = new ArrayList<WpFeaturedmedium_>();
            in.readList(wpFeaturedmedia, WpFeaturedmedium_.class.getClassLoader());
        } else {
            wpFeaturedmedia = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (wpFeaturedmedia == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(wpFeaturedmedia);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Embedded> CREATOR = new Parcelable.Creator<Embedded>() {
        @Override
        public Embedded createFromParcel(Parcel in) {
            return new Embedded(in);
        }

        @Override
        public Embedded[] newArray(int size) {
            return new Embedded[size];
        }
    };
}
