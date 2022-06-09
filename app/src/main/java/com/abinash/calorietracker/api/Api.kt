package com.abinash.calorietracker.api

import com.abinash.calorietracker.models.CalEntry
import com.abinash.calorietracker.models.MResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*


interface Api {

    @GET("d/entries")
    fun getEntries(
        @Header("Authorization") token: String?,
        @Query("fromdate") fromdate: Long,
        @Query("todate") todate: Long
    ): Single<MResponse>


    @GET("d/adminentries")
    fun getAdminEntries(
        @Header("Authorization") token: String?
    ): Single<MResponse>


    @POST("d/entrys")
    fun postEntry(
        @Header("Authorization") token: String?,
        @Body calEntry: CalEntry?
    ): Single<MResponse>


    @PUT("d/entry/{id}")
    fun editEntry(
        @Header("Authorization") token: String?,
        @Path("id") id: String?,
        @Body calEntry: CalEntry?
    ): Single<MResponse>


    @DELETE("d/entry/{id}")
    fun deleteEntry(
        @Header("Authorization") token: String?,
        @Path("id") id: String?
    ): Single<MResponse>


    @GET("d/recentstats")
    fun getRecentStats(
        @Header("Authorization") token: String?
    ): Single<MResponse>


    @FormUrlEncoded
    @POST("d/register")
    fun registerUser(
        @Field("email") email: String?,
        @Field("fullname") fullname: String?,
        @Field("password") password: String?
    ): Single<MResponse>


    @FormUrlEncoded
    @POST("d/authenticate")
    fun authenticate(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Single<MResponse>


    @PUT("d/resetLimit/{id}/{limit}")
    fun resetLimit(
        @Header("Authorization") token: String?,
        @Path("id") id: String?,
        @Path("limit") limit: Int
    ): Single<MResponse>


    @Multipart
    @POST("d/upload-image")
    fun uploadImage(
        @Part image: MultipartBody.Part?
    ): Single<MResponse?>
}