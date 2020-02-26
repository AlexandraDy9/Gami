package com.degree.gami.model

import java.time.LocalDate

data class UserDao (
        var user: String,
        var pass: String,
        var firstname: String,
        var lastname: String,
        var eMail: String?,
        var description: String,
        var birthdate: LocalDate?
)