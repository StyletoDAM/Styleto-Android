package tn.esprit.labasniandroid.models

import com.google.gson.annotations.SerializedName

data class SigninResponse(
    @SerializedName("user")
    val user: User,
    @SerializedName("access_token")
    val accessToken: String
)

