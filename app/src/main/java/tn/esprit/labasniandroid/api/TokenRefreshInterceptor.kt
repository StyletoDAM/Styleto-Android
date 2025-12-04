package tn.esprit.labasniandroid.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tn.esprit.labasniandroid.utils.APIConstants
import tn.esprit.labasniandroid.utils.TokenManager
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * ✨ NOUVEAU : Intercepteur pour rafraîchir automatiquement le token quand il expire (401)
 */
class TokenRefreshInterceptor(private val context: Context) : Interceptor {
    
    private val refreshApi: AuthApi by lazy {
        // Créer un Retrofit sans intercepteur de refresh pour éviter les boucles infinies
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        
        Retrofit.Builder()
            .baseUrl(APIConstants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
    
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        
        // ✨ Si on reçoit un 401, essayer de rafraîchir le token
        if (response.code == 401) {
            val responseBody = response.body
            response.close() // Fermer la réponse actuelle
            
            val refreshToken = TokenManager.getRefreshToken(context)
            if (refreshToken != null) {
                try {
                    // ✨ Utiliser runBlocking pour appeler la fonction suspend
                    val tokens = kotlinx.coroutines.runBlocking {
                        refreshApi.refreshToken(RefreshTokenRequest(refreshToken))
                    }
                    
                    if (tokens.isSuccessful && tokens.body() != null) {
                        val tokenResponse = tokens.body()!!
                        
                        // Sauvegarder les nouveaux tokens
                        TokenManager.saveToken(context, tokenResponse.accessToken)
                        TokenManager.saveRefreshToken(context, tokenResponse.refreshToken)
                        
                        // ✨ Réessayer la requête originale avec le nouveau token
                        val newRequest = request.newBuilder()
                            .header("Authorization", "Bearer ${tokenResponse.accessToken}")
                            .build()
                        
                        return chain.proceed(newRequest)
                    } else {
                        // Refresh échoué, déconnecter l'utilisateur
                        TokenManager.clearToken(context)
                        // Retourner une nouvelle réponse 401
                        return response.newBuilder()
                            .code(401)
                            .message("Unauthorized")
                            .body(responseBody)
                            .build()
                    }
                } catch (e: Exception) {
                    // Erreur lors du refresh, déconnecter l'utilisateur
                    TokenManager.clearToken(context)
                    // Retourner une nouvelle réponse 401
                    return response.newBuilder()
                        .code(401)
                        .message("Unauthorized")
                        .body(responseBody)
                        .build()
                }
            } else {
                // Pas de refresh token, déconnecter l'utilisateur
                TokenManager.clearToken(context)
                // Retourner une nouvelle réponse 401
                return response.newBuilder()
                    .code(401)
                    .message("Unauthorized")
                    .body(responseBody)
                    .build()
            }
        }
        
        return response
    }
}

