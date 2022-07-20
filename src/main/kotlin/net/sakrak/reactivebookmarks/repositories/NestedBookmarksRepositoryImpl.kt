package net.sakrak.reactivebookmarks.repositories

import com.mongodb.BasicDBObject
import com.mongodb.client.result.UpdateResult
import net.sakrak.reactivebookmarks.domain.Bookmark
import net.sakrak.reactivebookmarks.domain.BookmarkFolder
import net.sakrak.reactivebookmarks.exceptions.BookmarkFolderNotFoundException
import net.sakrak.reactivebookmarks.exceptions.BookmarkNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.function.Consumer


/**
 * Kümmert sich um die Verwaltung von @{link Bookmark} als Subdocuments von @{link BookmarkFolder}.
 *
 * Wird über Spring Magic injected, wenn man eine Instanz von @{link BookmarkRepository} anfordert,
 * weil @{link BookmarkRepository} auch @{link NestedBookmarksRepository} implementiert.
 */
class NestedBookmarksRepositoryImpl(@Autowired private val mongoTemplate: ReactiveMongoTemplate) :
    NestedBookmarksRepository {
    // https://dev.to/iuriimednikov/how-to-build-custom-queries-with-spring-data-reactive-mongodb-1802
    override fun insertByUserIdAndBookmarkFolderId(userId: String, bookmarkFolderId: String, bookmark: Bookmark): Mono<UpdateResult> {
        // step 1
        val bookmarkFolderCriteria = Criteria.where("id").`is`(bookmarkFolderId)
        val userIdCriteria = Criteria.where("loginUserId").`is`(userId)
        val query = Query(bookmarkFolderCriteria.andOperator(userIdCriteria))

        // step 2
        val update: Update = Update().push("bookmarks", bookmark)

        // step 3
        return mongoTemplate
            .updateFirst(query, update, BookmarkFolder::class.java)
    }

    override fun deleteByUserIdBookmarkFolderIdAndBookmarkId(userId: String, bookmarkFolderId: String, bookmarkId: String): Mono<UpdateResult> {
        /*
            db.bookmarkFolder.update(
                {"_id": ObjectId("62d06c6b2c22540a66f96c22")},
                {"$pull":{"bookmarks":{"_id":"62d06c6b2c22540a66f96c20-79ec91a3-a2e1-4bde-a5a1-b73318a646ed"}}}
            );
         */

        // step 1
        val bookmarkFolderCriteria = Criteria.where("id").`is`(bookmarkFolderId)
        val userIdCriteria = Criteria.where("loginUserId").`is`(userId)
        val query = Query(bookmarkFolderCriteria.andOperator(userIdCriteria))

        // step 2
        val update: Update = Update().pull("bookmarks", BasicDBObject("id", bookmarkId))

        // step 3
        return mongoTemplate
            .updateFirst(query, update, BookmarkFolder::class.java)
    }

    override fun updateUserUserIdAndBookmarkFolderIdAndBookmarkId(
        userId: String, bookmarkFolderId: String, bookmarkId: String,
        bookmark: Bookmark
    ): Mono<UpdateResult> {
        // step 1
        val bookmarkFolderCriteria = Criteria.where("id").`is`(bookmarkFolderId)
        val userIdCriteria = Criteria.where("loginUserId").`is`(userId)
        val bookmarkCriteria = Criteria.where("bookmarks._id").`is`(bookmarkId)
        val query = Query(bookmarkFolderCriteria.andOperator(userIdCriteria, bookmarkCriteria))

        // step 2
        val update = Update().set("bookmarks.\$", bookmark)

        // step 3
        return mongoTemplate
            .updateFirst(query, update, BookmarkFolder::class.java)
    }
}