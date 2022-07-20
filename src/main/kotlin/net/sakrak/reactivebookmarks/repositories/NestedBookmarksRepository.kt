package net.sakrak.reactivebookmarks.repositories

import com.mongodb.client.result.UpdateResult
import net.sakrak.reactivebookmarks.domain.Bookmark
import net.sakrak.reactivebookmarks.domain.BookmarkFolder
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

interface NestedBookmarksRepository {
    // https://dev.to/iuriimednikov/how-to-build-custom-queries-with-spring-data-reactive-mongodb-1802
    fun insertByUserIdAndBookmarkFolderId(userId: String, bookmarkFolderId: String, bookmark: Bookmark): Mono<UpdateResult>
    fun deleteByUserIdBookmarkFolderIdAndBookmarkId(userId: String, bookmarkFolderId: String, bookmarkId: String): Mono<UpdateResult>
    fun updateUserUserIdAndBookmarkFolderIdAndBookmarkId(userId: String, bookmarkFolderId: String, bookmarkId: String, bookmark: Bookmark): Mono<UpdateResult>
}