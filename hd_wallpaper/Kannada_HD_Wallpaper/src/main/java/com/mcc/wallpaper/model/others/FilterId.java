package com.mcc.wallpaper.model.others;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FilterId {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("count")
    @Expose
    private Integer count;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
