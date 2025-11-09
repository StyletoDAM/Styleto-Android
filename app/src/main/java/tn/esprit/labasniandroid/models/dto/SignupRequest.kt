package tn.esprit.labasniandroid.models.dto

import com.google.gson.annotations.SerializedName
import tn.esprit.labasniandroid.models.User

data class SignupRequest(
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("gender")
    val gender: User.Gender,
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    @SerializedName("preferences")
    val preferences: List<String>? = null
)

