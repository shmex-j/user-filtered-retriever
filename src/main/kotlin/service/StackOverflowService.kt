package service

import model.ApiResponse
import model.StackOverflowUserDto
import model.TagDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface StackOverflowService {
    @GET("/2.3/users")
    fun getUsers(@Query("page") page: Long, @QueryMap options: Map<String, String>)
            : Call<ApiResponse<StackOverflowUserDto>>

    @GET("/2.3/users/{ids}/tags")
    fun getUsersTags(@Path("ids") ids : String, @Query("page") page: Long, @QueryMap options: Map<String, String>)
            : Call<ApiResponse<TagDto>>
}
