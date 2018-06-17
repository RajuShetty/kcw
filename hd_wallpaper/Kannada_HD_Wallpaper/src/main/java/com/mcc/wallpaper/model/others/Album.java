package com.mcc.wallpaper.model.others;

import android.os.Parcel;
import android.os.Parcelable;

public class Album implements Parcelable {
    private String folderName;
    private String imagePath;
    private int imageCount;

    public Album(String folderName, String imagePath, int imageCount) {
        this.folderName = folderName;
        this.imagePath = imagePath;
        this.imageCount = imageCount;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    protected Album(Parcel in) {
        folderName = in.readString();
        imagePath = in.readString();
        imageCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(folderName);
        dest.writeString(imagePath);
        dest.writeInt(imageCount);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
}
