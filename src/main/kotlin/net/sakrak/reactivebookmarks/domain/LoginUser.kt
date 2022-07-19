package net.sakrak.reactivebookmarks.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class LoginUser(
    @Id
    var id: String? = null,

    @Indexed(unique=true)
    var email: String,
    var pwhash: String,
    var pwsalt: String,
    var isActive: Boolean = true,
    var verificationToken: String?,
    var nextLoginAfter: LocalDateTime? = null
)
