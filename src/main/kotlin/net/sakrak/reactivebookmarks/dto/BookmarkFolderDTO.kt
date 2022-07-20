package net.sakrak.reactivebookmarks.dto

data class BookmarkFolderDTO(
    var id: String? = null,
    var parentId: String? = null,
    var title: String,
    var description: String,
    val loginUserId: String,
    val children: List<BookmarkFolderDTO> = listOf(),
    val bookmarks: List<BookmarkDTO> = listOf()
    ) {
}