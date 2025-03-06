package com.example.happywallet.models

//Imports
import com.google.gson.annotations.SerializedName

//User Model
data class User(
    val id: Int?,
    @SerializedName("email") val email: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("date_of_birth") val dateOfBirth: String,
    val password: String
)