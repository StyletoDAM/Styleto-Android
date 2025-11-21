package tn.esprit.labasniandroid.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tn.esprit.labasniandroid.utils.APIConstants
import java.util.concurrent.TimeUnit
import tn.esprit.labasniandroid.api.StoreApi

object RetrofitClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(APIConstants.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
    val clothesApi: ClothesApi = retrofit.create(ClothesApi::class.java)
    val outfitsApi: OutfitsApi = retrofit.create(OutfitsApi::class.java)
    val storeApi: StoreApi = retrofit.create(StoreApi::class.java)
    val chatApi: ChatApi = retrofit.create(ChatApi::class.java)
}

