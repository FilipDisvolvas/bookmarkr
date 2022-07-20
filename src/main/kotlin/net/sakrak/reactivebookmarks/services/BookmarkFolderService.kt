package net.sakrak.reactivebookmarks.services

import net.sakrak.reactivebookmarks.domain.Bookmark
import net.sakrak.reactivebookmarks.dto.BookmarkFolderDTO
import net.sakrak.reactivebookmarks.dto.LoginUserDTO
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface BookmarkFolderService {
    fun findRootFolders(loginUserDTO: LoginUserDTO): Flux<BookmarkFolderDTO>
    fun findFolderById(loginUserDTO: LoginUserDTO, folderId: String): Mono<BookmarkFolderDTO>
    fun create(loginUserDTO: LoginUserDTO, bookmarkFolderDTO: BookmarkFolderDTO): Mono<BookmarkFolderDTO>
    fun createRecursive(loginUserDTO: LoginUserDTO, bookmarkFolderDTO: BookmarkFolderDTO): Mono<BookmarkFolderDTO>
    fun update(loginUserDTO: LoginUserDTO, bookmarkFolderDTO: BookmarkFolderDTO): Mono<BookmarkFolderDTO>
    fun sortedBookmarks(bookmarks: List<Bookmark>): List<Bookmark>
}