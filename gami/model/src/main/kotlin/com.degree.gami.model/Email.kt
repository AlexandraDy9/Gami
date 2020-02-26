package com.degree.gami.model

data class Email(var to: String? = null) {
    var from: String = "hobi.do.not.reply@gmail.com"
    var subject: String = "Reset your password for Hobi"
    var messageText: String = "Hello,\n\n\n" +
            "We received a request to reset the password for the Hobi account associated with this e-mail address.\n\n" +
            "Please click on the link below to reset your password.\n" +
            "https://www.hobi.com/resetpassword/" + to + "\n\n\n" +
            " If you did not request to have your password reset, you can ignore this e-mail.\n\n\n" +
            "Have a nice day,\n" +
            "The Hobi Team"
}