package tn.esprit.labasniandroid.models.entities

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("_id")
    val mongoId: String? = null,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("gender")
    val gender: Gender,
    @SerializedName("preferences")
    val preferences: List<String>? = emptyList(),
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    @SerializedName("authProvider")
    val authProvider: AuthProvider? = null,
    @SerializedName("googleId")
    val googleId: String? = null,
    @SerializedName("appleId")
    val appleId: String? = null,
    @SerializedName("profilePicture")
    val profilePicture: String? = null
) {
    val userId: String
        get() = id ?: mongoId ?: ""

    enum class Gender(val value: String) {
        @SerializedName("male")
        MALE("male"),
        @SerializedName("female")
        FEMALE("female")
    }

    enum class AuthProvider(val value: String) {
        @SerializedName("local")
        LOCAL("local"),
        @SerializedName("google")
        GOOGLE("google"),
        @SerializedName("apple")
        APPLE("apple")
    }
}

