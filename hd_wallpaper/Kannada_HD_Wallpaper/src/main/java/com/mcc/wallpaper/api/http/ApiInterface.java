package com.mcc.wallpaper.api.http;


import com.mcc.wallpaper.api.params.HttpParams;
import com.mcc.wallpaper.model.others.Category;
import com.mcc.wallpaper.model.others.FilterId;
import com.mcc.wallpaper.model.wallpaper.Wallpaper;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ashiq on 9/6/2017.
 */

public interface ApiInterface {

    @GET(HttpParams.API_CATEGORIES)
    Call<List<Category>> getCategories(@Query("page") int page);

    @GET(HttpParams.API_WALLPAPERS)
    Call<List<Wallpaper>> getWallpapers(@Query("categories") int categoryId, @Query("page") int page, @Query("_embed") boolean embed);

    @GET(HttpParams.API_FEATURED_ID)
    Call<List<FilterId>> getFeaturedItemsId(@Query("slug") String slug);

    @GET(HttpParams.API_FEATURED)
    Call<List<Wallpaper>> getFeaturedWallpapers(@Query("tags") int id, @Query("page") int page, @Query("_embed") boolean embed);

    @POST(HttpParams.API_INCREASE_VIEW + "/{post_id}")
    Call<Boolean> increaseViewCount(@Path("post_id") int postId);

    @GET(HttpParams.API_WALLPAPERS + "/{post_id}")
    Call<Wallpaper> getWallpaper(@Path("post_id") String postId , @Query("_embed") boolean embed);

    @GET(HttpParams.API_GIF_ID)
    Call<List<FilterId>> getGifItemsId(@Query("slug") String slug);

    @GET(HttpParams.API_SEARCH)
    Call<List<Wallpaper>> searchWallpapers(@Query("search") String query, @Query("page") int page, @Query("_embed") boolean embed);
}
