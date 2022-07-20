package net.sakrak.reactivebookmarks.repositories

import net.sakrak.reactivebookmarks.domain.BookmarkFolder
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface BookmarkFolderRepository : ReactiveMongoRepository<BookmarkFolder, String> {
    fun findByLoginUserIdAndId(loginUser: String, folderId: String) : Flux<BookmarkFolder>
    fun findByLoginUserIdAndParentId(loginUser: String, parent: String) : Flux<BookmarkFolder>
    fun findByLoginUserIdAndParentIdIsNull(loginUserId: String) : Flux<BookmarkFolder>
    fun existsByLoginUserIdAndId(loginUserId: String, folderId: String) : Mono<Boolean>
}