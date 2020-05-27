package com.degree.gami.model

data class Email(var to: String? = null) {
    var from: String = "gami.do.not.reply@gmail.com"
    var subject: String = "Reset your password for Gami"
    var messageText: String = "<p>Hello, </p>" +
            "<p>We received a request to reset the password for the Gami " +
            "account associated with this e-mail address.</p>" +
            "<p>Please click on the link below to reset your password.</p>" +
            "<a href=\"https://www.gami.com/resetpassword/" + to + "\">www.gami.com/resetpassword</a>" +
            "<p>If you did not request to have your password reset, you can ignore this e-mail.</p>" +
            "<p>Have a nice day,</p>" +
            "<p style=\"color:#009933;\">The Gami Team</p>"
}

