package net.sakrak.reactivebookmarks.converters

import net.sakrak.reactivebookmarks.domain.Bookmark
import net.sakrak.reactivebookmarks.dto.BookmarkDTO

object BookmarkConverter {
    fun bookmarkToDTO(bookmark: Bookmark) = BookmarkDTO(
        id = bookmark.id,
        url = bookmark.url,
        title = bookmark.title,
        description = bookmark.description,
        loginUserId = bookmark.loginUserId
    )

    fun dtoToBookmark(dto: BookmarkDTO) = Bookmark(
        id = dto.id,
        url = dto.url,
        title = dto.title,
        description = dto.description,
        loginUserId = dto.loginUserId
    )
}