package net.sakrak.reactivebookmarks.repositories

import net.sakrak.reactivebookmarks.domain.LoginUser
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface LoginUserRepository : ReactiveMongoRepository<LoginUser, String> {
    fun findByIdAndIsActiveIsTrue(id: String) : Mono<LoginUser>
    fun findByEmailAndIsActiveIsTrue(email: String) : Mono<LoginUser>
    fun findByEmail(email: String) : Mono<LoginUser>
    fun findByEmailAndIsActiveIsTrueAndPwhash(email: String, pwhash: String) : Mono<LoginUser>
    fun findByEmailAndIsActiveIsFalseAndVerificationToken(email: String, verificationToken: String) : Mono<LoginUser>
}
