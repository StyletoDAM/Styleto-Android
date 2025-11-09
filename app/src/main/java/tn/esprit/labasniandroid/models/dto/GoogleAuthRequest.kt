package tn.esprit.labasniandroid.models.dto

import com.google.gson.annotations.SerializedName

data class GoogleAuthRequest(
    @SerializedName("googleId")
    val googleId: String,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("profilePicture")
    val profilePicture: String? = null,
    @SerializedName("gender")
    val gender: String? = null
)

