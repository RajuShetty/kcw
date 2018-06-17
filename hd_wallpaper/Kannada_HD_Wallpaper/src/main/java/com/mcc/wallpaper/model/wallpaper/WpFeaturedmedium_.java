package com.mcc.wallpaper.model.wallpaper;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WpFeaturedmedium_ implements Parcelable {
    @SerializedName("source_url")
    @Expose
    private String sourceUrl;

    public WpFeaturedmedium_() {
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    protected WpFeaturedmedium_(Parcel in) {
        sourceUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sourceUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<WpFeaturedmedium_> CREATOR = new Parcelable.Creator<WpFeaturedmedium_>() {
        @Override
        public WpFeaturedmedium_ createFromParcel(Parcel in) {
            return new WpFeaturedmedium_(in);
        }

        @Override
        public WpFeaturedmedium_[] newArray(int size) {
            return new WpFeaturedmedium_[size];
        }
    };
}
