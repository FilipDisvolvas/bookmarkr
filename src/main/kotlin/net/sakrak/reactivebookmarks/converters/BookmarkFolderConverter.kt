package net.sakrak.reactivebookmarks.converters

import net.sakrak.reactivebookmarks.converters.BookmarkConverter.bookmarkToDTO
import net.sakrak.reactivebookmarks.converters.BookmarkConverter.dtoToBookmark
import net.sakrak.reactivebookmarks.domain.BookmarkFolder
import net.sakrak.reactivebookmarks.dto.BookmarkDTO
import net.sakrak.reactivebookmarks.dto.BookmarkFolderDTO

object BookmarkFolderConverter {
    fun bookmarkFolderToDTO(bookmarkFolder: BookmarkFolder) = BookmarkFolderDTO(
        id = bookmarkFolder.id,
        parentId = bookmarkFolder.parentId,
        title = bookmarkFolder.title,
        description = bookmarkFolder.description,
        loginUserId = bookmarkFolder.loginUserId,
        bookmarks = bookmarkFolder.bookmarks.map { bookmarkToDTO(it) }
    )

    fun bookmarkFolderToDTO(
        bookmarkFolder: BookmarkFolder,
        children: List<BookmarkFolderDTO>,
        bookmarks: List<BookmarkDTO>
    ) = BookmarkFolderDTO(
        id = bookmarkFolder.id,
        parentId = bookmarkFolder.parentId,
        title = bookmarkFolder.title,
        description = bookmarkFolder.description,
        loginUserId = bookmarkFolder.loginUserId,
        children = children,
        bookmarks = bookmarks
    )


    fun dtoToBookmarkFolder(dto: BookmarkFolderDTO) = BookmarkFolder(
        id = dto.id,
        parentId = dto.parentId,
        title = dto.title,
        description = dto.description,
        loginUserId = dto.loginUserId,
        bookmarks = dto.bookmarks.map { dtoToBookmark(it) }
    )
}