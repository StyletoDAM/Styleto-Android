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

    // Client standard avec timeout de 30 secondes
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Client avec timeout étendu pour les recommandations (2.5 minutes)
    // Le backend peut prendre jusqu'à 2 minutes pour exécuter le script Python ML
    private val okHttpClientExtended = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(150, TimeUnit.SECONDS) // 2.5 minutes pour les recommandations ML
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(APIConstants.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Retrofit avec timeout étendu pour les recommandations
    private val retrofitExtended = Retrofit.Builder()
        .baseUrl(APIConstants.BASE_URL)
        .client(okHttpClientExtended)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
    val clothesApi: ClothesApi = retrofit.create(ClothesApi::class.java)
    val outfitsApi: OutfitsApi = retrofit.create(OutfitsApi::class.java)
    val recommendationsApi: RecommendationsApi = retrofitExtended.create(RecommendationsApi::class.java)
    val storeApi: StoreApi = retrofit.create(StoreApi::class.java)
    val chatApi: ChatApi = retrofit.create(ChatApi::class.java)
    val subscriptionApi: SubscriptionApi = retrofit.create(SubscriptionApi::class.java)
    val ordersApi: OrdersApi = retrofit.create(OrdersApi::class.java)
}

