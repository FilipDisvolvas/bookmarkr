package net.sakrak.reactivebookmarks.services

import com.mongodb.client.result.UpdateResult
import net.sakrak.reactivebookmarks.converters.BookmarkConverter.dtoToBookmark
import net.sakrak.reactivebookmarks.dto.BookmarkDTO
import net.sakrak.reactivebookmarks.exceptions.BookmarkFolderNotFoundException
import net.sakrak.reactivebookmarks.exceptions.BookmarkNotFoundException
import net.sakrak.reactivebookmarks.repositories.BookmarkRepository
import net.sakrak.reactivebookmarks.repositories.NestedBookmarksRepository
import net.sakrak.reactivebookmarks.services.BookmarkService.Companion.bookmarkWithId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.net.URL
import java.util.function.Consumer

@Service
class BookmarkServiceImpl(@Autowired private val bookmarkRepository: BookmarkRepository) : BookmarkService {
    override fun create(loginUserId: String, bookmarkFolderId: String, bookmarkDTO: BookmarkDTO): Mono<UpdateResult> {
        return bookmarkRepository
            .insertByUserIdAndBookmarkFolderId(
                loginUserId,
                bookmarkFolderId,
                bookmarkWithId(dtoToBookmark(bookmarkDTO).copy(loginUserId = loginUserId)))
            .doOnSuccess(buildUpdateHandler(loginUserId, bookmarkFolderId))
    }

    override fun update(loginUserId: String, bookmarkFolderId: String, bookmarkDTO: BookmarkDTO): Mono<UpdateResult> {
        return bookmarkRepository
            .updateUserUserIdAndBookmarkFolderIdAndBookmarkId(
                loginUserId,
                bookmarkFolderId,
                bookmarkDTO.id!!,
                dtoToBookmark(bookmarkDTO).copy(loginUserId = loginUserId))
            .doOnSuccess(buildUpdateHandler(loginUserId, bookmarkFolderId))
    }

    override fun delete(loginUserId: String, bookmarkFolderId: String, bookmarkId: String): Mono<UpdateResult> {
        return bookmarkRepository
            .deleteByUserIdBookmarkFolderIdAndBookmarkId(
                loginUserId,
                bookmarkFolderId,
                bookmarkId)
            .doOnSuccess(buildUpdateHandler(loginUserId, bookmarkFolderId))
    }

    private fun buildUpdateHandler(userId: String, bookmarkFolderId: String, bookmarkId: String? = null): Consumer<UpdateResult> = Consumer {
        val message = if (bookmarkId == null) {
            "userId: \"$userId\", bookmarkFolderId: \"$bookmarkFolderId\""
        } else {
            "userId: \"$userId\", bookmarkFolderId: \"$bookmarkFolderId\", bookmarkId: \"$bookmarkId\""
        }

        if (it.matchedCount == 0L) {
            throw BookmarkFolderNotFoundException(message)
        } else if (it.modifiedCount == 0L) {
            throw BookmarkNotFoundException(message)
        }
    }
}