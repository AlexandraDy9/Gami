package com.university.gami_android.ui.signUp

data class SignUpDao(
    var firstName: String,
    var lastName: String,
    var email: String,
    var birthday: String,
    var userName: String,
    var password: String,
    var confirmPassword: String
)