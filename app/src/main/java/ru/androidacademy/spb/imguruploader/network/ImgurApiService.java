package ru.androidacademy.spb.imguruploader.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author Artur Vasilov
 */
public interface ImgurApiService {

    @POST("/3/image")
    Call<ResponseBody> uploadImage(@Header("Authorization") String auth,
                                   @Query("album") String albumId,
                                   @Body RequestBody image);
}
