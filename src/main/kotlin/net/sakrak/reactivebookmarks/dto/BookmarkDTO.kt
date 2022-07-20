package net.sakrak.reactivebookmarks.dto

data class BookmarkDTO(
    var id: String? = null,
    var url: String,
    var title: String,
    var description: String,
    val loginUserId: String
) {
}