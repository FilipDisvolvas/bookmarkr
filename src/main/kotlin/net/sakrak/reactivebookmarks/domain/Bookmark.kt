package net.sakrak.reactivebookmarks.domain

import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Bookmark(
    var id: String? = null,

    var url: String,

    @Indexed(background = true)
    var domainName: String? = null, // TLD + second-level-domain

    var hostName : String? = null, // hostName; für eine bessere Sortiermöglichkeit in umgekehrter Schreibweise

    var title: String,

    var description: String,

    var loginUserId: String
)