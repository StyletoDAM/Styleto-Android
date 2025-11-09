package tn.esprit.labasniandroid.models.dto

import com.google.gson.annotations.SerializedName

data class AppleAuthRequest(
    @SerializedName("appleId")
    val appleId: String,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("profilePicture")
    val profilePicture: String? = null,
    @SerializedName("gender")
    val gender: String? = null
)

