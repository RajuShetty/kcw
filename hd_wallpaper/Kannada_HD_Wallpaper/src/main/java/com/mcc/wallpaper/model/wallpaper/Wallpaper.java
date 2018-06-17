package com.mcc.wallpaper.model.wallpaper;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wallpaper implements Parcelable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private Title title;
    @SerializedName("post_views")
    @Expose
    private Integer postViews;
    @SerializedName("_embedded")
    @Expose
    private Embedded embedded;

    public Wallpaper() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public Integer getPostViews() {
        return postViews;
    }

    public void setPostViews(Integer postViews) {
        this.postViews = postViews;
    }

    public Embedded getEmbedded() {
        return embedded;
    }

    public void setEmbedded(Embedded embedded) {
        this.embedded = embedded;
    }

    protected Wallpaper(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        postViews = in.readByte() == 0x00 ? null : in.readInt();
        title = (Title) in.readValue(Title.class.getClassLoader());
        embedded = (Embedded) in.readValue(Embedded.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
        if (postViews == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(postViews);
        }
        dest.writeValue(title);
        dest.writeValue(embedded);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Wallpaper> CREATOR = new Parcelable.Creator<Wallpaper>() {
        @Override
        public Wallpaper createFromParcel(Parcel in) {
            return new Wallpaper(in);
        }

        @Override
        public Wallpaper[] newArray(int size) {
            return new Wallpaper[size];
        }
    };
}

