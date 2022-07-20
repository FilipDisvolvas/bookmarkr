package net.sakrak.reactivebookmarks.services

import com.mongodb.client.result.UpdateResult
import net.sakrak.reactivebookmarks.domain.Bookmark
import net.sakrak.reactivebookmarks.dto.BookmarkDTO
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

interface BookmarkService {
    fun create(loginUserId: String, bookmarkFolderId: String, bookmarkDTO: BookmarkDTO): Mono<UpdateResult>
    fun update(loginUserId: String, bookmarkFolderId: String, bookmarkDTO: BookmarkDTO): Mono<UpdateResult>
    fun delete(loginUserId: String, bookmarkFolderId: String, bookmarkId: String): Mono<UpdateResult>

    companion object {
        fun bookmarkWithId(bookmark: Bookmark): Bookmark {
            val id = if (bookmark.id == null) {
                bookmark.loginUserId + "-" + UUID.randomUUID().toString()
            } else {
                bookmark.id
            }

            return bookmark.copy(id)
        }

        fun bookmarkWithId(bookmark: BookmarkDTO): BookmarkDTO {
            val id = if (bookmark.id == null) {
                bookmark.loginUserId + "-" + UUID.randomUUID().toString()
            } else {
                bookmark.id
            }

            return bookmark.copy(id)
        }


    }
}

