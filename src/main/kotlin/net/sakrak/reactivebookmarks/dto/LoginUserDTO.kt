package net.sakrak.reactivebookmarks.dto

import java.time.LocalDateTime

data class LoginUserDTO(
    var id: String? = null,
    var email: String,
    var pwhash: String,
    var pwsalt: String,
    var isActive: Boolean = true,
    var verificationToken: String? = null,
    var nextLoginAfter: LocalDateTime? = null
) {
}