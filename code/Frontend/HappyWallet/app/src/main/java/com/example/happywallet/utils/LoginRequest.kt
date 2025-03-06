package com.example.happywallet.utils

//Import
import com.google.gson.annotations.SerializedName

//Data class for login details, Serialized used to ensure correct JSON extraction
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)