package net.sakrak.reactivebookmarks.repositories

import net.sakrak.reactivebookmarks.domain.Bookmark
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface BookmarkRepository : ReactiveMongoRepository<Bookmark, Long>, NestedBookmarksRepository {
}