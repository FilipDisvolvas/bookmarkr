package net.sakrak.reactivebookmarks.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class BookmarkFolder(
    @Id
    var id: String? = null,

    var parentId: String? = null,

    var title: String,

    var description: String,

    val loginUserId: String,

    val bookmarks: List<Bookmark> = listOf()
)